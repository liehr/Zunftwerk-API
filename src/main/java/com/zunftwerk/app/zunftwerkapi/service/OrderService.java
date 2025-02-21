package com.zunftwerk.app.zunftwerkapi.service;

import com.zunftwerk.app.zunftwerkapi.dto.request.order.CreateOrderRequest;
import com.zunftwerk.app.zunftwerkapi.dto.response.order.OrderResponse;
import com.zunftwerk.app.zunftwerkapi.exception.LimitReachedException;
import com.zunftwerk.app.zunftwerkapi.exception.UnauthorizedException;
import com.zunftwerk.app.zunftwerkapi.model.Order;
import com.zunftwerk.app.zunftwerkapi.model.Organization;
import com.zunftwerk.app.zunftwerkapi.model.User;
import com.zunftwerk.app.zunftwerkapi.provider.AuthenticatedUserProvider;
import com.zunftwerk.app.zunftwerkapi.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AuthenticatedUserProvider userProvider;

    @SneakyThrows
    public OrderResponse createOrder(CreateOrderRequest request) {
        return Optional.of(getAuthenticatedUser())
                .filter(user -> {
                    Organization organization = user.getOrganization();
                    if (organization.getSubscriptionPlan() != null && organization.getSubscriptionPlan().getMaxOrders() != null) {
                        return orderRepository.countByOrganization(organization) < organization.getSubscriptionPlan().getMaxOrders();
                    }
                    return true;
                })
                .map(user -> Order.builder()
                        .description(request.description())
                        .status(request.status())
                        .organization(user.getOrganization())
                        .build())
                .map(orderRepository::save)
                .map(this::mapToOrderResponse)
                .orElseThrow(() -> new LimitReachedException("Max order limit reached for your organization."));
    }


    @SneakyThrows
    private User getAuthenticatedUser() {
        return Optional.ofNullable(userProvider.getCurrentUser())
                .orElseThrow(() -> new UnauthorizedException("User not authorized!"));
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(order.getDescription(), order.getStatus());
    }
}
