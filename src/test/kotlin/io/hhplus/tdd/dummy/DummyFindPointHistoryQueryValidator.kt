package io.hhplus.tdd.dummy

import io.hhplus.tdd.point.query.FindPointHistoryQuery
import io.hhplus.tdd.point.validator.FindPointHistoryQueryValidator

class DummyFindPointHistoryQueryValidator : FindPointHistoryQueryValidator() {

    override fun validate(query: FindPointHistoryQuery) {}
}
