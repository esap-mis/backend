package ru.javavlsu.kb.esap.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.javavlsu.kb.esap.dto.EmailRequest;
import ru.javavlsu.kb.esap.exception.EmailSendingException;
import ru.javavlsu.kb.esap.model.Patient;

@Service
public class EmailService {
    public final RestTemplate restTemplate;

    @Value("${emailServiceUrl}")
    private String emailServiceUrl;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendUserData(Patient patient) {
        String emailSubject = "Добро пожаловать в нашу клинику";
        String emailMessage = "Уважаемый " + patient.getFirstName()
                + "!\nВысылаем вам данные для личного кабинета"
                + "\nЛогин: " + patient.getLogin() + "\nПароль: " + patient.getPassword()
                + "\n\nС уважением, поликлиника \"" + patient.getClinic().getName() + "\".";

        EmailRequest emailRequest = new EmailRequest(patient.getEmail(), emailSubject, emailMessage);

        try {
            restTemplate.postForEntity(emailServiceUrl, emailRequest, String.class);
        } catch (EmailSendingException mailException) {
            throw new EmailSendingException("Unable to send email");
        }
    }
}
