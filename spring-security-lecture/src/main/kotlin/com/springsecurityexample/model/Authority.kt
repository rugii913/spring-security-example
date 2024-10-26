package com.springsecurityexample.model

import jakarta.persistence.*

@Entity
@Table(name = "authorities")
class Authority(
    val name: String,
    @ManyToOne @JoinColumn(name = "customer_id") val customer: Customer,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
