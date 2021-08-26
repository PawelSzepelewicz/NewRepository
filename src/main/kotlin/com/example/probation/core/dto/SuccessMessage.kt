package com.example.probation.core.dto

data class SuccessMessage(val message: String = SUCCESS_MESSAGE) {
    companion object {
        const val SUCCESS_MESSAGE = "success"
    }
}
