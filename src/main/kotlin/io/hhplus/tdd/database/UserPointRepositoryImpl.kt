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
    private val pointHistoryTable: PointHistoryTable,
    private val lockManager: LockManager
) : UserPointRepository {

    override fun charge(command: ChargePointCommand): UserPoint {
        return lockManager.executeWithLock(command.id) {
            val pointSum = getById(command.id).point + command.amount
            pointHistoryTable.insert(
                id = command.id,
                amount = command.amount,
                transactionType = TransactionType.CHARGE,
                updateMillis = System.currentTimeMillis()
            )

            userPointTable.insertOrUpdate(command.id, pointSum)
        }
    }

    override fun use(command: UsePointCommand): UserPoint {
        pointHistoryTable.insert(command.id, command.amount, TransactionType.USE, System.currentTimeMillis())
        val subtractedPoint = getById(command.id).point - command.amount
        return userPointTable.insertOrUpdate(command.id, subtractedPoint)
    }

    override fun getById(id: Long): UserPoint {
        return userPointTable.selectById(id)
    }
}
