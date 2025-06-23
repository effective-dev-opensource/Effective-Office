package band.effective.office.backend.feature.notifications.config

import band.effective.office.backend.feature.notifications.service.FcmNotificationSender
import band.effective.office.backend.feature.notifications.service.INotificationSender
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FirebaseConfig(
    val resourceLoader: org.springframework.core.io.ResourceLoader,
) {

    @Value("\${calendar.subscription.firebase-credentials}")
    private lateinit var firebaseCredentials: String

    @Bean
    fun objectMapper(): ObjectMapper {
        println("[firebaseCredentials] finded: $firebaseCredentials")
        return ObjectMapper()
            .registerModule(com.fasterxml.jackson.module.kotlin.KotlinModule.Builder().build())
            .registerModule(com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Bean
    fun firebaseOptions(): FirebaseOptions {
        val inputStream = resourceLoader.getResource(firebaseCredentials).inputStream
        return FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(inputStream))
            .build()
    }

    @Bean
    fun firebaseApp(options: FirebaseOptions): FirebaseApp {
        return if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        } else {
            FirebaseApp.getInstance()
        }
    }

    @Bean
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(firebaseApp)
    }

    @Bean
    fun notificationSender(firebaseMessaging: FirebaseMessaging): INotificationSender {
        return FcmNotificationSender(firebaseMessaging)
    }
}
