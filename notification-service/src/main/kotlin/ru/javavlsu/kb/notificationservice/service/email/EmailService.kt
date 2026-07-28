package ru.javavlsu.kb.notificationservice.service.email

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    @Value("\${spring.mail.username}")
    private val email: String,
    private val emailSender: JavaMailSender,
) {
    private val log: Logger = LoggerFactory.getLogger(EmailService::class.java)

    fun sendEmail(emailContent: EmailBuilder.EmailContent) {
        val simpleMailMessage = SimpleMailMessage()
        simpleMailMessage.from = email
        simpleMailMessage.setTo(emailContent.to)
        simpleMailMessage.subject = emailContent.subject
        simpleMailMessage.text = emailContent.body

        try {
            emailSender.send(simpleMailMessage)
            log.info("Email notification sent successfully to: {token=${emailContent.to}}")
        } catch (e: MailException) {
            log.error("Error sending email notification to {token=" + emailContent.to + "} : {error=" + e.message + "}")
        }
    }
}
