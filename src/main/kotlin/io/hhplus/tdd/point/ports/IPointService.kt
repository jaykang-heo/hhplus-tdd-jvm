package io.hhplus.tdd.point.ports

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.command.UsePointCommand
import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.query.FindPointHistoryQuery
import io.hhplus.tdd.point.query.FindUserPointQuery

interface IPointService {
    fun charge(command: ChargePointCommand): UserPoint
    fun use(command: UsePointCommand): UserPoint
    fun findUserPoint(query: FindUserPointQuery): UserPoint
    fun findPointHistoryList(query: FindPointHistoryQuery): List<PointHistory>
}
