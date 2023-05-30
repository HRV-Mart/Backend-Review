package com.hrv.mart.backendreview.controller

import com.hrv.mart.backendreview.fixture.ReviewFixture.description
import com.hrv.mart.backendreview.fixture.ReviewFixture.images
import com.hrv.mart.backendreview.fixture.ReviewFixture.productId1
import com.hrv.mart.backendreview.fixture.ReviewFixture.productId2
import com.hrv.mart.backendreview.fixture.ReviewFixture.title
import com.hrv.mart.backendreview.fixture.ReviewFixture.userId1
import com.hrv.mart.backendreview.fixture.ReviewFixture.userId2
import com.hrv.mart.backendreview.model.Review
import com.hrv.mart.backendreview.repository.ReviewRepository
import com.hrv.mart.backendreview.service.ReviewService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.server.reactive.ServerHttpResponse
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class BackendReviewControllerTest {
    private val response = mock(ServerHttpResponse::class.java)
    private val reviewRepository = mock(ReviewRepository::class.java)
    private val reviewService = ReviewService(reviewRepository)
    private val reviewController = ReviewController(reviewService)

    private val allReviews = listOf(
        Review(
            productId = productId1,
            userId = userId1,
            images = images,
            description = description,
            title = title
        ),
        Review(
            productId = productId1,
            userId = userId2,
            images = images,
            description = description,
            title = title
        ),
        Review(
            productId = productId2,
            userId = userId1,
            images = images,
            description = description,
            title = title
        ),
        Review(
            productId = productId2,
            userId = userId2,
            images = images,
            description = description,
            title = title
        )
    )

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
}
