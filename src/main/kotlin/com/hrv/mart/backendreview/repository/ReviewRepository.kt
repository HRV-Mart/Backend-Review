package com.hrv.mart.backendreview.repository

import com.hrv.mart.backendreview.model.Review
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ReviewRepository : ReactiveMongoRepository<Review, String> {
    fun findByUserIdAndProductId(userId: String, productId: String): Mono<Review>
    fun findByProductId(productId: String): Flux<Review>
    fun findByUserId(userId: String): Flux<Review>
    fun existsByUserIdAndProductId(userId: String, productId: String): Mono<Boolean>
    fun countByUserId(userId: String): Mono<Long>
    fun countByProductId(productId: String): Mono<Long>
    fun deleteByUserIdAndProductId(userId: String, productId: String): Mono<Void>
}
