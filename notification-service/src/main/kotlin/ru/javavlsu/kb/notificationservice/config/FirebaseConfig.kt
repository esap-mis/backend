package ru.javavlsu.kb.notificationservice.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
class FirebaseConfig {

    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        val googleCredentials = GoogleCredentials
            .fromStream(ClassPathResource("firebase-service-account.json").inputStream)

        val options = FirebaseOptions.builder()
            .setCredentials(googleCredentials)
            .build()

        val app = FirebaseApp.initializeApp(options, "notification-service")
        return FirebaseMessaging.getInstance(app)
    }
}