import { Component, OnInit, Output, EventEmitter } from "@angular/core";
import { HttpService } from "../http.service";

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

  constructor(private http: HttpService) {}

  ngOnInit() {
    this.fetchMediaList();
    this.fetchMediaAmount();
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
