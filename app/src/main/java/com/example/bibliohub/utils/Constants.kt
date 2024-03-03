package com.example.bibliohub.utils

class Constants private constructor() {
    companion object {
        const val APP_IDENTIFIER = "BiblioHub"
        const val USER = "user"
        const val IS_LOGGED_IN = "is_logged_in"
        const val IS_ADMIN = "is_admin"
    }

    enum class Status {
        PENDING,
        COMPLETED,
        APPROVED,
        REJECTED
    }
}