package com.zunftwerk.app.zunftwerkapi.repository;

import com.zunftwerk.app.zunftwerkapi.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
