package com.zunftwerk.app.zunftwerkapi.service;

import com.zunftwerk.app.zunftwerkapi.dto.request.organization.CreateOrganizationRequest;
import com.zunftwerk.app.zunftwerkapi.dto.response.organization.OrganizationRegistrationResponse;
import com.zunftwerk.app.zunftwerkapi.model.Module;
import com.zunftwerk.app.zunftwerkapi.model.Organization;
import com.zunftwerk.app.zunftwerkapi.model.OrganizationModule;
import com.zunftwerk.app.zunftwerkapi.model.SubscriptionPlan;
import com.zunftwerk.app.zunftwerkapi.model.User;
import com.zunftwerk.app.zunftwerkapi.repository.ModuleRepository;
import com.zunftwerk.app.zunftwerkapi.repository.OrganizationRepository;
import com.zunftwerk.app.zunftwerkapi.repository.SubscriptionPlanRepository;
import com.zunftwerk.app.zunftwerkapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ModuleRepository moduleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    public OrganizationRegistrationResponse createOrganizationAndAdminAccount(CreateOrganizationRequest request) {
        SubscriptionPlan freemiumPlan = subscriptionPlanRepository.findByName("Freemium")
                .orElseThrow(() -> new RuntimeException("Freemium-Plan nicht gefunden"));

        Module orderModule = moduleRepository.findByModuleName("ORDER_MANAGEMENT")
                .orElseThrow(() -> new RuntimeException("ORDER_MANAGEMENT Modul nicht gefunden"));
        Organization organization = Organization.builder()
                .subscriptionPlan(freemiumPlan)
                .name(request.organizationName())
                .subscriptionStartDate(LocalDate.now())
                .subscriptionEndDate(null)
                .build();
        organization = organizationRepository.save(organization);

        OrganizationModule organizationModule = OrganizationModule.builder()
                .organization(organization)
                .module(orderModule)
                .startDate(LocalDate.now())
                .endDate(null)
                .priceOverride(0.0)
                .build();
        organization.addOrganizationModule(organizationModule);
        organization = organizationRepository.save(organization);

        User adminUser = User.builder()
                .email(request.adminEmail())
                .password(bCryptPasswordEncoder.encode(request.adminPassword()))
                .organization(organization)
                .role("ROLE_ADMIN")
                .build();
        adminUser = userRepository.save(adminUser);

        List<String> roles = List.of("ROLE_ADMIN");
        List<String> modules = organization.getEffectiveModules()
                .stream()
                .map(Module::getModuleName)
                .toList();
        String jwtToken = jwtService.generateToken(adminUser.getEmail(), organization.getId(), roles, modules);

        return new OrganizationRegistrationResponse(
                organization.getId(),
                organization.getName(),
                freemiumPlan.getName(),
                jwtToken,
                adminUser.getEmail()
        );
    }
}