package com.hrv.mart.backendreview.service

import com.hrv.mart.backendreview.model.Review
import com.hrv.mart.backendreview.repository.ReviewRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ReviewService(
    @Autowired
    private val reviewRepository: ReviewRepository
) {
    fun createReview(
        review: Review,
        response: ServerHttpResponse
    ) =
        reviewRepository
            .existsByUserIdAndProductId(review.userId, review.productId)
            .flatMap { isExist ->
                if (isExist) {
                    response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                    Mono.just("Review already exist")
                } else {
                    response.statusCode = HttpStatus.OK
                    reviewRepository
                        .insert(review)
                        .then(Mono.just("Review created successfully"))
                }
            }
    fun deleteReview(
        userId: String,
        productId: String,
        response: ServerHttpResponse
    ) =
        reviewRepository.existsByUserIdAndProductId(
            userId,
            productId
        )
            .flatMap { exist ->
                if (exist) {
                    response.statusCode = HttpStatus.OK
                    reviewRepository.deleteByUserIdAndProductId(userId, productId)
                        .then(Mono.just("Review deleted successfully"))
                } else {
                    response.statusCode = HttpStatus.NOT_FOUND
                    Mono.just("Review not found")
                }
            }

    fun getProductReviews(
        productId: String,
        pageRequest: PageRequest
    ) =
        reviewRepository.findByProductId(
            productId,
            pageRequest
        )
    fun getUserReview(userId: String, pageRequest: PageRequest) =
        reviewRepository.findByUserId(
            userId,
            pageRequest
        )
    fun getProductReviewPostedByUser(
        productId: String,
        userId: String
    ) =
        reviewRepository.findByUserIdAndProductId(
            userId,
            productId
        )
    fun getReviewById(reviewId: String) =
        reviewRepository.findById(reviewId)
}
