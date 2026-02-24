package ru.javavlsu.kb.esap.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.model.Role;
import ru.javavlsu.kb.esap.model.RoleName;
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
        for (RoleName roleName : RoleName.values()) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = Role.builder()
                        .name(roleName)
                        .build();
                roleRepository.save(role);
            }
        }
    }
}

