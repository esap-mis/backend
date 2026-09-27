package ru.javavlsu.kb.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.javavlsu.kb.auth.dto.DoctorDTO;
import ru.javavlsu.kb.auth.dto.DoctorRegistration;
import ru.javavlsu.kb.auth.dto.DoctorResponseDTO;
import ru.javavlsu.kb.auth.model.Doctor;
import ru.javavlsu.kb.auth.model.Role;
import ru.javavlsu.kb.auth.model.RoleName;
import ru.javavlsu.kb.auth.repository.RoleRepository;
import ru.javavlsu.kb.common.web.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "clinic", ignore = true)
    @Mapping(target = "login", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    Doctor toDoctor(DoctorRegistration doctorRegistration);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    Doctor toDoctor(DoctorDTO doctorDTO);

    DoctorDTO toDoctorDTO(Doctor doctor);

    List<DoctorResponseDTO> toDoctorResponseDTOList(List<Doctor> doctors);

    default Page<DoctorDTO> toDoctorDTOPage(Page<Doctor> doctors) {
        return doctors.map(this::toDoctorDTO);
    }

    /**
     * Роль приходит строкой, а хранится справочником, поэтому её подстановка
     * вынесена из MapStruct в default-метод с доступом к репозиторию.
     */
    default Doctor toDoctor(DoctorRegistration doctorRegistration, RoleRepository roleRepository) {
        Doctor doctor = toDoctor(doctorRegistration);
        doctor.setRole(resolveRoles(doctorRegistration.getRole(), roleRepository));
        return doctor;
    }

    private Set<Role> resolveRoles(String role, RoleRepository roleRepository) {
        if (role == null || role.isBlank()) {
            return Collections.emptySet();
        }
        RoleName roleName = RoleName.from(role);
        return Set.of(roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleName)));
    }
}
