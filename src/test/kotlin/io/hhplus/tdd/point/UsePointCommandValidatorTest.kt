package io.hhplus.tdd.point

import io.hhplus.tdd.point.command.UsePointCommand
import io.hhplus.tdd.point.validator.UsePointCommandValidator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random

class UsePointCommandValidatorTest {

    private val sut = UsePointCommandValidator()

    @Test
    @DisplayName("사용 명령의 유저 아이디가 0이면, 에러를 반환한다")
    fun `when id in use command is 0, then throw error`() {
        // given
        val id = 0L
        val amount = 100L
        val command = UsePointCommand(id, amount)

        // when, then
        assertThrows<RuntimeException> { sut.validate(command) }
    }

    @Test
    @DisplayName("사용 명령의 유저 아이디가 음수이면, 에러를 반환한다")
    fun `when id in use command is negative, then throw error`() {
        // given
        val id = -1L
        val amount = 100L
        val command = UsePointCommand(id, amount)

        // when, then
        assertThrows<RuntimeException> { sut.validate(command) }
    }

    @Test
    @DisplayName("사용 명령의 유저 아이디가 양수이면, 성공한다")
    fun `when id in use command is positive, then succeed`() {
        // given
        val id = Random.nextLong(1, Long.MAX_VALUE)
        val amount = 100L
        val command = UsePointCommand(id, amount)

        // when, then
        assertDoesNotThrow { sut.validate(command) }
    }
}
