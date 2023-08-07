package com.hrv.mart.backendreview.service

import com.hrv.mart.backendreview.model.Review
import com.hrv.mart.backendreview.model.ReviewResponse
import com.hrv.mart.backendreview.repository.ReviewRepository
import com.hrv.mart.custompageable.model.Pageable
import com.hrv.mart.userlibrary.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ReviewService(
    @Autowired
    private val reviewRepository: ReviewRepository,
    @Autowired
    private val userRepository: UserRepository
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
        getPageable(
            data = reviewRepository
                .findByProductId(
                    productId,
                    addSortingInPageRequest(pageRequest)
                )
                .flatMap { review ->
                    userRepository.getUserDetails(review.userId)
                        .map { user ->
                            ReviewResponse(
                                user = user,
                                review = review
                            )
                        }
                },
            count = reviewRepository
                .countByProductId(productId),
            pageRequest = pageRequest
        )

    fun getUserReview(
        userId: String,
        pageRequest: PageRequest
    ) =
        getPageable(
            data = reviewRepository
                .findByUserId(
                    userId,
                    addSortingInPageRequest(pageRequest)
                )
                .flatMap { review ->
                    userRepository.getUserDetails(review.userId)
                        .map { user ->
                            ReviewResponse(
                                user = user,
                                review = review
                            )
                        }
                },
            count = reviewRepository
                .countByUserId(userId),
            pageRequest = pageRequest
        )
    fun getProductReviewPostedByUser(
        productId: String,
        userId: String,
        pageRequest: PageRequest
    ) =
        reviewRepository.findByUserIdAndProductId(
            userId,
            productId
        )
            .flatMap { review ->
                userRepository.getUserDetails(review.userId)
                    .map { user ->
                        ReviewResponse(
                            review = review,
                            user = user
                        )
                    }
            }
            .map { review ->
                val count = 1L
                Pageable(
                    data = listOf(review),
                    nextPage = Pageable.getNextPage(
                        pageSize = pageRequest.pageSize.toLong(),
                        page = pageRequest.pageNumber.toLong(),
                        totalSize = count
                    ),
                    size = pageRequest.pageSize.toLong()
                )
            }
    private fun <T> getPageable(data: Flux<T>, count: Mono<Long>, pageRequest: PageRequest) =
        data.collectList()
            .flatMap { reviews ->
                count
                    .map { totalSize ->
                        Pageable(
                            data = reviews,
                            nextPage = Pageable.getNextPage(
                                pageSize = pageRequest.pageSize.toLong(),
                                page = pageRequest.pageNumber.toLong(),
                                totalSize = totalSize
                            ),
                            size = pageRequest.pageSize.toLong()
                        )
                    }
            }
    private fun addSortingInPageRequest(pageRequest: PageRequest) =
        pageRequest
            .withSort(
                Sort.by(
                    Sort.Direction.DESC,
                    "dateTimeOfReview"
                )
            )
}
