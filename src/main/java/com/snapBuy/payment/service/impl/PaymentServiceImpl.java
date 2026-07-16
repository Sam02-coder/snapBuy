package com.snapBuy.payment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snapBuy.common.enums.OrderStatus;
import com.snapBuy.common.enums.PaymentStatus;
import com.snapBuy.exception.ForbiddenException;
import com.snapBuy.exception.PaymentException;
import com.snapBuy.exception.ResourceNotFoundException;
import com.snapBuy.notification.EmailService;
import com.snapBuy.order.entity.Order;
import com.snapBuy.order.repository.OrderRepository;
import com.snapBuy.payment.dto.request.VerifyPaymentRequest;
import com.snapBuy.payment.dto.response.CreateRazorpayOrderResponse;
import com.snapBuy.payment.dto.response.PaymentResponse;
import com.snapBuy.payment.entity.Payment;
import com.snapBuy.payment.repository.PaymentRepository;
import com.snapBuy.payment.service.PaymentService;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final RazorpayClient razorpayClient;
    private final EmailService emailService;

    @Value("${app.razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${app.razorpay.key-secret}")
    private String razorpayKeySecret;

    @Override
    @Transactional
    public CreateRazorpayOrderResponse createRazorpayOrder(Long customerId, Long orderId) {
        Order order = findOwnedOrder(customerId, orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new PaymentException("This order is not payable in its current status: " + order.getStatus());
        }

        Payment payment = paymentRepository.findByOrderId(orderId).orElse(null);

        if (payment != null && payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new PaymentException("This order has already been paid for");
        }

        // Reuse the existing Razorpay order if the customer re-opens checkout
        // (page refresh, closed the widget) rather than creating a new one every time.
        if (payment != null && payment.getStatus() == PaymentStatus.CREATED) {
            return buildResponse(payment, order);
        }

        long amountInPaise = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValueExact();

        String razorpayOrderId;
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcpt_" + order.getId());

            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            razorpayOrderId = razorpayOrder.get("id");
        } catch (RazorpayException ex) {
            log.error("Failed to create Razorpay order for order {}: {}", orderId, ex.getMessage());
            throw new PaymentException("Unable to initiate payment. Please try again.");
        }

        if (payment == null) {
            payment = Payment.builder()
                    .order(order)
                    .razorpayOrderId(razorpayOrderId)
                    .amount(order.getTotalAmount())
                    .status(PaymentStatus.CREATED)
                    .build();
        } else {
            payment.setRazorpayOrderId(razorpayOrderId);
            payment.setStatus(PaymentStatus.CREATED);
        }
        paymentRepository.save(payment);

        return buildResponse(payment, order);
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(Long customerId, VerifyPaymentRequest request) {
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));

        if (!payment.getOrder().getCustomer().getId().equals(customerId)) {
            throw new ForbiddenException("You do not have access to this payment");
        }

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(options, razorpayKeySecret);
            if (!isValid) {
                throw new PaymentException("Payment signature verification failed");
            }
        } catch (RazorpayException ex) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.warn("Payment signature verification error for order {}: {}",
                    payment.getOrder().getId(), ex.getMessage());
            throw new PaymentException("Payment verification failed");
        }

        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        emailService.sendOrderConfirmationEmail(order.getCustomer().getEmail(), order.getId(), order.getTotalAmount());
        log.info("Payment verified and order confirmed: orderId={}", order.getId());

        return toResponse(payment);
    }

    @Override
    public Page<PaymentResponse> getPaymentHistory(Long customerId, Pageable pageable) {
        return paymentRepository.findByOrder_Customer_Id(customerId, pageable).map(this::toResponse);
    }

    private Order findOwnedOrder(Long customerId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new ForbiddenException("You do not have access to this order");
        }
        return order;
    }

    private CreateRazorpayOrderResponse buildResponse(Payment payment, Order order) {
        return CreateRazorpayOrderResponse.builder()
                .razorpayOrderId(payment.getRazorpayOrderId())
                .amountInPaise(order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValueExact())
                .currency("INR")
                .razorpayKeyId(razorpayKeyId)
                .internalOrderId(order.getId())
                .build();
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}