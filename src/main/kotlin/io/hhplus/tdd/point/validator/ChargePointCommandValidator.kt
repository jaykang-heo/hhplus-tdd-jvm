package io.hhplus.tdd.point.validator

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.ports.UserPointRepository
import org.springframework.stereotype.Component

@Component
class ChargePointCommandValidator(
    private val userPointRepository: UserPointRepository
) {
    fun validate(command: ChargePointCommand) {
        if (command.id < 1) {
            throw RuntimeException("id:${command.id} cannot be less than 1")
        }

        if (command.amount < 1) {
            throw RuntimeException("amount:${command.amount} cannot be less than 1")
        }

        val userPoint = userPointRepository.getById(command.id).point
        val isMax = userPoint == Long.MAX_VALUE
        if (isMax) {
            throw RuntimeException("${command.id} user charged point is max. $userPoint")
        }
    }
}
