package io.hhplus.tdd.point

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.validator.ChargePointCommandValidator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random

class ChargePointCommandValidatorTest {

    private val sut = ChargePointCommandValidator()

    @Test
    @DisplayName("포인트 충전 명령의 아이디가 1보다 작으면 에러를 반환한다")
    fun `when id in charge point command is less than 1, then throw error`() {
        // given
        val id = -Random.nextLong(1, Long.MAX_VALUE)
        val amount = 1L
        val command = ChargePointCommand(id, amount)

        // when, then
        assertThrows<RuntimeException> { sut.validate(command) }
    }

    @Test
    @DisplayName("포인트 충전 명령의 아이디가 0이면, 에러를 반환한다")
    fun `when id in charge point command is 0, then throw error`() {
        val id = 0L
        val amount = 1L
        val command = ChargePointCommand(id, amount)

        // when, then
        assertThrows<RuntimeException> { sut.validate(command) }
    }

    @Test
    @DisplayName("포인트 충전 명령의 아이디가 1 이상이면, 성공한다")
    fun `when id in charge point command is greater or equal to 1, then succeed`() {
        val id = Random.nextLong(1, Long.MAX_VALUE)
        val amount = 1L
        val command = ChargePointCommand(id, amount)

        // when, then
        assertDoesNotThrow { sut.validate(command) }
    }

    @Test
    @DisplayName("포인트 충전 명령의 금액이 1 보다 작으면, 에러를 반환한다")
    fun `when amount in charge point command is less than 1, then throw error`() {
        // given
        val id = 1L
        val amount = -Random.nextLong(1, Long.MAX_VALUE)
        val command = ChargePointCommand(id, amount)

        // when, then
        assertThrows<RuntimeException> { sut.validate(command) }
    }

    @Test
    @DisplayName("포인트 충전 명령의 금액이 1 이상이면, 성공한다")
    fun `when amount in charge point command is greater or equal to 1, then succeed`() {
        val id = 1L
        val amount = Random.nextLong(1, Long.MAX_VALUE)
        val command = ChargePointCommand(id, amount)

        // when, then
        assertDoesNotThrow { sut.validate(command) }
    }
}
