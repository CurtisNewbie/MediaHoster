import { Component, OnInit, ElementRef } from "@angular/core";
import { HttpService } from "../http.service";

@Component({
  selector: "app-player",
  templateUrl: "./player.component.html",
  styleUrls: ["./player.component.css"]
})
export class PlayerComponent implements OnInit {
  readonly URL_WITH_PARAM: string = `${this.http.getBaseUrl()}?filename=`;
  srcUrl: string = "";
  displayedUrl: string = "";
  constructor(private http: HttpService) {}

  ngOnInit() {}

  // change srcUrl
  onSelected(mediaName: string) {
    this.srcUrl = this.URL_WITH_PARAM + mediaName;
    this.displayedUrl = mediaName;
    console.log(this.srcUrl);
  }
}
