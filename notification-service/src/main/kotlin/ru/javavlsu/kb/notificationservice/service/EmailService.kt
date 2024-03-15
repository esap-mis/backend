package ru.javavlsu.kb.notificationservice.service

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

    fun sendEmail(toAddress: String, subject: String?, message: String?) {
        val simpleMailMessage = SimpleMailMessage()
        simpleMailMessage.from = email
        simpleMailMessage.setTo(toAddress)
        simpleMailMessage.subject = subject
        simpleMailMessage.text = message

        try {
            emailSender.send(simpleMailMessage)
            log.info("Email notification sent successfully to: {token=$toAddress}")
        } catch (e: MailException) {
            log.error("Error sending email notification to {token=" + toAddress + "} : {error=" + e.message + "}")
        }
    }
}
