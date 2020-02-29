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
      this.medias = val;
    });
  }

  fetchMediaAmount() {
    this.http.fetchMediaAmount().subscribe((amount: number) => {
      this.amount = amount;
    });
  }
}
