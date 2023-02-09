import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  url = environment.apiURL;
  jsonHeader = {
    headers: new HttpHeaders().set('Content-Type', 'application/json'),
  };

  constructor(private http: HttpClient) {}

  signup(data: any) {
    return this.http.post(`${this.url}/user/signup`, data, this.jsonHeader);
  }

  forgotPassword(data: any) {
    return this.http.post(
      `${this.url}/user/forgotPassword`,
      data,
      this.jsonHeader
    );
  }

  login(data: any) {
    return this.http.post(`${this.url}/user/login`, data, this.jsonHeader);
  }

  checkToken() {
    return this.http.get(`${this.url}/user/check-token`);
  }

  changePassword(data: any) {
    return this.http.post(
      `${this.url}/user/changePassword`,
      data,
      this.jsonHeader
    );
  }

  getAllUsers() {
    return this.http.get(`${this.url}/user/get`);
  }

  update(data: any) {
    return this.http.post(`${this.url}/user/update`, data, this.jsonHeader);
  }
}
