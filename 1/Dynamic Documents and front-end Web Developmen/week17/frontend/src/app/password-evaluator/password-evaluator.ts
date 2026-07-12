import { HttpClient } from '@angular/common/http';
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
  serverMessage: string | null = null;
  isLoading: boolean = false;

  constructor(
    private passwordEvaluator: PasswordEvaluator,
    private http: HttpClient
  ) { }

  /**
   * Submit password to backend
   */
  submitPassword(): void {
    if (!this.password) return;

    this.isLoading = true;
    this.serverMessage = null;

    this.http.post<{ message: string }>('http://127.0.0.1:3000/api/check-password', {
      password: this.password
    }).subscribe({
      next: (response) => {
        this.serverMessage = response.message;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error submitting password:', error);
        this.serverMessage = 'Error connecting to server';
        this.isLoading = false;
      }
    });
  }

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
