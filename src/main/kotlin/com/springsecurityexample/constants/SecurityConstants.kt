package com.springsecurityexample.constants

object SecurityConstants {
    const val JWT_KEY = "jxgEQeXHuPQ8VdbyYFNkANdudQ53YUn4" // 실제 애플리케이션에서는 감춰져야 함, 환경 변수로 설정하는 등의 방법 사용
    const val JWT_HEADER = "Authorization" // 이 헤더에 토큰을 담아 보낼 것 → CORS에서 exposed header로 설정하여 브라우저가 수락할 수 있도록 해야 함
    const val JWT_ISSUER = "Spring Security Example"
    const val JWT_SUBJECT = "JWT"
}
