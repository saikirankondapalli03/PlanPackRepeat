import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, ReplaySubject } from 'rxjs';
import { User } from '../models/new-user';
import { publishReplay, refCount } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  userEmail: string;
  isSuperUser: boolean;
  private userEmailUpdate = new ReplaySubject<string>();
  userEmailObservable = this.userEmailUpdate.asObservable();

  constructor(private authHttp: HttpClient) { }


  getUserInfo(email: string): Observable<any> {
      this.userEmail = email;
      return this.authHttp.get(`https://travelapp-boot.cfapps.io/users/getUserByEmail/${email}`)
      .pipe(publishReplay(1))
      .pipe(refCount());
  }

  createUser(userInfo: User) {
    console.log('this is the user info:', userInfo);
    return this.authHttp.post(`https://travelapp-boot.cfapps.io/users/createuser`, userInfo);
  }

  updateUser(userInfo: User) {
    console.log('Updating:', userInfo);
    return this.authHttp.put(`https://travelapp-boot.cfapps.io/users/${userInfo.id}`, userInfo);
  }

  setUserEmail(userEmail: string) {
    this.userEmail = userEmail;
    this.userEmailUpdate.next(this.userEmail);
  }

}
