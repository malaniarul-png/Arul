package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val symbol: String,
    val addedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "paper_trades")
data class PaperTradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val isBuy: Boolean,
    val quantity: Int,
    val buyPrice: Double,
    val targetPrice: Double,
    val stopLossPrice: Double,
    val exitPrice: Double? = null,
    val isOpen: Boolean = true,
    val pnl: Double = 0.0,
    val openedAt: Long = System.currentTimeMillis(),
    val closedAt: Long? = null
)

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY addedAt DESC")
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE symbol = :symbol")
    suspend fun delete(symbol: String)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE symbol = :symbol)")
    suspend fun isFavorite(symbol: String): Boolean
}

@Dao
interface PaperTradeDao {
    @Query("SELECT * FROM paper_trades ORDER BY openedAt DESC")
    fun getAllTrades(): Flow<List<PaperTradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: PaperTradeEntity): Long

    @Query("UPDATE paper_trades SET isOpen = 0, exitPrice = :exitPrice, pnl = :pnl, closedAt = :closedAt WHERE id = :id")
    suspend fun closeTrade(id: Long, exitPrice: Double, pnl: Double, closedAt: Long)

    @Query("DELETE FROM paper_trades")
    suspend fun clearAllTrades()
}

@Database(
    entities = [WatchlistEntity::class, PaperTradeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
    abstract fun paperTradeDao(): PaperTradeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stock_market_ai_db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
