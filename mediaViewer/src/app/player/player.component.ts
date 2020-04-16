import { Component, OnInit, ElementRef, ViewChild } from "@angular/core";
import { HttpService } from "../http.service";
import { PlayerProgressService } from "../player-progress.service";
import { hostViewClassName } from "@angular/compiler";

@Component({
  selector: "app-player",
  templateUrl: "./player.component.html",
  styleUrls: ["./player.component.css"],
})
export class PlayerComponent implements OnInit {
  readonly URL_WITH_PARAM: string = `${this.http.getBaseUrl()}?filename=`;
  srcUrl: string = "";
  displayedUrl: string = "";

  @ViewChild("playerRef", { static: false })
  playerLocRef: ElementRef;

  @ViewChild("videoPlayer", { static: false })
  videoPlayerRef: ElementRef;

  constructor(
    private http: HttpService,
    private playerProgress: PlayerProgressService
  ) {}

  ngOnInit() {}

  /**
   * Change the url in html:video
   */
  onSelected(mediaName: string) {
    this.srcUrl = this.URL_WITH_PARAM + mediaName;
    this.displayedUrl = mediaName;
    let prevProg = this.playerProgress.getProgress(this.srcUrl);
    if (prevProg != null) this.setCurrtime(prevProg);
    this.scrollToPlayer();
    console.log("Media URL:", this.srcUrl, "Previous Progress: ", prevProg);
  }

  /**
   * Scroll up to where the player is displayed
   */
  scrollToPlayer() {
    this.playerLocRef.nativeElement.scrollIntoView({ behavior: "smooth" });
  }

  /**
   * When the duration of the player is changed
   */
  onTimeChanged() {
    let prog = {
      name: this.srcUrl,
      time: this.getCurrTime(),
    };
    this.playerProgress.recordProgress(prog);
  }

  /**
   * Get the current time of the player
   */
  getCurrTime(): number {
    return parseInt(this.videoPlayerRef.nativeElement.currentTime);
  }

  /**
   * Set current time of the player, only when the first frame is loaded ("loadeddata")
   */
  setCurrtime(time: number) {
    let event = "loadeddata";
    let ev = () => {
      this.videoPlayerRef.nativeElement.currentTime = time;
      this.videoPlayerRef.nativeElement.removeEventListener(event, ev);
    };
    this.videoPlayerRef.nativeElement.addEventListener(event, ev);
  }
}
