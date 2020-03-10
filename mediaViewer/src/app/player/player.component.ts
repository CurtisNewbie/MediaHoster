import { Component, OnInit, ElementRef, ViewChild } from "@angular/core";
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

  @ViewChild("playerRef", { static: false })
  playerEleRef: ElementRef;

  constructor(private http: HttpService) {}
  ngOnInit() {}

  // change srcUrl
  onSelected(mediaName: string) {
    this.srcUrl = this.URL_WITH_PARAM + mediaName;
    this.displayedUrl = mediaName;
    this.scrollToPlayer();
    console.log(this.srcUrl);
  }

  scrollToPlayer() {
    this.playerEleRef.nativeElement.scrollIntoView({ behavior: "smooth" });
  }
}
