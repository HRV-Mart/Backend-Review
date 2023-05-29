package com.hrv.mart.backendreview.controller

import com.hrv.mart.backendreview.model.Review
import com.hrv.mart.backendreview.service.ReviewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.Optional

@RestController
@RequestMapping("/review")
class ReviewController (
    @Autowired
    private val reviewService: ReviewService
)
{
    @PostMapping
    fun createReview(@RequestBody review: Review) =
        reviewService.createReview(review)
    @PutMapping
    fun updateReview(@RequestBody review: Review) =
        reviewService.updateReview(review)
    @DeleteMapping("/{reviewId}")
    fun deleteReview(@PathVariable reviewId: String) =
        reviewService.deleteReview(reviewId)
    @GetMapping
    fun getProductReview(
        @RequestParam productId: Optional<String>,
        @RequestParam userId: Optional<String>,
        @RequestParam size: Optional<Int>,
        @RequestParam page: Optional<Int>
    ) =
        if (productId.isPresent) {
            if (userId.isPresent) {
                reviewService
                    .getProductReviewPostedByUser(productId.get(), userId.get())
            }
            else {
                reviewService
                    .getProductReviews(productId.get())
            }
        }
    else {
        if (userId.isPresent) {
            reviewService.getUserReview(userId.get())
        }
        else {
            reviewService.getAllReview()
        }
    }
    @GetMapping("/{reviewID}")
    fun getReviewById(@PathVariable reviewID: String) =
        reviewService.getReviewById(reviewID)
}