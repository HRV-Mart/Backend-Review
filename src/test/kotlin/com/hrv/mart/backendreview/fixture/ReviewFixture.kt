package com.hrv.mart.backendreview.fixture

import com.hrv.mart.backendreview.model.Review
import com.hrv.mart.userlibrary.model.User

object ReviewFixture {
    private const val PRODUCT_ID_1 = "productId1"
    private const val PRODUCT_ID_2 = "productId2"

    private const val USER_ID_1 = "userId1"
    private const val USER_ID_2 = "userId2"

    private const val DESCRIPTION = "Test Description"
    private val images = listOf(
        "https://image.test.com/1",
        "https://image.test.com/2",
        "https://image.test.com/3"
    )
    private const val TITLE = "Test"

    val allReviews = listOf(
        Review(
            productId = PRODUCT_ID_1,
            userId = USER_ID_1,
            images = images,
            description = DESCRIPTION,
            title = TITLE + "1",
            dateTimeOfReview = "07/08/2023 06:26:12"
        ),
        Review(
            productId = PRODUCT_ID_1,
            userId = USER_ID_2,
            images = images,
            description = DESCRIPTION,
            title = TITLE + "2",
            dateTimeOfReview = "07/08/2023 06:27:20"
        ),
        Review(
            productId = PRODUCT_ID_2,
            userId = USER_ID_1,
            images = images,
            description = DESCRIPTION,
            title = TITLE + "3",
            dateTimeOfReview = "07/08/2023 06:28:35"
        ),
        Review(
            productId = PRODUCT_ID_2,
            userId = USER_ID_2,
            images = images,
            description = DESCRIPTION,
            title = TITLE + "4",
            dateTimeOfReview = "07/08/2023 06:29:05"
        )
    )
        .sortedBy {
            it.dateTimeOfReview
        }
        .reversed()
    val allUsers = listOf(
        User(
            name = "Test User",
            emailId = USER_ID_1
        ),
        User(
            name = "Test User",
            emailId = USER_ID_2
        )
    )
}
