import { Component, OnInit, Output, EventEmitter } from "@angular/core";
import { HttpService } from "../http.service";
import { WebSocketSubject } from "rxjs/webSocket";

//TODO: this causes tight coupling, bad idea
const FILE_ADDED = "added";
const FILE_REMOVED = "removed";

@Component({
  selector: "app-select-bar",
  templateUrl: "./select-bar.component.html",
  styleUrls: ["./select-bar.component.css"]
})
export class SelectBarComponent implements OnInit {
  medias: string[];
  amount: number;
  searchedText: string = "";
  /** indicies of search result in medias[] */
  searchResult: number[] = [];

  @Output() selected = new EventEmitter<string>();

  constructor(private http: HttpService) { }

  ngOnInit() {
    this.fetchMediaList();
    this.fetchMediaAmount();
    let wss: WebSocketSubject<string> = this.http.openWebSocketConn();
    wss.subscribe({
      next: (msg: string) => {
        let splited: string[] = msg.split(":");
        if (splited[1] == FILE_ADDED) {
          this.medias.push(splited[0]);
          console.log("Added", splited[0]);
        } else if (splited[1] == FILE_REMOVED) {
          let index = this.medias.indexOf(splited[0]);
          if (index >= 0) {
            this.medias.splice(index, 1);
          }
          console.log("Removed", splited[0]);
        }
      }
    });
  }

  /**
   * Emit event to player, payload of event is the name of the media file
   * @param mediaName
   */
  play(mediaName: string) {
    this.selected.emit(mediaName);
  }

  fetchMediaList() {
    this.http.fetchMediaList().subscribe((val: string[]) => {
      this.medias = val.sort();
    });
  }

  fetchMediaAmount() {
    this.http.fetchMediaAmount().subscribe((amount: number) => {
      this.amount = amount;
    });
  }

  /**
   * Search given text
   *
   * @param text
   */
  search() {
    if (this.searchedText.length > 0 && !this.searchedText.match(/\s+/)) {
      this.searchResult = [];
      this.searchedText = this.searchedText.toLowerCase();
      for (let i = 0; i < this.medias.length; i++) {
        if (this.medias[i].toLowerCase().includes(this.searchedText)) {
          this.searchResult.push(i);
        }
      }
    } else {
      if (this.searchResult.length > 0)
        // reset
        this.searchResult = [];
    }
  }
}
