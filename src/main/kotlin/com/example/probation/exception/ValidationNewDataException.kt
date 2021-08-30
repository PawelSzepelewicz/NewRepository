package com.example.probation.exception

class ValidationNewDataException(
    override var message: String,
    var field: String
) : RuntimeException(message)