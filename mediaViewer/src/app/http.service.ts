import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { CONFIG } from "../environments/config";
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';

@Injectable({
  providedIn: "root"
})
export class HttpService {
  readonly BASE_URL = `http://${CONFIG.backend.host}:${CONFIG.backend.port}/media`;
  readonly SOCKET_URL = `ws://${CONFIG.backend.host}:${CONFIG.backend.port}/event/change`;

  constructor(private http: HttpClient) { }

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

  getSocketUrl(): string {
    return this.SOCKET_URL;
  }

  // this WebSocketSubject listens to the event about change 
  // emitted from backend
  openWebSocketConn(): WebSocketSubject<string> {
    return webSocket({ url: this.SOCKET_URL, deserializer: msg => msg.data });
  }
}
