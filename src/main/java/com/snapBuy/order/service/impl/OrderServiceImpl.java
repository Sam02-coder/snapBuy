package com.snapBuy.order.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapBuy.cart.entity.Cart;
import com.snapBuy.cart.entity.CartItem;
import com.snapBuy.cart.repository.CartItemRepository;
import com.snapBuy.cart.repository.CartRepository;
import com.snapBuy.common.enums.OrderStatus;
import com.snapBuy.customer.entity.Address;
import com.snapBuy.customer.mapper.AddressMapper;
import com.snapBuy.customer.repository.AddressRepository;
import com.snapBuy.exception.ForbiddenException;
import com.snapBuy.exception.ResourceNotFoundException;
import com.snapBuy.notification.EmailService;
import com.snapBuy.order.dto.request.CheckoutRequest;
import com.snapBuy.order.dto.response.OrderItemResponse;
import com.snapBuy.order.dto.response.OrderResponse;
import com.snapBuy.order.entity.Order;
import com.snapBuy.order.entity.OrderItem;
import com.snapBuy.order.repository.OrderItemRepository;
import com.snapBuy.order.repository.OrderRepository;
import com.snapBuy.order.service.OrderService;
import com.snapBuy.product.entity.Product;
import com.snapBuy.product.repository.ProductRepository;
import com.snapBuy.user.User;
import com.snapBuy.user.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Set<OrderStatus> CANCELLABLE_STATUSES = Set.of(OrderStatus.PENDING, OrderStatus.CONFIRMED);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    private final EmailService emailService;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    public OrderResponse checkout(Long customerId, CheckoutRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUser().getId().equals(customerId)) {
            throw new ForbiddenException("You do not have access to this address");
        }

        Cart cart = cartRepository.findByUserId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Your cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty");
        }

        // Re-validate every line against live stock/visibility - the cart may have
        // gone stale since items were added (merchant edited price/stock/approval).
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (!product.isVisibleToCustomers()) {
                throw new IllegalArgumentException(
                        "\"" + product.getName() + "\" is no longer available. Please remove it from your cart.");
            }
            if (cartItem.getQuantity() > product.getStock()) {
                throw new IllegalArgumentException(
                        "Only " + product.getStock() + " units of \"" + product.getName() + "\" are available");
            }
        }

        Order order = Order.builder()
                .customer(customer)
                .address(address)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();
        order = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .productName(product.getName())
                    .priceAtPurchase(product.getPrice())
                    .build();
            orderItemRepository.save(orderItem);
            order.getItems().add(orderItem);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            // Reserve stock immediately at order creation, not at payment confirmation -
            // prevents two customers from checking out the last unit simultaneously.
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
            evictProductCache(product.getId());
        }

        order.setTotalAmount(total);
        order = orderRepository.save(order);

        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();

        log.info("Order {} created for customer {}, total={}", order.getId(), customerId, total);
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrderHistory(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(Long customerId, Long orderId) {
        return toResponse(findOwnedOrder(customerId, orderId));
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long customerId, Long orderId) {
        Order order = findOwnedOrder(customerId, orderId);

        if (!CANCELLABLE_STATUSES.contains(order.getStatus())) {
            throw new IllegalArgumentException(
                    "Order cannot be cancelled once it has been " + order.getStatus().name().toLowerCase());
        }

        // Restock every item - the reservation made at checkout is released.
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
            evictProductCache(product.getId());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        emailService.sendOrderStatusUpdateEmail(order.getCustomer().getEmail(), order.getId(), OrderStatus.CANCELLED);

        return toResponse(order);
    }

    /**
     * Stock changes here happen via direct repository saves (checkout reserves,
     * cancellation restocks), bypassing ProductServiceImpl's @CacheEvict entirely.
     * Without this, a cached product detail response would show stale stock
     * after every purchase or cancellation.
     */
    private void evictProductCache(Long productId) {
        Cache cache = cacheManager.getCache("products");
        if (cache != null) {
            cache.evict(productId);
        }
    }

    private Order findOwnedOrder(Long customerId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new ForbiddenException("You do not have access to this order");
        }
        return order;
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(addressMapper.toResponse(order.getAddress()))
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        Product product = item.getProduct();
        String imageUrl = (product != null && !product.getImages().isEmpty())
                ? product.getImages().get(0).getImageUrl()
                : null;

        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(product != null ? product.getId() : null)
                .productName(item.getProductName())
                .productImageUrl(imageUrl)
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .subtotal(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}