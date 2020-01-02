package com.example.wheremoney.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debt")
data class Debt(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo var partner: String,
    @ColumnInfo var infinityDebt: Boolean = true,
    @ColumnInfo var year: Int? = null,
    @ColumnInfo var month: Int? = null,
    @ColumnInfo var day: Int? = null,
    @ColumnInfo var quantity: Float,
    @ColumnInfo var currency: String
)
