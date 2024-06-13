package com.springsecurityexample.dto

data class CustomerRegistrationRequest(
    val name: String,
    val email: String,
    val mobileNumber: String,
    val password: String,
    val role: String,
)
