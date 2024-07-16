import { Component, OnInit } from '@angular/core';
import { User } from "src/app/model/user.model";
import { NgForm } from '@angular/forms';
import { LoginService } from 'src/app/services/login/login.service';
import { Router } from '@angular/router';
import { getCookie } from 'typescript-cookie';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  authStatus: string = "";
  model = new User();

  constructor(private loginService: LoginService, private router: Router) {

   }

  ngOnInit(): void {

  }

  validateUser(loginForm: NgForm) {
    this.loginService.validateLoginDetails(this.model).subscribe(
      responseData => {
        // 로그인 요청 후 받은 응답에서 Authorization 헤더에 있는 JWT 추출 후 session storage에 "Authorization"을 key로 저장
        window.sessionStorage.setItem("Authorization", responseData.headers.get("Authorization")!);

        this.model = <any> responseData.body;

        this.model.authStatus = 'AUTH';
        window.sessionStorage.setItem("userdetails",JSON.stringify(this.model));
        // ------------ CSRF 토큰 관련 ------------
        // XSRF-TOKEN 자체는 응답에서 Set-Cookie 헤더로 오기 때문에 알아서 Cookie에 저장되어 있음
        // "XSRF-TOKEN"이라는 이름을 가진 cookie를 가져와서 session storage에 저장
        let xsrf = getCookie('XSRF-TOKEN')!;
        window.sessionStorage.setItem("XSRF-TOKEN",xsrf);
        // ------------ CSRF 토큰 관련 끝 ------------
        this.router.navigate(['dashboard']);
      });

  }

}
