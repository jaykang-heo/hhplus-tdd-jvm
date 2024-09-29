package io.hhplus.tdd.fake

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.TransactionType

class FakePointHistoryTable : PointHistoryTable() {

    private val table = mutableListOf<PointHistory>()
    private var cursor: Long = 1L

    override fun insert(
        id: Long,
        amount: Long,
        transactionType: TransactionType,
        updateMillis: Long
    ): PointHistory {
        val history = PointHistory(
            id = cursor++,
            userId = id,
            amount = amount,
            type = transactionType,
            timeMillis = fixedUpdateMillis
        )
        table.add(history)
        return history
    }

    override fun selectAllByUserId(userId: Long): List<PointHistory> {
        return table.filter { it.userId == userId }
    }

    companion object {
        var fixedUpdateMillis: Long = 1_700_000_000_000L
    }
}
