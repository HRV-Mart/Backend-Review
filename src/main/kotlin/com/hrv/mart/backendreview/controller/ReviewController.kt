package com.hrv.mart.backendreview.controller

import com.hrv.mart.backendreview.model.Review
import org.springframework.web.bind.annotation.*
import java.util.Optional

@RestController
@RequestMapping("/review")
class ReviewController {
    @PostMapping
    fun createReview(@RequestBody review: Review) =
        TODO("Link with service")
    @PutMapping
    fun updateReview(@RequestBody review: Review) =
        TODO("Link with service")
    @GetMapping
    fun getProductReview(
        @RequestParam productId: Optional<String>,
        @RequestParam userId: Optional<String>,
        @RequestParam size: Optional<Int>,
        @RequestParam page: Optional<Int>
    ) =
        TODO("Link with service")
    @GetMapping("/{reviewID}")
    fun getReviewById(@PathVariable reviewID: String) =
        TODO("Link with service")
}