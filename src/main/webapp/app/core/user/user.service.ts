import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, Pagination } from 'app/shared/util/request-util';
import { IUser } from './user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  public resourceUrl = SERVER_API_URL + 'services/service1/api/users';
  public resourceUrlOrg = SERVER_API_URL + 'services/service1/api/orgs';
  public resourceGetUrl = this.resourceUrl + '/1';

  constructor(private http: HttpClient) {}

  query(req?: Pagination): Observable<HttpResponse<IUser[]>> {
    const options = createRequestOption(req);
    return this.http.get<IUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  queryOrg(req?: Pagination): Observable<HttpResponse<IUser[]>> {
    const options = createRequestOption(req);
    return this.http.get<IUser[]>(this.resourceUrlOrg, { params: options, observe: 'response' });
  }

  get(req?: Pagination): Observable<HttpResponse<IUser[]>> {
    const options = createRequestOption(req);
    return this.http.get<IUser[]>(this.resourceGetUrl, { params: options, observe: 'response' });
  }

  post(req?: Pagination): Observable<HttpResponse<IUser[]>> {
    const options = createRequestOption(req);
    return this.http.post<IUser[]>(this.resourceUrl, null, { params: options, observe: 'response' });
  }

  put(req?: Pagination): Observable<HttpResponse<IUser[]>> {
    const options = createRequestOption(req);
    return this.http.put<IUser[]>(this.resourceUrl, null, { params: options, observe: 'response' });
  }

  delete(req?: Pagination): Observable<HttpResponse<IUser[]>> {
    const options = createRequestOption(req);
    return this.http.delete<IUser[]>(this.resourceGetUrl, { params: options, observe: 'response' });
  }
}
