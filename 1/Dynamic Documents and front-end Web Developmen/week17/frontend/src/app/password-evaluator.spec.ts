import { TestBed } from '@angular/core/testing';

import { PasswordEvaluator } from './password-evaluator';

describe('PasswordEvaluator', () => {
  let service: PasswordEvaluator;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PasswordEvaluator);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
