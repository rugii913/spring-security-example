import { Injectable } from '@angular/core';
import { HttpInterceptor,HttpRequest,HttpHandler,HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import {Router} from '@angular/router';
import {tap} from 'rxjs/operators';
import { User } from 'src/app/model/user.model';

@Injectable()
export class XhrInterceptor implements HttpInterceptor {

  user = new User();
  constructor(private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    let httpHeaders = new HttpHeaders();
    if(sessionStorage.getItem('userdetails')){
      this.user = JSON.parse(sessionStorage.getItem('userdetails')!);
    }
    if(this.user && this.user.password && this.user.email){
      // credential을 이용한 인증(로그인 요청) 시 사용
      httpHeaders = httpHeaders.append('Authorization', 'Basic ' + window.btoa(this.user.email + ':' + this.user.password));
    } else {
      // 로그인 후 인증(+인가)을 위해 사용
      let jwt = sessionStorage.getItem("Authorization");
      if (jwt) {
        httpHeaders = httpHeaders.append("Authorization", jwt)
      }
    }
    // ------------ CSRF 토큰 관련 ------------
    let xsrf = sessionStorage.getItem('XSRF-TOKEN');
    if(xsrf){ // session storage에 "XSRF-TOKEN"이 있으면, 요청 헤더에 "X-XSRF-TOKEN"라는 이름으로 해당 값을 추가
      httpHeaders = httpHeaders.append('X-XSRF-TOKEN', xsrf);
    }
    // ------------ CSRF 토큰 관련 끝 ------------
    httpHeaders = httpHeaders.append('X-Requested-With', 'XMLHttpRequest');
    const xhr = req.clone({
      headers: httpHeaders
    });
  return next.handle(xhr).pipe(tap(
      (err: any) => {
        if (err instanceof HttpErrorResponse) {
          if (err.status !== 401) {
            return;
          }
          this.router.navigate(['dashboard']);
        }
      }));
  }
}
