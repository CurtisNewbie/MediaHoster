import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { HttpClientModule } from "@angular/common/http";

import { AppComponent } from "./app.component";
import { SelectBarComponent } from "./select-bar/select-bar.component";
import { PlayerComponent } from "./player/player.component";

@NgModule({
  declarations: [AppComponent, SelectBarComponent, PlayerComponent],
  imports: [BrowserModule, HttpClientModule],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {}
