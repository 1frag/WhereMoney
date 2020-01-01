package com.example.wheremoney.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

@Entity(tableName = "debt")
data class Debt(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo var from: String,
    @ColumnInfo var to: String,
    @ColumnInfo var date: Int,
    @ColumnInfo var type: Int,
    @ColumnInfo var quantity: Int,
    @ColumnInfo var currency: String
)
