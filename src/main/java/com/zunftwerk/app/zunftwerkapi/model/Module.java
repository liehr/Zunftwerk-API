package com.zunftwerk.app.zunftwerkapi.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "modules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"plans", "organizationModules"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String moduleName;

    @Column(nullable = false)
    private Double modulePrice;

    @Builder.Default
    @ManyToMany(mappedBy = "modules")
    private Set<SubscriptionPlan> plans = new HashSet<>();

    @Builder.Default
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
