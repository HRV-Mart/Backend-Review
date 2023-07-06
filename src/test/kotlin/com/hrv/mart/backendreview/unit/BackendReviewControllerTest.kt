package com.hrv.mart.backendreview.unit

import com.hrv.mart.backendreview.controller.ReviewController
import com.hrv.mart.backendreview.fixture.ReviewFixture.allReviews
import com.hrv.mart.backendreview.fixture.ReviewFixture.allUsers
import com.hrv.mart.backendreview.fixture.ReviewFixture.description
import com.hrv.mart.backendreview.fixture.ReviewFixture.images
import com.hrv.mart.backendreview.fixture.ReviewFixture.productId1
import com.hrv.mart.backendreview.fixture.ReviewFixture.productId2
import com.hrv.mart.backendreview.fixture.ReviewFixture.title
import com.hrv.mart.backendreview.fixture.ReviewFixture.userId1
import com.hrv.mart.backendreview.fixture.ReviewFixture.userId2
import com.hrv.mart.backendreview.model.Review
import com.hrv.mart.backendreview.model.ReviewResponse
import com.hrv.mart.backendreview.repository.ReviewRepository
import com.hrv.mart.backendreview.service.ReviewService
import com.hrv.mart.custompageable.CustomPageRequest
import com.hrv.mart.custompageable.model.Pageable
import com.hrv.mart.userlibrary.model.User
import com.hrv.mart.userlibrary.repository.UserRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.server.reactive.ServerHttpResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

class BackendReviewControllerTest {
    private val response = mock(ServerHttpResponse::class.java)
    private val reviewRepository = mock(ReviewRepository::class.java)
    private val userRepository = mock(UserRepository::class.java)
    private val reviewService = ReviewService(reviewRepository, userRepository)
    private val reviewController = ReviewController(reviewService)

    @Test
    fun `should create product and return a successful message`() {
        val review = allReviews.random()
        doReturn(Mono.just(review))
            .`when`(reviewRepository)
            .insert(review)
        doReturn(Mono.just(false))
            .`when`(reviewRepository)
            .existsByUserIdAndProductId(review.userId, review.productId)
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
        doReturn(Mono.just(true))
            .`when`(reviewRepository)
            .existsByUserIdAndProductId(review.userId, review.productId)
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
        doReturn(Mono.just(true))
            .`when`(reviewRepository)
            .existsByUserIdAndProductId(review.userId, review.productId)
        doReturn(Mono.empty<Void>())
            .`when`(reviewRepository)
            .deleteByUserIdAndProductId(review.userId, review.productId)
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
        doReturn(Mono.just(false))
            .`when`(reviewRepository)
            .existsByUserIdAndProductId(review.userId, review.productId)
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

        val page = Optional.of(0)
        val size = Optional.of(10)

        val pageRequest = CustomPageRequest.getPageRequest(
            optionalPage = page,
            optionalSize = size
        )

        doReturn(Flux.just(*reviews.toTypedArray()))
            .`when`(reviewRepository)
            .findByUserId(
                userId = userId,
                pageRequest = pageRequest
            )
        doReturn(Mono.just(reviews.size.toLong()))
            .`when`(reviewRepository)
            .countByUserId(userId)
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

        val page = Optional.of(0)
        val size = Optional.of(10)

        val pageRequest = CustomPageRequest.getPageRequest(
            optionalPage = page,
            optionalSize = size
        )

        doReturn(Flux.just(*reviews.toTypedArray()))
            .`when`(reviewRepository)
            .findByProductId(productId, pageRequest)
        doReturn(Mono.just(reviews.size.toLong()))
            .`when`(reviewRepository)
            .countByProductId(productId)
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

        doReturn(Mono.just(review))
            .`when`(reviewRepository)
            .findByUserIdAndProductId(
                userId,
                productId
            )
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
}
