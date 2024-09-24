package io.hhplus.tdd.point.ports

import io.hhplus.tdd.point.model.PointHistory

interface PointHistoryRepository {
    fun findById(id: Long): List<PointHistory>
}
