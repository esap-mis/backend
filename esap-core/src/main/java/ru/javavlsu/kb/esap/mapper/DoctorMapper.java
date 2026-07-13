package ru.javavlsu.kb.esap.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.javavlsu.kb.esap.dto.DoctorDTO;
import ru.javavlsu.kb.esap.dto.DoctorResponseDTO;
import ru.javavlsu.kb.esap.dto.auth.DoctorRegistration;
import ru.javavlsu.kb.esap.exception.NotFoundException;
import ru.javavlsu.kb.esap.model.Doctor;
import ru.javavlsu.kb.esap.model.Role;
import ru.javavlsu.kb.esap.model.RoleName;
import ru.javavlsu.kb.esap.repository.RoleRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    Doctor toDoctor(DoctorDTO doctorDTO);
    DoctorDTO toDoctorDTO(Doctor doctor);
    List<DoctorDTO> toDoctorDTOList(List<Doctor> doctors);
    List<DoctorResponseDTO> toDoctorResponseDTOList(List<Doctor> doctors);
    default Page<DoctorDTO> toDoctorDTOPage(Page<Doctor> doctors) {
        return doctors.map(this::toDoctorDTO);
    }

    @Mapping(target = "role", ignore = true)
    Doctor toDoctor(DoctorRegistration doctorDTO);

    @Mapping(target = "role", source = "role")
    Doctor toDoctor(DoctorRegistration doctorDTO, @Context RoleRepository roleRepository);

    default Set<Role> map(String roleValue, @Context RoleRepository roleRepository) {
        if (roleValue == null) {
            return Collections.emptySet();
        }

        final RoleName roleName = RoleName.from(roleValue);
        final Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found"));

        return Set.of(role);
    }
}
