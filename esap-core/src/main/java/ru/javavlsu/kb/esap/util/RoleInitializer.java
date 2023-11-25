package ru.javavlsu.kb.esap.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.model.Role;
import ru.javavlsu.kb.esap.repository.RoleRepository;

import java.util.Arrays;
import java.util.List;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        List<String> roleNames = Arrays.asList("ROLE_ADMIN", "ROLE_CHIEF_DOCTOR", "ROLE_DOCTOR",
                "ROLE_REGISTRANT", "ROLE_LABORATORY", "ROLE_PATIENT");

        for (String roleName : roleNames) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = Role.builder()
                        .name(roleName)
                        .build();
                roleRepository.save(role);
            }
        }
    }
}

