package com.hrv.mart.backendreview.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("Review")
data class Review(
    val userId: String,
    val productId: String,
    val title: String,
    val description: String,
    val images: List<String>
) {
    @Id
    var id: String = ObjectId().toString()
    constructor(
        userId: String,
        productId: String,
        name: String,
        description: String,
        images: List<String>,
        id: String
    ) : this(userId, productId, name, description, images) {
        this.id = id
    }
    fun setIdToDefault(): Review {
        this.id = ObjectId().toString()
        return this
    }
}
