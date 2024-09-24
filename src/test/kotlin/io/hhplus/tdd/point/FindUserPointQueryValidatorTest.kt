package io.hhplus.tdd.point

import io.hhplus.tdd.point.query.FindUserPointQuery
import io.hhplus.tdd.point.validator.FindUserPointQueryValidator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random

class FindUserPointQueryValidatorTest {

    private val sut = FindUserPointQueryValidator()

    @Test
    @DisplayName("유저 포인트를 조회할때 아이디가 0 이면, 에러를 반환한다 ")
    fun `when id is 0, then throw error`() {
        // given
        val id = 0L
        val query = FindUserPointQuery(id)

        // when, then
        assertThrows<RuntimeException> { sut.validate(query) }
    }

    @Test
    @DisplayName("유저 포인트를 조회할때 아이디가 음수이면, 에러를 반환한다")
    fun `when id is negative, then throw error`() {
        // given
        val id = -Random.nextLong(1, Long.MAX_VALUE)
        val query = FindUserPointQuery(id)

        // when, then
        assertThrows<RuntimeException> { sut.validate(query) }
    }

    @Test
    @DisplayName("유저 포인트를 조회할때 아이디가 1 이상이면, 성공한다")
    fun `when id greater or equal to 1, then succeed`() {
        // given
        val id = Random.nextLong(1, Long.MAX_VALUE)
        val query = FindUserPointQuery(id)

        // when, then
        assertDoesNotThrow { sut.validate(query) }
    }
}
