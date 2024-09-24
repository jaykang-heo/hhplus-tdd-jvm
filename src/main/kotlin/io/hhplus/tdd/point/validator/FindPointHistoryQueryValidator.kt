package io.hhplus.tdd.point.validator

import io.hhplus.tdd.point.query.FindPointHistoryQuery
import org.springframework.stereotype.Component

@Component
class FindPointHistoryQueryValidator {
    fun validate(query: FindPointHistoryQuery) {
        if (query.id < 1) {
            throw RuntimeException("id:${query.id} must be greater than or equal to 1")
        }
    }
}
