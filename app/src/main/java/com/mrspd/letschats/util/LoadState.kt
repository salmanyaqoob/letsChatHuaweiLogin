package com.mrspd.letschats.util

class ErrorMessage {
    companion object {
        var errorMessage: String? = "Something went wrong"
    }
}

enum class LoadState {
    SUCCESS, FAILURE, LOADING
}
