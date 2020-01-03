package com.example.wheremoney.interfaces

import com.example.wheremoney.helpers.AppDatabase
import com.example.wheremoney.models.Debt

interface HeadDriverInterface {
    fun setTextInTextView(text: String, tv: TextViewType)
    fun DBExecutor(
        longFunction: (db: AppDatabase) -> Map<String, List<Debt>>,
        postFunction: (map: Map<String, List<Debt>>) -> Unit
    )
    fun getResourceString(r: ResourceType): String
}

enum class ResourceType {
    BUDGETFS, OWETOMEFS, IMUSTFS, EXCHANGERATESFS, SORRY,
}

enum class TextViewType {
    BUDGETTV, OWETOMETV, IMUSTTV, EXCHANGERATESTV,
}
