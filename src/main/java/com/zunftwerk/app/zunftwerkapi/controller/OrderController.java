package com.zunftwerk.app.zunftwerkapi.controller;

import com.zunftwerk.app.zunftwerkapi.dto.request.order.CreateOrderRequest;
import com.zunftwerk.app.zunftwerkapi.dto.response.order.OrderResponse;
import com.zunftwerk.app.zunftwerkapi.exception.LimitReachedException;
import com.zunftwerk.app.zunftwerkapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@CrossOrigin
@PreAuthorize("authentication.modules.contains('ORDER_MANAGEMENT')")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ExceptionHandler(LimitReachedException.class)
    public ResponseEntity<String> handleLimitReachedException(LimitReachedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}
