package io.hhplus.tdd.point.modifier

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.ports.UserPointRepository
import org.springframework.stereotype.Component

@Component
class ChargePointCommandPreModifier(
    private val userPointRepository: UserPointRepository
) {
    fun modify(command: ChargePointCommand): ChargePointCommand {
        val userPoint = userPointRepository.getById(command.id).point

        if (command.amount > Long.MAX_VALUE - userPoint) {
            return ChargePointCommand(command.id, Long.MAX_VALUE - userPoint)
        }

        return command
    }
}
