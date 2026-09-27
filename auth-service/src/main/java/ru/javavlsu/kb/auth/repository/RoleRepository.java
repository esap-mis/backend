package ru.javavlsu.kb.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.auth.model.Role;
import ru.javavlsu.kb.auth.model.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);

    boolean existsByName(RoleName name);
}
