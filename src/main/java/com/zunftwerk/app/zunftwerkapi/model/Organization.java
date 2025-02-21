package com.zunftwerk.app.zunftwerkapi.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate subscriptionStartDate;
    private LocalDate subscriptionEndDate;
    private int additionalPurchasedUsers;

    @ManyToOne
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlan subscriptionPlan;

    @Builder.Default
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrganizationModule> organizationModules = new HashSet<>();

    // Convenience method: get effective modules (default plus purchased)
    public Set<Module> getEffectiveModules() {
        Set<Module> effectiveModules = new HashSet<>();
        if (subscriptionPlan != null) {
            effectiveModules.addAll(subscriptionPlan.getModules());
        }
        if (organizationModules != null) {
            for (OrganizationModule orgModule : organizationModules) {
                effectiveModules.add(orgModule.getModule());
            }
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
