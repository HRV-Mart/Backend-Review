package com.hrv.mart.backendreview.model

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.text.SimpleDateFormat
import java.util.Date

@Document("Review")
@CompoundIndex(
    name = "review_idx",
    def = "{'userId': 1, 'productId': 1}",
    unique = true
)
data class Review(
    val userId: String,
    val productId: String,
    val title: String,
    val description: String,
    val images: List<String>,
    var dateTimeOfReview: String? = SimpleDateFormat("dd/MM/YYYY hh:mm:ss")
        .format(Date())
) {
    fun resetDateTime(): Review{
        this.dateTimeOfReview = SimpleDateFormat("dd/MM/YYYY hh:mm:ss")
            .format(Date())
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Review

        if (userId != other.userId) return false
        if (productId != other.productId) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (images != other.images) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + productId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + images.hashCode()
        return result
    }

}
