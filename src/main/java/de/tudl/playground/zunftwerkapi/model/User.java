package de.tudl.playground.zunftwerkapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owning side of the relationship
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    private String email;
    private String password;
    private String role;
}
