import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';

@Injectable()
export class AuthenticationService {
    constructor(private http: HttpClient) {}

    initRequest() {
        return this.http.get<any>('/api/v1/init');
    }

    login(username: string, password: string) {
        return this.http.post<any>('/api/v1/login', { username, password }, { observe: 'response' })
            .pipe(map((res: any) => res.status === 200));
    }

    createUser(username: string, password: string) {
        return this.http.post<any>('/api/v1/users', { username, password });
    }

    logout() {
        // remove user from local storage to log user out
        localStorage.removeItem('currentUser');
    }

    isLoggedIn() {
        return localStorage && localStorage.getItem('currentUser');
    }

    getUser() {
        return JSON.parse(localStorage.getItem('currentUser'));
    }

}