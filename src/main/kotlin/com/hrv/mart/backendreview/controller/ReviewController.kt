package com.hrv.mart.backendreview.controller

import com.hrv.mart.backendreview.model.Review
import com.hrv.mart.backendreview.service.ReviewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Optional

@RestController
@RequestMapping("/review")
class ReviewController(
    @Autowired
    private val reviewService: ReviewService
) {
    @PostMapping
    fun createReview(
        @RequestBody review: Review,
        response: ServerHttpResponse
    ) =
        reviewService.createReview(
            review,
            response
        )


    @DeleteMapping("/{userId}/{productId}")
    fun deleteReview(
        @PathVariable userId: String,
        @PathVariable productId: String,
        response: ServerHttpResponse
    ) =
        reviewService.deleteReview(
            userId,
            productId,
            response
        )

    @GetMapping
    fun getProductReview(
        @RequestParam productId: Optional<String>,
        @RequestParam userId: Optional<String>
//        @RequestParam size: Optional<Int>,
//        @RequestParam page: Optional<Int>
    ) =
        if (productId.isPresent) {
            if (userId.isPresent) {
                reviewService
                    .getProductReviewPostedByUser(productId.get(), userId.get())
            } else {
                reviewService
                    .getProductReviews(productId.get())
            }
        } else {
            if (userId.isPresent) {
                reviewService.getUserReview(userId.get())
            } else {
                reviewService.getAllReview()
            }
        }

    @GetMapping("/{reviewID}")
    fun getReviewById(@PathVariable reviewID: String) =
        reviewService.getReviewById(reviewID)
}
