import { inject, Component, signal, ChangeDetectionStrategy } from '@angular/core';
import { LabseqService, LabSeqResponse } from './labseq.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  template: `
    <div class="container">
      <div class="card">
        <div class="header">
          <div class="icon">🔢</div>
          <h1>LabSeq Calculator</h1>
          <p class="subtitle">Calculate values from the labseq sequence</p>
        </div>

        <div class="formula-box">
          <div class="formula-title">Sequence Definition</div>
          <div class="formula">
            <span>l(0) = 0, l(1) = 1, l(2) = 0, l(3) = 1</span><br/>
            <span>l(n) = l(n-4) + l(n-3) for n > 3</span>
          </div>
        </div>

        <form (ngSubmit)="calculate()" class="form">
          <div class="input-group">
            <label for="n-input">Enter Index (n)</label>
            <input 
              id="n-input"
              type="number" 
              [ngModel]="n()" 
              (ngModelChange)="n.set($event)" 
              name="n" 
              min="0" 
              required 
              placeholder="e.g., 10, 100, 1000..."
              [disabled]="loading()"
            />
          </div>

          <button type="submit" class="btn-primary" [disabled]="loading() || n() < 0">
            <span *ngIf="!loading()">Calculate l({{ n() }})</span>
            <span *ngIf="loading()" class="loading-text">
              <span class="spinner"></span>
              Calculating...
            </span>
          </button>
        </form>

        <div *ngIf="error()" class="error-box">
          <span class="error-icon">⚠️</span>
          <span>{{ error() }}</span>
        </div>

        <div *ngIf="response()" class="result-box">
          <div class="result-header">
            <span class="result-label">Result for l({{ response()?.index }})</span>
          </div>
          <div class="result-value">{{ response()?.value }}</div>
          
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon">⚡</div>
              <div class="stat-content">
                <div class="stat-label">Calculation Time</div>
                <div class="stat-value">{{ response()?.calculationTimeMs }} ms</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">💾</div>
              <div class="stat-content">
                <div class="stat-label">Cache Size</div>
                <div class="stat-value">{{ response()?.cacheSize }} values</div>
              </div>
            </div>
          </div>
        </div>

        <div class="footer">
          <a href="http://localhost:8080/swagger-ui" target="_blank" class="link">
            📚 API Documentation
          </a>
          <span class="separator">•</span>
          <a href="http://localhost:8080/openapi" target="_blank" class="link">
            🔧 OpenAPI Spec
          </a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 2rem 1rem;
    }

    .container {
      max-width: 600px;
      margin: 0 auto;
    }

    .card {
      background: white;
      border-radius: 20px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
      padding: 2.5rem;
      animation: slideIn 0.4s ease;
    }

    @keyframes slideIn {
      from {
        opacity: 0;
        transform: translateY(-20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .header {
      text-align: center;
      margin-bottom: 2rem;
    }

    .icon {
      font-size: 3rem;
      margin-bottom: 1rem;
      animation: bounce 2s infinite;
    }

    @keyframes bounce {
      0%, 100% { transform: translateY(0); }
      50% { transform: translateY(-10px); }
    }

    h1 {
      margin: 0 0 0.5rem 0;
      color: #333;
      font-size: 2rem;
      font-weight: 700;
    }

    .subtitle {
      margin: 0;
      color: #666;
      font-size: 0.95rem;
    }

    .formula-box {
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
      padding: 1.25rem;
      border-radius: 12px;
      margin-bottom: 2rem;
    }

    .formula-title {
      font-weight: 600;
      color: #555;
      font-size: 0.85rem;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      margin-bottom: 0.5rem;
    }

    .formula {
      font-family: 'Courier New', monospace;
      font-size: 0.9rem;
      color: #333;
      line-height: 1.6;
    }

    .form {
      margin-bottom: 1.5rem;
    }

    .input-group {
      margin-bottom: 1.5rem;
    }

    label {
      display: block;
      margin-bottom: 0.5rem;
      color: #555;
      font-weight: 600;
      font-size: 0.95rem;
    }

    input[type="number"] {
      width: 100%;
      padding: 0.875rem 1rem;
      border: 2px solid #e0e0e0;
      border-radius: 10px;
      font-size: 1rem;
      transition: all 0.3s ease;
      box-sizing: border-box;
    }

    input[type="number"]:focus {
      outline: none;
      border-color: #667eea;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }

    input[type="number"]:disabled {
      background: #f5f5f5;
      cursor: not-allowed;
    }

    .btn-primary {
      width: 100%;
      padding: 1rem;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border: none;
      border-radius: 10px;
      font-size: 1rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-primary:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
    }

    .btn-primary:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none;
    }

    .loading-text {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
    }

    .spinner {
      display: inline-block;
      width: 16px;
      height: 16px;
      border: 2px solid rgba(255, 255, 255, 0.3);
      border-top-color: white;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }

    @keyframes spin {
      to { transform: rotate(360deg); }
    }

    .error-box {
      background: #fee;
      border: 1px solid #fcc;
      color: #c33;
      padding: 1rem;
      border-radius: 10px;
      margin-bottom: 1.5rem;
      display: flex;
      align-items: center;
      gap: 0.75rem;
      animation: shake 0.5s ease;
    }

    @keyframes shake {
      0%, 100% { transform: translateX(0); }
      25% { transform: translateX(-10px); }
      75% { transform: translateX(10px); }
    }

    .error-icon {
      font-size: 1.5rem;
    }

    .result-box {
      background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
      padding: 1.5rem;
      border-radius: 12px;
      margin-bottom: 1.5rem;
      animation: fadeIn 0.5s ease;
    }

    @keyframes fadeIn {
      from {
        opacity: 0;
        transform: scale(0.95);
      }
      to {
        opacity: 1;
        transform: scale(1);
      }
    }

    .result-header {
      margin-bottom: 1rem;
    }

    .result-label {
      color: #666;
      font-size: 0.85rem;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      font-weight: 600;
    }

    .result-value {
      color: #667eea;
      font-size: 1.75rem;
      font-weight: 700;
      word-break: break-all;
      margin-bottom: 1.5rem;
      font-family: 'Courier New', monospace;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 1rem;
    }

    .stat-card {
      background: white;
      padding: 1rem;
      border-radius: 10px;
      display: flex;
      align-items: center;
      gap: 0.75rem;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    }

    .stat-icon {
      font-size: 1.5rem;
    }

    .stat-content {
      flex: 1;
    }

    .stat-label {
      color: #999;
      font-size: 0.75rem;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      margin-bottom: 0.25rem;
    }

    .stat-value {
      color: #333;
      font-weight: 700;
      font-size: 1.1rem;
    }

    .footer {
      text-align: center;
      padding-top: 1.5rem;
      border-top: 1px solid #e0e0e0;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.75rem;
      flex-wrap: wrap;
    }

    .link {
      color: #667eea;
      text-decoration: none;
      font-weight: 600;
      font-size: 0.9rem;
      transition: color 0.3s ease;
    }

    .link:hover {
      color: #764ba2;
      text-decoration: underline;
    }

    .separator {
      color: #ccc;
    }

    @media (max-width: 640px) {
      .card {
        padding: 1.5rem;
      }

      h1 {
        font-size: 1.5rem;
      }

      .stats-grid {
        grid-template-columns: 1fr;
      }

      .footer {
        flex-direction: column;
        gap: 0.5rem;
      }

      .separator {
        display: none;
      }
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
  labseqService = inject(LabseqService);

  n = signal<number>(0);
  response = signal<LabSeqResponse | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);

  calculate() {
    if (this.n() < 0) {
      this.error.set('Index must be non-negative');
      return;
    }

    this.loading.set(true);
    this.error.set(null);
    this.response.set(null);

    this.labseqService.getValue(this.n()).subscribe({
      next: (res) => {
        this.response.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Error fetching value');
        this.loading.set(false);
      }
    });
  }
}