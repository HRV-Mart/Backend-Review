package com.hrv.mart.backendreview.controller

import com.hrv.mart.backendreview.model.Review
import com.hrv.mart.backendreview.model.ReviewResponse
import com.hrv.mart.backendreview.service.ReviewService
import com.hrv.mart.custompageable.CustomPageRequest
import com.hrv.mart.custompageable.model.Pageable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
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
        @RequestParam userId: Optional<String>,
        @RequestParam size: Optional<Int>,
        @RequestParam page: Optional<Int>,
        response: ServerHttpResponse
    ): Mono<Pageable<ReviewResponse>> {
        val pageRequest = CustomPageRequest.getPageRequest(
            optionalPage = page,
            optionalSize = size
        )
        if (productId.isPresent) {
            return if (userId.isPresent) {
                response.statusCode = HttpStatus.OK
                reviewService
                    .getProductReviewPostedByUser(
                        productId.get(),
                        userId.get(),
                        pageRequest
                    )
            } else {
                response.statusCode = HttpStatus.OK
                reviewService
                    .getProductReviews(
                        productId.get(),
                        pageRequest
                    )
            }
        } else {
            return if (userId.isPresent) {
                response.statusCode = HttpStatus.OK
                reviewService
                    .getUserReview(
                        userId.get(),
                        pageRequest
                    )
            } else {
                response.statusCode = HttpStatus.NOT_IMPLEMENTED
                Mono.empty()
            }
        }
    }
}
