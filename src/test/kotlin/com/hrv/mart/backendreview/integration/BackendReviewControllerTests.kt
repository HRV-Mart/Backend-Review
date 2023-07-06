package com.hrv.mart.backendreview.integration

import com.hrv.mart.backendreview.repository.ReviewRepository
import io.restassured.RestAssured
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BackendReviewControllerTests(
    @LocalServerPort
    private val port: Int,

    @Autowired
    private val reviewRepository: ReviewRepository
) {

    @BeforeEach
    fun setUp() {
        RestAssured.basePath = "http://localhost:$port"
        reviewRepository
            .deleteAll()
            .block()
    }

    @Test
    fun `should create product and return a successful message`() {
        println("Ohh yes..")
    }
    companion object Factory {
        private val mongoDb = MongoDBContainer("mongo:latest")

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            mongoDb.addExposedPort(27_017)
            mongoDb.start()
        }

        @JvmStatic
        @AfterAll
        fun afterAll() =
            mongoDb.stop()

        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri", mongoDb::getConnectionString)
        }
    }
}
