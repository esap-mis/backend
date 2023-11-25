package ru.javavlsu.kb.esap.dto.auth;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistration {

    private Long id;

    private String password;

    private String login;

    private String role;

}
