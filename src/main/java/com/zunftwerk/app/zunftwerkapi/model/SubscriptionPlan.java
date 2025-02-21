package com.zunftwerk.app.zunftwerkapi.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "modules")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;  // e.g., "Freemium", "Base", "Pro"

    @Column(nullable = false)
    private Double monthlyPrice;

    @Column(nullable = false)
    private Double annualPrice;

    @Column(nullable = false)
    private Integer includedUsers;

    private Integer maxOrders;

    @Builder.Default
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "plan_modules",
            joinColumns = @JoinColumn(name = "subscription_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "module_id")
    )
    private Set<Module> modules = new HashSet<>();

    // Convenience methods to manage the relationship.
    public void addModule(Module module) {
        this.modules.add(module);
        module.getPlans().add(this);
    }

    public void removeModule(Module module) {
        this.modules.remove(module);
        module.getPlans().remove(this);
    }
}
