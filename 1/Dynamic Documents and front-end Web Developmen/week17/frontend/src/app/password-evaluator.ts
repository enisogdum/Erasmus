import { Injectable } from '@angular/core';

export interface PasswordStrength {
  score: number;           // 0-4 (Very Weak to Strong)
  label: string;           // "Weak", "Fair", "Good", "Strong"
  color: string;           // CSS color for visual feedback
  suggestions: string[];   // Array of improvement suggestions
}

@Injectable({
  providedIn: 'root',
})
export class PasswordEvaluator {
  
  /**
   * Main method to evaluate password strength
   */
  evaluatePassword(password: string): PasswordStrength {
    if (!password || password.length === 0) {
      return {
        score: 0,
        label: 'Empty',
        color: '#6c757d',
        suggestions: ['Please enter a password']
      };
    }

    let score = 0;
    const suggestions: string[] = [];

    // Check length
    const lengthScore = this.checkLength(password);
    score += lengthScore;
    
    if (password.length < 6) {
      suggestions.push('Password is too short (minimum 6 characters)');
    } else if (password.length < 9) {
      suggestions.push('Consider using a longer password (9+ characters)');
    }

    // Check character variety
    const varietyScore = this.checkCharacterVariety(password);
    score += varietyScore;

    const hasLower = /[a-z]/.test(password);
    const hasUpper = /[A-Z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const hasSymbol = /[^a-zA-Z0-9]/.test(password);

    if (!hasSymbol) {
      suggestions.push('Add special characters (!@#$%^&*)');
    }
    if (!hasNumber) {
      suggestions.push('Add numbers');
    }
    if (!hasUpper) {
      suggestions.push('Add uppercase letters');
    }
    if (!hasLower) {
      suggestions.push('Add lowercase letters');
    }

    // Check for repeated characters
    if (this.hasRepeatedCharacters(password)) {
      score -= 1;
      suggestions.push('Avoid repeated characters');
    }

    // Check for sequential characters
    if (this.hasSequentialCharacters(password)) {
      score -= 0.5;
      suggestions.push('Avoid sequential characters (e.g., abc, 123)');
    }

    // Normalize score to 0-4 range
    score = Math.max(0, Math.min(4, score));

    // Determine label and color based on score
    let label: string;
    let color: string;

    if (score <= 1) {
      label = 'Weak';
      color = '#dc3545'; // Red
    } else if (score <= 2) {
      label = 'Fair';
      color = '#fd7e14'; // Orange
    } else if (score <= 3) {
      label = 'Good';
      color = '#ffc107'; // Yellow
    } else {
      label = 'Strong';
      color = '#28a745'; // Green
      if (suggestions.length === 0) {
        suggestions.push('Excellent password!');
      }
    }

    return {
      score,
      label,
      color,
      suggestions
    };
  }

  /**
   * Check password length and return score
   */
  private checkLength(password: string): number {
    if (password.length < 6) return 0;
    if (password.length < 9) return 1;
    if (password.length < 13) return 1.5;
    return 2;
  }

  /**
   * Check character variety and return score
   */
  private checkCharacterVariety(password: string): number {
    let varietyScore = 0;
    
    if (/[a-z]/.test(password)) varietyScore += 0.5;
    if (/[A-Z]/.test(password)) varietyScore += 0.5;
    if (/[0-9]/.test(password)) varietyScore += 0.5;
    if (/[^a-zA-Z0-9]/.test(password)) varietyScore += 1;

    return varietyScore;
  }

  /**
   * Check for repeated consecutive characters
   */
  private hasRepeatedCharacters(password: string): boolean {
    return /(.)\\1/.test(password);
  }

  /**
   * Check for sequential characters
   */
  private hasSequentialCharacters(password: string): boolean {
    const sequences = ['abc', 'bcd', 'cde', 'def', 'efg', 'fgh', 'ghi', 'hij', 
                       'ijk', 'jkl', 'klm', 'lmn', 'mno', 'nop', 'opq', 'pqr',
                       'qrs', 'rst', 'stu', 'tuv', 'uvw', 'vwx', 'wxy', 'xyz',
                       '012', '123', '234', '345', '456', '567', '678', '789'];
    
    const lowerPassword = password.toLowerCase();
    return sequences.some(seq => lowerPassword.includes(seq));
  }
}
