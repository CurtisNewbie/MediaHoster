import { TestBed } from '@angular/core/testing';

import { PlayerProgressService } from './player-progress.service';

describe('PlayerProgressService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: PlayerProgressService = TestBed.get(PlayerProgressService);
    expect(service).toBeTruthy();
  });
});
