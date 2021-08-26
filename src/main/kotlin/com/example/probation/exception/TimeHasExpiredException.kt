package com.example.probation.exception

class TimeHasExpiredException(override var message: String) : RuntimeException(message)