package com.example.util

import com.example.data.model.CandleData

object RsiCalculator {
    /**
     * Calculates Relative Strength Index (RSI) for each candle in the list using Wilder's smoothed method.
     * Returns a list of Float values (0f to 100f) corresponding to each candle index.
     */
    fun calculateRsiSeries(candles: List<CandleData>, period: Int = 14): List<Float> {
        if (candles.isEmpty()) return emptyList()
        val effectivePeriod = if (candles.size <= period) maxOf(2, candles.size / 2) else period

        val rsiSeries = mutableListOf<Float>()
        val closes = candles.map { it.close }

        var lastAvgGain = 0f
        var lastAvgLoss = 0f

        for (i in closes.indices) {
            if (i == 0) {
                rsiSeries.add(50f)
                continue
            }

            if (i < effectivePeriod) {
                var gains = 0f
                var losses = 0f
                for (j in 1..i) {
                    val change = closes[j] - closes[j - 1]
                    if (change > 0) gains += change else losses += -change
                }
                val avgGain = gains / i
                val avgLoss = losses / i
                val rsi = calculateRsiValue(avgGain, avgLoss)
                rsiSeries.add(rsi)
                if (i == effectivePeriod - 1) {
                    lastAvgGain = avgGain
                    lastAvgLoss = avgLoss
                }
            } else {
                val change = closes[i] - closes[i - 1]
                val currentGain = if (change > 0) change else 0f
                val currentLoss = if (change < 0) -change else 0f

                // Wilder's smoothing technique
                lastAvgGain = ((lastAvgGain * (effectivePeriod - 1)) + currentGain) / effectivePeriod
                lastAvgLoss = ((lastAvgLoss * (effectivePeriod - 1)) + currentLoss) / effectivePeriod

                val rsi = calculateRsiValue(lastAvgGain, lastAvgLoss)
                rsiSeries.add(rsi)
            }
        }
        return rsiSeries
    }

    private fun calculateRsiValue(avgGain: Float, avgLoss: Float): Float {
        return when {
            avgLoss == 0f && avgGain == 0f -> 50f
            avgLoss == 0f -> 100f
            avgGain == 0f -> 0f
            else -> {
                val rs = avgGain / avgLoss
                (100f - (100f / (1f + rs))).coerceIn(0f, 100f)
            }
        }
    }
}
