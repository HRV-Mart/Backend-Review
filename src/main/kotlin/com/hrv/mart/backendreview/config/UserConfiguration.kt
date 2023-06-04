package com.hrv.mart.backendreview.config

import com.hrv.mart.userlibrary.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class UserConfiguration(
    @Autowired
    private val webClientBuilder: WebClient.Builder,
    @Value("\${user.url}")
    private val userUrl: String
) {
    @Bean
    fun getUserRepository() =
        UserRepository(webClientBuilder, userUrl)
}
