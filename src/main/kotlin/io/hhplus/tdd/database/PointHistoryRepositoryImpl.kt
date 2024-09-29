package io.hhplus.tdd.database

import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.ports.PointHistoryRepository
import org.springframework.stereotype.Repository

@Repository
class PointHistoryRepositoryImpl(
    private val pointHistoryTable: PointHistoryTable
) : PointHistoryRepository {
    override fun findById(id: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(id)
    }
}
