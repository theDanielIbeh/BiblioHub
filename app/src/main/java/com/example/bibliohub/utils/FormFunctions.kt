package com.example.bibliohub.utils

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout

object FormFunctions {

    fun validateLoginEmail(value: String, layout: TextInputLayout): Boolean {
        var isValid = false
        if (value.isEmpty()) {
            val errorText = "This field is required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else {
            layout.setFieldError(isError = false)
            isValid = true
        }
        layout.isErrorEnabled = !isValid
        return isValid
    }

    fun validateLoginPassword(value: String, layout: TextInputLayout): Boolean {
        var isValid = false
        if (value.isBlank()) {
            val errorText = "This field is required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else {
            layout.setFieldError(isError = false)
            isValid = true
        }
        layout.isErrorEnabled = !isValid
        return isValid
    }

    fun validateName(value: String, layout: TextInputLayout): Boolean {
        var isValid = false
        if (value.isEmpty()) {
            val errorText = "This field is required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else if (value.length < 3) {
            val errorText = "At least 3 characters are required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else {
            layout.setFieldError(isError = false)
            isValid = true
        }
        layout.isErrorEnabled = !isValid
        return isValid
    }

    fun validateEmail(value: String, layout: TextInputLayout): Boolean {
        var isValid = false
        if (value.isEmpty()) {
            val errorText = "This field is required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else if (!Patterns.EMAIL_ADDRESS.toRegex().matches(value)) {
            val errorText = "Enter a valid email address"
            layout.setFieldError(isError = true, errorText = errorText)
        } else {
            layout.setFieldError(isError = false)
            isValid = true
        }
        layout.isErrorEnabled = !isValid
        return isValid
    }

    fun validatePassword(value: String, layout: TextInputLayout): Boolean {
        val pattern = "^(?=.*\\d{2})(?=.*[a-zA-Z]{2}).{6,}\$"
        var isValid = false
        if (value.isBlank()) {
            val errorText = "This field is required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else if (!Regex(pattern).matches(value)) {
            val errorText = "Minimum of 6 characters, min. 2 digits and 2 letters are required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else {
            layout.setFieldError(isError = false)
            isValid = true
        }
        layout.isErrorEnabled = !isValid
        return isValid
    }

    fun validateConfirmPassword(
        value: String,
        password: String,
        layout: TextInputLayout
    ): Boolean {
        var isValid = false
        if (value.isBlank()) {
            val errorText = "This field is required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else if (value != password) {
            val errorText = "Passwords do not match"
            layout.setFieldError(isError = true, errorText = errorText)
        } else {
            layout.setFieldError(isError = false)
            isValid = true
        }
        layout.isErrorEnabled = !isValid
        return isValid
    }

    fun validatePostcode(value: String, layout: TextInputLayout): Boolean {
        val pattern = "^[A-Z]{1,2}\\d[A-Z\\d]? ?\\d[A-Z]{2}\$"
        var isValid = false
        if (value.isBlank()) {
            val errorText = "This field is required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else if (!Regex(pattern).matches(value)) {
            val errorText = "Invalid postcode. Make sure all characters are uppercase."
            layout.setFieldError(isError = true, errorText = errorText)
        } else {
            layout.setFieldError(isError = false)
            isValid = true
        }
        layout.isErrorEnabled = !isValid
        return isValid
    }

    fun validateExpiryDate(value: String, layout: TextInputLayout): Boolean {
        val pattern = "^(0[1-9]|1[0-2])\\/?([0-9]{4}|[0-9]{2})\$"
        var isValid = false
        if (value.isBlank()) {
            val errorText = "This field is required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else if (!Regex(pattern).matches(value)) {
            val errorText = "Invalid Expiry Date"
            layout.setFieldError(isError = true, errorText = errorText)
        } else {
            layout.setFieldError(isError = false)
            isValid = true
        }
        layout.isErrorEnabled = !isValid
        return isValid
    }

    fun validateCVV(value: String, layout: TextInputLayout): Boolean {
        val pattern = "^([0-9]{3})\$"
        var isValid = false
        if (value.isBlank()) {
            val errorText = "This field is required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else if (!Regex(pattern).matches(value)) {
            val errorText = "Invalid CVV"
            layout.setFieldError(isError = true, errorText = errorText)
        } else {
            layout.setFieldError(isError = false)
            isValid = true
        }
        layout.isErrorEnabled = !isValid
        return isValid
    }

    fun validateISBN(value: String, layout: TextInputLayout): Boolean {
        val pattern =
            "^(?:ISBN(?:-1[03])?:? )?(?=[ 0-9X]{10}\$|(?=(?:[ 0-9]+[-●]){3})[-●0-9X]{13}\$)[0-9X]{1,5}[-● ][0-9X]+[-● ][0-9X]+[-● ][0-9X]\$\n"
        var isValid = false
        if (value.isBlank()) {
            val errorText = "This field is required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else if (!Regex(pattern).matches(value)) {
            val errorText = "Invalid ISBN"
            layout.setFieldError(isError = true, errorText = errorText)
        } else {
            layout.setFieldError(isError = false)
            isValid = true
        }
        layout.isErrorEnabled = !isValid
        return isValid
    }

    fun validatePIN(value: String, layout: TextInputLayout): Boolean {
        var isValid = false
        if (value.isBlank()) {
            val errorText = "This field is required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else if (value.length != 4 || value.toIntOrNull() == null) {
            val errorText = "Invalid PIN"
            layout.setFieldError(isError = true, errorText = errorText)
        } else {
            layout.setFieldError(isError = false)
            isValid = true
        }
        layout.isErrorEnabled = !isValid
        return isValid
    }

    fun validateNumber(value: String, layout: TextInputLayout): Boolean {
        var isValid = false
        val number = value.toIntOrNull()
        if (value.isBlank()) {
            val errorText = "This field is required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else if (number != null && number < 0) {
            val errorText = "Invalid PIN"
            layout.setFieldError(isError = true, errorText = errorText)
        } else if (number == null) {
            val errorText = "Invalid PIN"
            layout.setFieldError(isError = true, errorText = errorText)
        } else {
            layout.setFieldError(isError = false)
            isValid = true
        }
        layout.isErrorEnabled = !isValid
        return isValid
    }

    fun validateGeneral(value: String?, layout: TextInputLayout): Boolean {
        var isValid = false
        if (value.isNullOrEmpty()) {
            val errorText = "This field is required"
            layout.setFieldError(isError = true, errorText = errorText)
        } else {
            layout.setFieldError(isError = false)
            isValid = true
        }
        layout.isErrorEnabled = !isValid
        return isValid
    }

    private fun TextInputLayout.setFieldError(isError: Boolean, errorText: String? = null) {
        error = if (isError) errorText else null
    }
}