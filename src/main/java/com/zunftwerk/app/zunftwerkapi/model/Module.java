package com.zunftwerk.app.zunftwerkapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "modules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String moduleName;

    // Decrypted to a Double
    private String modulePrice;

    @ManyToMany(mappedBy = "modules")
    private Set<SubscriptionPlan> plans = new HashSet<>();

    // Convenience methods for managing organization modules
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrganizationModule> organizationModules = new HashSet<>();

    public void addOrganizationModule(OrganizationModule orgModule) {
        organizationModules.add(orgModule);
        orgModule.setModule(this);
    }

    public void removeOrganizationModule(OrganizationModule orgModule) {
        organizationModules.remove(orgModule);
        orgModule.setModule(null);
    }
}
