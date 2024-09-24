package io.hhplus.tdd.stub

import io.hhplus.tdd.dummy.DummyUserPointRepository
import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.modifier.ChargePointCommandPreModifier

class StubChargePointCommandPreModifier : ChargePointCommandPreModifier(DummyUserPointRepository()) {
    override fun modify(command: ChargePointCommand): ChargePointCommand {
        return command
    }
}
