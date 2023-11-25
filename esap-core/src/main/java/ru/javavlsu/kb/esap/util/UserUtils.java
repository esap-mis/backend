package ru.javavlsu.kb.esap.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.model.Doctor;
import ru.javavlsu.kb.esap.model.User;
import ru.javavlsu.kb.esap.security.UserDetails;
import ru.javavlsu.kb.esap.service.DoctorService;

@Component
public class UserUtils {

    private final DoctorService doctorService;

    public UserUtils(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    public UserDetails UserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetails) authentication.getPrincipal();
    }

}
