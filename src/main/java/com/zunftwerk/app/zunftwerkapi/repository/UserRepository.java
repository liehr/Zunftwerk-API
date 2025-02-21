package com.zunftwerk.app.zunftwerkapi.repository;

import com.zunftwerk.app.zunftwerkapi.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {
            "organization",
            "organization.subscriptionPlan",
            "organization.subscriptionPlan.modules",
            "organization.organizationModules",
            "organization.organizationModules.module"
    })
    Optional<User> findUserByEmail(String email);
}
