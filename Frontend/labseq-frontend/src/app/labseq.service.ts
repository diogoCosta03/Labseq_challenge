import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LabSeqResponse {
  index: number;
  value: string;
  calculationTimeMs: number;
  cacheSize: number;
}

export interface CacheStatsResponse {
  cacheSize: number;
}

export interface MessageResponse {
  message: string;
  cacheSize: number;
}

@Injectable({
  providedIn: 'root',
})
export class LabseqService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/labseq';

  /**
   * Calculate labseq value at index n
   */
  getValue(n: number): Observable<LabSeqResponse> {
    return this.http.get<LabSeqResponse>(`${this.baseUrl}/${n}`);
  }

  /**
   * Get cache statistics
   */
  getCacheStats(): Observable<CacheStatsResponse> {
    return this.http.get<CacheStatsResponse>(`${this.baseUrl}/cache/stats`);
  }

  /**
   * Clear the cache
   */
  clearCache(): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${this.baseUrl}/cache`);
  }
}