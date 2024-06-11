package com.springsecurityexample.dto

data class CustomerRegistrationRequest(
    val email: String,
    val password: String,
    val role: String,
)
