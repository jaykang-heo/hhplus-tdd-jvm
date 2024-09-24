package io.hhplus.tdd.point.validator

import io.hhplus.tdd.point.command.ChargePointCommand
import org.springframework.stereotype.Component

@Component
class ChargePointCommandValidator {
    fun validate(command: ChargePointCommand) {
        if (command.id < 1) {
            throw RuntimeException("id:${command.id} cannot be less than 1")
        }
    }
}
