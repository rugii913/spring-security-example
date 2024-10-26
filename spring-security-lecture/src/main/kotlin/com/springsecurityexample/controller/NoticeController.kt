package com.springsecurityexample.controller

import com.springsecurityexample.model.Notice
import com.springsecurityexample.repository.NoticeRepository
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
class NoticeController(
    private val noticeRepository: NoticeRepository,
) {

    @GetMapping("/notices")
    fun getNotices(): ResponseEntity<List<Notice>> {
        val notices = noticeRepository.findAllActiveNotices()

        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
            .body(notices)
    }
}
