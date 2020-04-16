import { Injectable, Inject } from "@angular/core";
import { LOCAL_STORAGE, StorageService } from "ngx-webstorage-service";

@Injectable({
  providedIn: "root",
})

/**
 * Responsible for recording the progress (of the media previously played)
 */
export class PlayerProgressService {
  constructor(@Inject(LOCAL_STORAGE) private storage: StorageService) {}

  /**
   * Record the progress
   */
  recordProgress(progress: Progress): void {
    this.storage.set(progress.name, progress.time);
  }

  /**
   * Get the progress
   */
  getProgress(name): number {
    return this.storage.get(name);
  }
}

interface Progress {
  name: string;
  time: number;
}
