package io.hhplus.tdd.point.ports

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.command.UsePointCommand
import io.hhplus.tdd.point.model.UserPoint

interface UserPointRepository {
    fun charge(command: ChargePointCommand): UserPoint
    fun use(command: UsePointCommand): UserPoint
    fun getById(id: Long): UserPoint
}
