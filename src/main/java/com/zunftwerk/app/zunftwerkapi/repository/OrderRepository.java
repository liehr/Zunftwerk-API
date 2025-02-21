package com.zunftwerk.app.zunftwerkapi.repository;

import com.zunftwerk.app.zunftwerkapi.model.Order;
import com.zunftwerk.app.zunftwerkapi.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<List<Order>> findByOrganization(Organization organization);

    long countByOrganization(Organization organization);
}
