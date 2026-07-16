package com.snapBuy.cart.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapBuy.cart.dto.request.AddToCartRequest;
import com.snapBuy.cart.dto.request.UpdateCartItemRequest;
import com.snapBuy.cart.dto.response.CartItemResponse;
import com.snapBuy.cart.dto.response.CartResponse;
import com.snapBuy.cart.entity.Cart;
import com.snapBuy.cart.entity.CartItem;
import com.snapBuy.cart.repository.CartItemRepository;
import com.snapBuy.cart.repository.CartRepository;
import com.snapBuy.cart.service.CartService;
import com.snapBuy.exception.ForbiddenException;
import com.snapBuy.exception.ResourceNotFoundException;
import com.snapBuy.product.entity.Product;
import com.snapBuy.product.repository.ProductRepository;
import com.snapBuy.user.User;
import com.snapBuy.user.UserRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public CartResponse getCart(Long customerId) {
        return toResponse(getOrCreateCart(customerId));
    }

    @Override
    @Transactional
    public CartResponse addToCart(Long customerId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(customerId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.isVisibleToCustomers()) {
            throw new ResourceNotFoundException("Product not found");
        }

        CartItem existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);

        if (existing != null) {
            int newQuantity = existing.getQuantity() + request.getQuantity();
            validateStock(product, newQuantity);
            existing.setQuantity(newQuantity);
            cartItemRepository.save(existing);
        } else {
            validateStock(product, request.getQuantity());
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(item);
            cart.getItems().add(item);
        }

        return toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(Long customerId, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(customerId);
        CartItem item = findOwnedItem(cart, itemId);

        validateStock(item.getProduct(), request.getQuantity());
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long customerId, Long itemId) {
        Cart cart = getOrCreateCart(customerId);
        CartItem item = findOwnedItem(cart, itemId);
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return toResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long customerId) {
        Cart cart = getOrCreateCart(customerId);
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (requestedQuantity > product.getStock()) {
            throw new IllegalArgumentException(
                    "Only " + product.getStock() + " units of \"" + product.getName() + "\" are available");
        }
    }

    private CartItem findOwnedItem(Cart cart, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ForbiddenException("You do not have access to this cart item");
        }
        return item;
    }

    private Cart getOrCreateCart(Long customerId) {
        return cartRepository.findByUserId(customerId).orElseGet(() -> {
            User user = userRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            return cartRepository.save(Cart.builder().user(user).build());
        });
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream().map(this::toItemResponse).toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = itemResponses.stream().mapToInt(CartItemResponse::getQuantity).sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalItems(totalItems)
                .totalAmount(total)
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        Product product = item.getProduct();
        String imageUrl = product.getImages().isEmpty() ? null : product.getImages().get(0).getImageUrl();
        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImageUrl(imageUrl)
                .unitPrice(product.getPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .availableStock(product.getStock())
                .exceedsAvailableStock(item.getQuantity() > product.getStock())
                .build();
    }
}