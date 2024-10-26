package com.springsecurityexample.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "notice_detail")
class Notice(
    val noticeSummary: String,
    val noticeDetail: String,
    val noticeBeginDate: LocalDate,
    val noticeEndDate: LocalDate,
    val createDate: LocalDate,
    val updateDate: LocalDate,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var noticeId: Int? = null
}
