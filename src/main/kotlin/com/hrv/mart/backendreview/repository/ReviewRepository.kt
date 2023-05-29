package com.hrv.mart.backendreview.repository

import com.hrv.mart.backendreview.model.Review
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ReviewRepository : ReactiveMongoRepository<Review, String>{
    fun findByUserIdAndProductId(userId: String, productId: String): Flux<Review>
    fun findByProductId(productId: String): Flux<Review>
    fun findByUserId(userId: String): Flux<Review>
}