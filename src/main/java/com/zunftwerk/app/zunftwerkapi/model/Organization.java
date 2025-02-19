package com.zunftwerk.app.zunftwerkapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    // Decrypted to LocalDate
    private String subscriptionStartDate;
    // Decrypted to LocalDate
    private String subscriptionEndDate;

    // Decrypted to int
    private int additionalPurchasedUsers;

    @ManyToOne
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlan subscriptionPlan;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    // A la carte purchased modules.
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrganizationModule> organizationModules = new HashSet<>();

    // Convenience method: get effective modules (default plus purchased)
    public Set<Module> getEffectiveModules() {
        Set<Module> effectiveModules = new HashSet<>();
        if (subscriptionPlan != null) {
            effectiveModules.addAll(subscriptionPlan.getModules());
        }
        for (OrganizationModule orgModule : organizationModules) {
            effectiveModules.add(orgModule.getModule());
        }
        return effectiveModules;
    }

    // Convenience methods for managing organizationModules
    public void addOrganizationModule(OrganizationModule orgModule) {
        organizationModules.add(orgModule);
        orgModule.setOrganization(this);
    }

    public void removeOrganizationModule(OrganizationModule orgModule) {
        organizationModules.remove(orgModule);
        orgModule.setOrganization(null);
    }
}

