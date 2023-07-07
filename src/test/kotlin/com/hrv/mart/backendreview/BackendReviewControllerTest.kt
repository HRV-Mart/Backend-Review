package com.hrv.mart.backendreview

import com.hrv.mart.backendreview.controller.ReviewController
import com.hrv.mart.backendreview.fixture.ReviewFixture.allReviews
import com.hrv.mart.backendreview.fixture.ReviewFixture.allUsers
import com.hrv.mart.backendreview.model.ReviewResponse
import com.hrv.mart.backendreview.repository.ReviewRepository
import com.hrv.mart.backendreview.service.ReviewService
import com.hrv.mart.custompageable.model.Pageable
import com.hrv.mart.userlibrary.repository.UserRepository
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.testcontainers.containers.MongoDBContainer
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@DataMongoTest
class BackendReviewControllerTest(
    @Autowired
    private val reviewRepository: ReviewRepository
) {
    private val response = mock(ServerHttpResponse::class.java)
    private val userRepository = mock(UserRepository::class.java)
    private val reviewService = ReviewService(reviewRepository, userRepository)
    private val reviewController = ReviewController(reviewService)

    @BeforeEach
    fun cleanDataBase() {
        reviewRepository
            .deleteAll()
            .subscribe()
    }

    @Test
    fun `should create product and return a successful message`() {
        val review = allReviews.random()
        StepVerifier.create(
            reviewController
                .createReview(
                    review,
                    response
                )
        )
            .expectNext("Review created successfully")
            .verifyComplete()
    }

    @Test
    fun `should not add review in database if it already exist`() {
        val review = allReviews.random()
        reviewRepository
            .insert(review)
            .subscribe()
        StepVerifier.create(
            reviewController
                .createReview(
                    review,
                    response
                )
        )
            .expectNext("Review already exist")
            .verifyComplete()
    }

    @Test
    fun `should delete review from database if it exist`() {
        val review = allReviews.random()
        reviewRepository
            .insert(review)
            .subscribe()
        StepVerifier.create(
            reviewController.deleteReview(
                review.userId,
                review.productId,
                response
            )
        )
            .expectNext("Review deleted successfully")
            .verifyComplete()
    }

    @Test
    fun `should not delete review from database if it does not  exist`() {
        val review = allReviews.random()
        StepVerifier.create(
            reviewController.deleteReview(
                review.userId,
                review.productId,
                response
            )
        )
            .expectNext("Review not found")
            .verifyComplete()
    }

    @Test
    fun `should get all reviews of user`() {
        val user = allUsers.random()
        val userId = user.emailId
        val reviews = allReviews
            .filter {
                it.userId == userId
            }
        reviewRepository
            .insert(reviews)
            .subscribe()

        val page = Optional.of(0)
        val size = Optional.of(10)

        for (currentUser in allUsers) {
            doReturn(Mono.just(currentUser))
                .`when`(userRepository)
                .getUserDetails(currentUser.emailId)
        }

        StepVerifier.create(
            reviewController.getProductReview(
                productId = Optional.empty(),
                userId = Optional.of(userId),
                size,
                page,
                response
            )
        )
            .expectNext(
                Pageable(
                    size = size.get().toLong(),
                    nextPage = null,
                    data = reviews
                        .map {
                            ReviewResponse(
                                review = it,
                                user = user
                            )
                        }
                )
            )
            .verifyComplete()
    }

    @Test
    fun `should return product reviews`() {
        val productId = allReviews.random().productId
        val reviews = allReviews
            .filter { it.productId == productId }
        reviewRepository
            .insert(reviews)
            .subscribe()

        val page = Optional.of(0)
        val size = Optional.of(10)

        for (user in allUsers) {
            doReturn(Mono.just(user))
                .`when`(userRepository)
                .getUserDetails(user.emailId)
        }

        StepVerifier
            .create(
                reviewController.getProductReview(
                    productId = Optional.of(productId),
                    userId = Optional.empty(),
                    size,
                    page,
                    response
                )
            )
            .expectNext(
                Pageable(
                    size = size.get().toLong(),
                    nextPage = null,
                    data = reviews
                        .map { review ->
                            ReviewResponse(
                                review = review,
                                user = allUsers.first { user ->
                                    user.emailId == review.userId
                                }
                            )
                        }
                )
            )
            .verifyComplete()
    }

    @Test
    fun `should return user review on product`() {
        val review = allReviews.random()
        val productId = review.productId
        val userId = review.userId

        val page = Optional.of(0)
        val size = Optional.of(10)

        reviewRepository
            .insert(review)
            .subscribe()
        for (user in allUsers) {
            doReturn(Mono.just(user))
                .`when`(userRepository)
                .getUserDetails(user.emailId)
        }

        StepVerifier
            .create(
                reviewController.getProductReview(
                    productId = Optional.of(productId),
                    userId = Optional.of(userId),
                    size,
                    page,
                    response
                )
            )
            .expectNext(
                Pageable(
                    size = size.get().toLong(),
                    nextPage = null,
                    data = listOf(review)
                        .map {
                            ReviewResponse(
                                review = it,
                                user = allUsers.first { user ->
                                    user.emailId == review.userId
                                }
                            )
                        }
                )
            )
            .verifyComplete()
    }
    companion object {
        private lateinit var mongoDBContainer: MongoDBContainer

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            mongoDBContainer = MongoDBContainer("mongo:latest")
                .apply { withExposedPorts(27_017) }
                .apply { start() }
            mongoDBContainer
                .withReuse(true)
                .withAccessToHost(true)
            System.setProperty("spring.data.mongodb.uri", "${mongoDBContainer.connectionString}/test")
            System.setProperty("spring.data.mongodb.auto-index-creation", "true")
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            mongoDBContainer.stop()
        }
    }
}
