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
import com.hrv.mart.custompageable.CustomPageRequest
import com.hrv.mart.custompageable.Pageable
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
        val userId = allReviews.random().userId
        val page = Optional.of(0)
        val size = Optional.of(10)
        val userReview = allReviews.filter {
            it.userId == userId
        }
        val pageRequest = CustomPageRequest.getPageRequest(
            optionalPage = page,
            optionalSize = size
        )
        doReturn(Flux.just(*userReview.toTypedArray()))
            .`when`(reviewRepository)
            .findByUserId(userId, pageRequest)
        doReturn(Mono.just(userReview.size.toLong()))
            .`when`(reviewRepository)
            .countByUserId(userId)
        StepVerifier.create(
            reviewController.getProductReview(
                page = page,
                size = size,
                userId = Optional.of(userId),
                productId = Optional.empty(),
                response = response
            )
        )
            .expectNext(Pageable(
                size = size.get().toLong(),
                nextPage = null,
                data = userReview
            ))
            .verifyComplete()
    }
    @Test
    fun `should return product reviews`() {
        val productId = allReviews.random().productId

        val page = Optional.of(0)
        val size = Optional.of(10)
        val productReview = allReviews.filter {
            it.productId == productId
        }
        val pageRequest = CustomPageRequest.getPageRequest(
            optionalPage = page,
            optionalSize = size
        )

        doReturn(Flux.just(*productReview.toTypedArray()))
            .`when`(reviewRepository)
            .findByProductId(productId, pageRequest)
        doReturn(Mono.just(productReview.size.toLong()))
            .`when`(reviewRepository)
            .countByProductId(productId)

        StepVerifier.create(
            reviewController.getProductReview(
                page = page,
                size = size,
                userId = Optional.empty(),
                productId = Optional.of(productId),
                response = response
            )
        )
            .expectNext(Pageable(
                size = size.get().toLong(),
                data = productReview,
                nextPage = null
            ))
            .verifyComplete()
    }
    @Test
    fun `should return user review on product`() {
        val review = allReviews.random()
        val userId = review.userId
        val productId = review.productId

        val page = Optional.of(0)
        val size = Optional.of(10)
        val userProductReview = allReviews.filter {
            it.productId == productId && it.userId == userId
        }

        doReturn(Mono.just(review))
            .`when`(reviewRepository)
            .findByUserIdAndProductId(userId, productId)
        StepVerifier.create(
            reviewController.getProductReview(
                page = page,
                size = size,
                userId = Optional.of(userId),
                productId = Optional.of(productId),
                response = response
            )
        )
            .expectNext(Pageable(
                size = size.get().toLong(),
                data = userProductReview,
                nextPage = null
            ))
            .verifyComplete()
    }
}
