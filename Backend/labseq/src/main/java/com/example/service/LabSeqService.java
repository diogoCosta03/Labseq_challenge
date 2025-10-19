package com.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class implementing the labseq sequence calculation with intelligent caching.
 *
 * Sequence definition:
 * - l(0) = 0
 * - l(1) = 1
 * - l(2) = 0
 * - l(3) = 1
 * - l(n) = l(n-4) + l(n-3) for n > 3
 *
 * Key Features:
 * - Thread-safe implementation using ConcurrentHashMap
 * - Bottom-up dynamic programming for optimal performance
 * - All intermediate calculations are cached
 * - Uses BigInteger to handle large values (l(100000) is enormous)
 * - Application-scoped singleton (shared across all requests)
 *
 */
@ApplicationScoped
public class LabSeqService {

    private static final Logger LOG = Logger.getLogger(LabSeqService.class);

    // Thread-safe cache that persists across all requests
    private final ConcurrentHashMap<Integer, BigInteger> cache;

    public LabSeqService() {
        this.cache = new ConcurrentHashMap<>();
        initializeBaseValues();
        LOG.info("LabSeqService initialized with base values");
    }

    /**
     * Initialize the cache with the four base values of the sequence.
     * These are the foundation for all subsequent calculations.
     */
    private void initializeBaseValues() {
        cache.put(0, BigInteger.ZERO);  // l(0) = 0
        cache.put(1, BigInteger.ONE);   // l(1) = 1
        cache.put(2, BigInteger.ZERO);  // l(2) = 0
        cache.put(3, BigInteger.ONE);   // l(3) = 1
    }

    /**
     * Calculate the labseq value at index n with caching.
     *
     * Algorithm:
     * 1. Validate input (n >= 0)
     * 2. Check if value is already cached (O(1) lookup)
     * 3. If not cached, calculate iteratively from the first missing value
     * 4. Cache all intermediate results for future use
     *
     * Time Complexity: O(n) for first calculation, O(1) for cached values
     * Space Complexity: O(n) for storing all values up to n
     *
     * @param n the index in the sequence (must be non-negative)
     * @return the labseq value at index n as BigInteger
     * @throws IllegalArgumentException if n is negative
     */
    public BigInteger calculate(int n) {
        if (n < 0) {
            LOG.warnf("Invalid index requested: %d", n);
            throw new IllegalArgumentException("Index must be non-negative, got: " + n);
        }

        BigInteger cached = cache.get(n);
        if (cached != null) {
            LOG.debugf("Cache hit for l(%d)", n);
            return cached;
        }

        LOG.debugf("Cache miss for l(%d), calculating...", n);
        return calculateWithCaching(n);
    }

    /**
     * Calculate values iteratively from the smallest missing index to n,
     * caching all intermediate results.
     *
     * This bottom-up approach ensures:
     * - All intermediate calculations benefit from caching
     * - No stack overflow (unlike recursive approach)
     * - Optimal performance for sequential access patterns
     *
     * Example: To calculate l(10), we ensure l(4) through l(10) are all cached
     */
    private BigInteger calculateWithCaching(int n) {
        // Find the starting point (first uncached value we need)
        int start = findStartIndex(n);

        // Calculate iteratively, caching each value
        // l(i) = l(i-4) + l(i-3) for i >= 4
        for (int i = start; i <= n; i++) {
            if (!cache.containsKey(i)) {
                // Both l(i-4) and l(i-3) are guaranteed to be cached
                BigInteger valueMinus4 = cache.get(i - 4);
                BigInteger valueMinus3 = cache.get(i - 3);
                BigInteger value = valueMinus4.add(valueMinus3);

                // Cache the result
                cache.put(i, value);

                LOG.tracef("Calculated and cached l(%d) = %s", i, value);
            }
        }

        return cache.get(n);
    }

    /**
     * Find the smallest index that needs to be calculated to compute l(n).
     * We need to ensure that l(n-4) and l(n-3) are available.
     *
     * Optimization: If the cache is continuous (no gaps), we start from
     * the next uncached value. Otherwise, we start from 4.
     *
     * @param n target index
     * @return starting index for calculation
     */
    private int findStartIndex(int n) {
        // Base cases are always cached, start from 4
        for (int i = 4; i <= n; i++) {
            if (!cache.containsKey(i)) {
                return i;
            }
        }
        return n; // All values up to n are cached (shouldn't reach here)
    }

    /**
            * Get the current size of the cache.
     * Useful for monitoring performance and cache efficiency.
     *
             * @return number of cached values
     */
    public int getCacheSize() {
        return cache.size();
    }

    /**
     * Clear the cache and reinitialize with base values.
     * Useful for testing or memory management.
     *
     * Note: In production, you might want to implement a smarter cache
     * eviction policy (LRU, TTL, etc.) for very large caches.
     */
    public void clearCache() {
        int previousSize = cache.size();
        cache.clear();
        initializeBaseValues();
        LOG.infof("Cache cleared (was %d entries, now %d)", previousSize, cache.size());
    }

    /**
     * Check if a value is cached.
     * Useful for testing and debugging.
     *
     * @param n the index to check
     * @return true if the value at index n is cached
     */
    public boolean isCached(int n) {
        return cache.containsKey(n);
    }

    /**
     * Get a cached value without triggering calculation.
     * Returns null if not cached.
     * Useful for testing.
     *
     * @param n the index to retrieve
     * @return cached value or null
     */
    public BigInteger getCachedValue(int n) {
        return cache.get(n);
    }

}