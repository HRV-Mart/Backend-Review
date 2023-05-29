package com.hrv.mart.backendreview.service

import com.hrv.mart.backendreview.model.Review
import com.hrv.mart.backendreview.repository.ReviewRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ReviewService (
    @Autowired
    private val reviewRepository: ReviewRepository
)
{
    fun createReview(review: Review) =
        reviewRepository.insert(review.setIdToDefault())
    fun updateReview(review: Review) =
        reviewRepository.existsById(review.id)
            .flatMap {
                if (it) {
                    reviewRepository.save(review)
                        .then(Mono.just("Review Updated successfully"))
                }
                else {
                    Mono.just("Review Not Found")
                }
            }
    fun deleteReview(reviewId: String) =
        reviewRepository.deleteById(reviewId)
            .then(Mono.just("Review Deleted"))
    fun getProductReviews(productId: String) =
        reviewRepository.findByProductId(productId)
    fun getUserReview(userId: String) =
        reviewRepository.findByUserId(userId)
    fun getProductReviewPostedByUser(productId: String, userId: String) =
        reviewRepository.findByUserIdAndProductId(userId, productId)
    fun getAllReview() =
        reviewRepository.findAll()
    fun getReviewById(reviewId: String) =
        reviewRepository.findById(reviewId)
}