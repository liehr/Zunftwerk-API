package de.tudl.playground.zunftwerkapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;           // e.g., "Freemium", "Base", "Pro"
    private Double monthlyPrice;
    private Double annualPrice;
    private int includedUsers;
    private Integer maxOrders;

    // This many-to-many defines the default/included modules for the plan.
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