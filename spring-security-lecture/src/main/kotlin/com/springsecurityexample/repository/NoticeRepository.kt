package com.springsecurityexample.repository

import com.springsecurityexample.model.Notice
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeRepository : CrudRepository<Notice, Int> {

    @Query(value = "from Notice n where CURDATE() BETWEEN n.noticeBeginDate AND n.noticeEndDate")
    fun findAllActiveNotices(): List<Notice>
}
