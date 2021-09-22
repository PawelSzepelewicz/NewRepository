package com.example.probation.exception

class UserNotFoundException(override var message: String) : RuntimeException(message)
