package io.hhplus.tdd.dummy

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.command.UsePointCommand
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.ports.UserPointRepository

class DummyUserPointRepository : UserPointRepository {
    override fun charge(command: ChargePointCommand): UserPoint {
        throw UnsupportedOperationException("DummyUserPointRepository: charge() should not be called.")
    }

    override fun use(command: UsePointCommand): UserPoint {
        throw UnsupportedOperationException("DummyUserPointRepository: use() should not be called.")
    }

    override fun getById(id: Long): UserPoint {
        throw UnsupportedOperationException("DummyUserPointRepository: getById() should not be called.")
    }
}
