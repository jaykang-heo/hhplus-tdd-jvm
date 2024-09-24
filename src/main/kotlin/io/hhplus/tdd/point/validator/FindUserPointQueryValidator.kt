package io.hhplus.tdd.point.validator

import io.hhplus.tdd.point.query.FindUserPointQuery
import org.springframework.stereotype.Component

@Component
class FindUserPointQueryValidator {
    fun validate(query: FindUserPointQuery) {
        if (query.id < 0) {
            throw RuntimeException("id:${query.id} must be greater than 0")
        }
    }
}
