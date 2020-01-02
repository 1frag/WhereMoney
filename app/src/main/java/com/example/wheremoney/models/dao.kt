package com.example.wheremoney.models

import androidx.room.*
import java.util.*


@Dao
interface DebtDao {
    @Query("SELECT * FROM debt")
    fun getAll(): List<Debt>
    @Insert
    fun insert(debt: Debt)
}

@Database(
    entities = [Debt::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun debtDao(): DebtDao
}