package com.example.bibliohub.utils

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout

class FormFunctions {
    companion object {
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

        private fun TextInputLayout.setFieldError(isError: Boolean, errorText: String? = null) {
            error = if (isError) errorText else null
        }
    }
}