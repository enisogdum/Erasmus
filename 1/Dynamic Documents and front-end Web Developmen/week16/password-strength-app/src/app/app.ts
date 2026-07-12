import { Component, signal } from '@angular/core';
import { PasswordEvaluatorComponent } from './password-evaluator/password-evaluator';

@Component({
  selector: 'app-root',
  imports: [PasswordEvaluatorComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('password-strength-app');
}
