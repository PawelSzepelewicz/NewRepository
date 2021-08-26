package com.example.probation.exception

class UserNotFoundByTokenException(override var message: String) : RuntimeException(message)