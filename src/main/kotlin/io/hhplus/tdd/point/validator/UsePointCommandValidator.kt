package io.hhplus.tdd.point.validator

import io.hhplus.tdd.point.command.UsePointCommand
import io.hhplus.tdd.point.ports.UserPointRepository
import org.springframework.stereotype.Component

@Component
class UsePointCommandValidator(
    private val userPointRepository: UserPointRepository
) {

    fun validate(command: UsePointCommand) {
        if (command.id < 1) {
            throw RuntimeException("id ${command.id} cannot be less than 1")
        }

        val savedPoint = userPointRepository.getById(command.id).point
        if (savedPoint < command.amount) {
            throw RuntimeException("${command.amount} is greater than saved point amount $savedPoint")
        }
    }
}
