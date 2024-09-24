package io.hhplus.tdd.fake

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.model.UserPoint
import java.util.concurrent.ConcurrentHashMap

class FakeUserPointTable : UserPointTable() {

    private val table = ConcurrentHashMap<Long, UserPoint>()

    override fun selectById(id: Long): UserPoint {
        return table[id] ?: UserPoint(id = id, point = 0, updateMillis = fixedUpdateMillis)
    }

    override fun insertOrUpdate(id: Long, amount: Long): UserPoint {
        val userPoint = UserPoint(id = id, point = amount, updateMillis = fixedUpdateMillis)
        table[id] = userPoint
        return userPoint
    }

    fun clear() {
        table.clear()
    }

    companion object {
        var fixedUpdateMillis: Long = 1_700_000_000_000L
    }
}
