package io.hhplus.tdd.point

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.command.UsePointCommand
import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.ports.IPointService
import io.hhplus.tdd.point.ports.PointHistoryRepository
import io.hhplus.tdd.point.ports.UserPointRepository
import io.hhplus.tdd.point.query.FindPointHistoryQuery
import io.hhplus.tdd.point.query.FindUserPointQuery
import io.hhplus.tdd.point.validator.FindPointHistoryQueryValidator
import io.hhplus.tdd.point.validator.FindUserPointQueryValidator
import org.springframework.stereotype.Service

@Service
class PointService(
    private val userPointRepository: UserPointRepository,
    private val pointHistoryRepository: PointHistoryRepository,
    private val findUserPointQueryValidator: FindUserPointQueryValidator,
    private val findPointHistoryQueryValidator: FindPointHistoryQueryValidator
) : IPointService {

    override fun charge(command: ChargePointCommand): UserPoint {
        return userPointRepository.charge(command)
    }

    override fun use(command: UsePointCommand): UserPoint {
        return userPointRepository.use(command)
    }

    override fun findUserPoint(query: FindUserPointQuery): UserPoint {
        findUserPointQueryValidator.validate(query)
        return userPointRepository.getById(query.id)
    }

    override fun findPointHistoryList(query: FindPointHistoryQuery): List<PointHistory> {
        findPointHistoryQueryValidator.validate(query)
        return pointHistoryRepository.findById(query.id)
    }
}
