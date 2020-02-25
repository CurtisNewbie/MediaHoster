import { Component, OnInit, ElementRef } from "@angular/core";
import { HttpService } from "../http.service";

@Component({
  selector: "app-player",
  templateUrl: "./player.component.html",
  styleUrls: ["./player.component.css"]
})
export class PlayerComponent implements OnInit {
  srcUrl: string = "";
  constructor(private http: HttpService, private eleRef: ElementRef) {}

  ngOnInit() {}

  // change srcUrl
  onSelected(mediaName: string) {
    this.srcUrl = `${this.http.getBaseUrl()}?filename=${mediaName}`;
    console.log(this.srcUrl);
  }
}
