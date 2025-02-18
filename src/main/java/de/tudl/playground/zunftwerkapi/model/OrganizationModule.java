package de.tudl.playground.zunftwerkapi.model;

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
    private LocalDate startDate;
    private LocalDate endDate;

    // Optional price override for this module purchase
    private Double priceOverride;
}

