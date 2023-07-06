package com.hrv.mart.backendreview.fixture

import com.hrv.mart.backendreview.model.Review
import com.hrv.mart.userlibrary.model.User

object ReviewFixture {
    const val productId1 = "productId1"
    const val productId2 = "productId2"

    const val userId1 = "userId1"
    const val userId2 = "userId2"

    const val description = "Test Description"
    val images = listOf(
        "https://image.test.com/1",
        "https://image.test.com/2",
        "https://image.test.com/3"
    )
    const val title = "Test"

    val allReviews = listOf(
        Review(
            productId = productId1,
            userId = userId1,
            images = images,
            description = description,
            title = title
        ),
        Review(
            productId = productId1,
            userId = userId2,
            images = images,
            description = description,
            title = title
        ),
        Review(
            productId = productId2,
            userId = userId1,
            images = images,
            description = description,
            title = title
        ),
        Review(
            productId = productId2,
            userId = userId2,
            images = images,
            description = description,
            title = title
        )
    )
    val allUsers = listOf(
        User(
            name = "Test User",
            emailId = userId1
        ),
        User(
            name = "Test User",
            emailId = userId2
        )
    )
}
