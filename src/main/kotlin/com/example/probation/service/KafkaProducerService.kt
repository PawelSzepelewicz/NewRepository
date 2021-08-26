package com.example.probation.service

interface KafkaProducerService {
    fun send(action: String)
}