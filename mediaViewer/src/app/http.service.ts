import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { CONFIG } from "../environments/config";

@Injectable({
  providedIn: "root"
})
export class HttpService {
  readonly BASE_URL = `http://${CONFIG.backend.host}:${CONFIG.backend.port}/media`;

  constructor(private http: HttpClient) {}

  fetchMediaList(): Observable<any> {
    return this.http.get(this.BASE_URL + "/all");
  }

  fetchMediaAmount(): Observable<any> {
    return this.http.get(this.BASE_URL + "/amount", {
      responseType: "text"
    });
  }

  getBaseUrl(): string {
    return this.BASE_URL;
  }
}
