package io.hhplus.tdd.dummy

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.validator.ChargePointCommandValidator

class DummyChargePointCommandValidator : ChargePointCommandValidator(DummyUserPointRepository()) {
    override fun validate(command: ChargePointCommand) {
    }
}
