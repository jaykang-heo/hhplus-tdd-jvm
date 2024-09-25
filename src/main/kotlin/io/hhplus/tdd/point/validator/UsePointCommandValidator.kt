package io.hhplus.tdd.point.validator

import io.hhplus.tdd.point.command.UsePointCommand
import org.springframework.stereotype.Component

@Component
class UsePointCommandValidator {

    fun validate(command: UsePointCommand) {
        if (command.id < 1) {
            throw RuntimeException("id ${command.id} cannot be less than 1")
        }
    }
}
