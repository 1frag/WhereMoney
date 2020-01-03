package com.example.wheremoney.helpers

import androidx.room.*
import com.example.wheremoney.models.Debt


@Dao
interface DebtDao {
    @Query("SELECT * FROM debt")
    fun getAll(): List<Debt>
    @Query("SELECT * FROM debt WHERE quantity < 0")
    fun getIMust(): List<Debt>
    @Query("SELECT * FROM debt WHERE quantity > 0")
    fun getOweToMe(): List<Debt>
    @Insert
    fun insert(debt: Debt)
}

@Database(
    entities = [Debt::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun debtDao(): DebtDao
}