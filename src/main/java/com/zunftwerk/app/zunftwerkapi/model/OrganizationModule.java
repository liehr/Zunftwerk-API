package com.zunftwerk.app.zunftwerkapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "organization_modules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The organization that purchased the module
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    // The purchased module
    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    // Additional fields for subscription details
    // Decrypted to LocalDate
    private String startDate;
    // Decrypted to LocalDate
    private String endDate;

    // Optional price override for this module purchase
    // Decrypted to Double
    private String priceOverride;
}

