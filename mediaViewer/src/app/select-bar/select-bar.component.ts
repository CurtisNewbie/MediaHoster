import { Component, OnInit, Output, EventEmitter } from "@angular/core";
import { Observable } from "rxjs";
import { HttpService } from "../http.service";

@Component({
  selector: "app-select-bar",
  templateUrl: "./select-bar.component.html",
  styleUrls: ["./select-bar.component.css"]
})
export class SelectBarComponent implements OnInit {
  medias: string[];
  @Output() selected = new EventEmitter<string>();

  constructor(private http: HttpService) {}

  ngOnInit() {
    this.fetchMediaList();
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
}
