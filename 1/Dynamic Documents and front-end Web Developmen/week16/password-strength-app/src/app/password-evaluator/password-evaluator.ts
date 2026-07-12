import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PasswordEvaluator, PasswordStrength } from '../password-evaluator';

@Component({
  selector: 'app-password-evaluator',
  imports: [FormsModule, CommonModule],
  templateUrl: './password-evaluator.html',
  styleUrl: './password-evaluator.css',
})
export class PasswordEvaluatorComponent {
  password: string = '';
  showPassword: boolean = false;
  strengthInfo: PasswordStrength | null = null;

  constructor(private passwordEvaluator: PasswordEvaluator) { }

  /**
   * Toggle password visibility
   */
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  /**
   * Handle password input changes and evaluate strength
   */
  onPasswordChange(): void {
    this.strengthInfo = this.passwordEvaluator.evaluatePassword(this.password);
  }
}
