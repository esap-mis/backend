//package ru.javavlsu.kb.esap.util;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import ru.javavlsu.kb.esap.security.DoctorDetails;
//
//@Component
//public class DoctorUtils {
//    public DoctorDetails getDoctorDetails(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return (DoctorDetails) authentication.getPrincipal();
//    }
//}
