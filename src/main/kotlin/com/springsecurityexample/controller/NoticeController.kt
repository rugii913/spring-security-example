package com.springsecurityexample.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NoticeController {

    @GetMapping("/notices")
    fun getNotices(): String {
        return "Here are the notices details from the DB"
    }
}
