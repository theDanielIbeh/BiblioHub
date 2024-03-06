package com.example.bibliohub.utils

object Constants {
    const val APP_IDENTIFIER = "BiblioHub"
    const val USER = "user"
    const val IS_LOGGED_IN = "is_logged_in"
    const val IS_ADMIN = "is_admin"

    /** File constants */
    const val NO_MEDIA_FILE = ".nomedia"
    const val PRODUCT_PICTURE_DIR = "Products"

    const val DATE_FORMAT_FULL = "MMMM d, yyyy" // e.g May 24, 2022
    const val DATE_FORMAT_HYPHEN_DMY = "dd-MM-yyyy" // 10-01-2022

    enum class Status {
        PENDING,
        COMPLETED,
        APPROVED,
        REJECTED
    }
}