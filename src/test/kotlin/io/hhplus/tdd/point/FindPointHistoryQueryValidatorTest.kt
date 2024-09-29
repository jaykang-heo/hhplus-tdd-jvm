package io.hhplus.tdd.point

import io.hhplus.tdd.point.query.FindPointHistoryQuery
import io.hhplus.tdd.point.validator.FindPointHistoryQueryValidator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random

class FindPointHistoryQueryValidatorTest {

    private val sut = FindPointHistoryQueryValidator()

    @Test
    @DisplayName("포인트 내역을 조회할떄 id가 0이면 에러를 반환한다")
    fun `when id is 0 in query, then throw error`() {
        // given
        val id = 0L
        val query = FindPointHistoryQuery(id)

        // when, then
        assertThrows<RuntimeException> { sut.validate(query) }
    }

    @Test
    @DisplayName("포인트 내역을 조회할때 id가 음수이면 에러를 반환한다")
    fun `when id is negative in query, then throw error`() {
        // given
        val id = -Random.nextLong(1, Long.MAX_VALUE)
        val query = FindPointHistoryQuery(id)

        // when, then
        assertThrows<RuntimeException> { sut.validate(query) }
    }

    @Test
    @DisplayName("포인트를 조회할때 id가 1 이상이면, 성공한다")
    fun `when id is greater or equal to 1, then succeed`() {
        // given
        val id = Random.nextLong(1, Long.MAX_VALUE)
        val query = FindPointHistoryQuery(id)

        // when, then
        assertDoesNotThrow { sut.validate(query) }
    }
}
