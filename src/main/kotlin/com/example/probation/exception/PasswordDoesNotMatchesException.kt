package com.example.probation.exception

class PasswordDoesNotMatchesException(override var message: String) : RuntimeException(message)