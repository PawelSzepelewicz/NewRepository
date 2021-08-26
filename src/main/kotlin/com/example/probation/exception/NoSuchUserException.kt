package com.example.probation.exception

class NoSuchUserException(override var message: String) : RuntimeException(message)