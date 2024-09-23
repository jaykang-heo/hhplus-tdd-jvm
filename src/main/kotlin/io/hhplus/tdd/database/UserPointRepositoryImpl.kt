package io.hhplus.tdd.database

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.command.UsePointCommand
import io.hhplus.tdd.point.model.TransactionType
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.ports.UserPointRepository
import org.springframework.stereotype.Repository

@Repository
class UserPointRepositoryImpl(
    private val userPointTable: UserPointTable,
    private val pointHistoryTable: PointHistoryTable
) : UserPointRepository {
    override fun charge(command: ChargePointCommand): UserPoint {
        pointHistoryTable.insert(command.id, command.amount, TransactionType.CHARGE, System.currentTimeMillis())
        return userPointTable.insertOrUpdate(command.id, command.amount)
    }

    override fun use(command: UsePointCommand): UserPoint {
        pointHistoryTable.insert(command.id, command.amount, TransactionType.USE, System.currentTimeMillis())
        return userPointTable.insertOrUpdate(command.id, command.amount)
    }

    override fun getById(id: Long): UserPoint {
        return userPointTable.selectById(id)
    }
}
