package com.example.wheremoney.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debt")
data class Debt(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo var partner: String,
    @ColumnInfo var date: String,
    @ColumnInfo var quantity: Float,
    @ColumnInfo var currency: String
)
