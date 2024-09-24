package io.hhplus.tdd.dummy

import io.hhplus.tdd.point.query.FindUserPointQuery
import io.hhplus.tdd.point.validator.FindUserPointQueryValidator

class DummyFindUserPointQueryValidator : FindUserPointQueryValidator() {

    override fun validate(query: FindUserPointQuery) {
    }
}
