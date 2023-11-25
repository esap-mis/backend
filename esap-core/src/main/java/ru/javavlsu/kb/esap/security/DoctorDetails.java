//package ru.javavlsu.kb.esap.security;
//
//import lombok.Getter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import ru.javavlsu.kb.esap.model.Doctor;
//
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.stream.Collectors;
//
//public class DoctorDetails implements UserDetails {
//
//    @Getter
//    private final Doctor doctor;
//
//    public DoctorDetails(Doctor doctor) {
//        this.doctor = doctor;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return new HashSet<GrantedAuthority>((doctor.getRole().stream()
//                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())));
//    }
//
//    @Override
//    public String getPassword() {
//        return doctor.getPassword();
//    }
//
//    @Override
//    public String getUsername() {
//        return doctor.getLogin();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}
