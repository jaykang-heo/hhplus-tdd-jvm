package io.hhplus.tdd.point

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.ports.UserPointRepository
import io.hhplus.tdd.point.validator.ChargePointCommandValidator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.random.Random

class ChargePointCommandValidatorTest {

    private val mockUserPointRepository = mock(UserPointRepository::class.java)
    private val sut = ChargePointCommandValidator(mockUserPointRepository)

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
        val subUserPoint = UserPoint(command.id, 1L, System.currentTimeMillis())
        `when`(mockUserPointRepository.getById(command.id)).thenReturn(subUserPoint)

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
        val subUserPoint = UserPoint(command.id, 1L, System.currentTimeMillis())
        `when`(mockUserPointRepository.getById(command.id)).thenReturn(subUserPoint)

        // when, then
        assertDoesNotThrow { sut.validate(command) }
    }

    @Test
    @DisplayName("유저에게 충전된 포인트 금액이 최대치이면, 에러를 반환한다")
    fun `when charged point is max, then throw error`() {
        val id = 1L
        val amount = Random.nextLong(1, Long.MAX_VALUE)
        val command = ChargePointCommand(id, amount)
        val subUserPoint = UserPoint(command.id, Long.MAX_VALUE, System.currentTimeMillis())
        `when`(mockUserPointRepository.getById(command.id)).thenReturn(subUserPoint)

        // when, then
        assertThrows<RuntimeException> { sut.validate(command) }
    }

    @Test
    @DisplayName("유저에게 충전된 포인트 금액이 최대치가 아니면, 성공한다")
    fun `when charged point is not max, then succeed`() {
        val id = 1L
        val amount = Random.nextLong(1, Long.MAX_VALUE)
        val command = ChargePointCommand(id, amount)
        val subUserPoint = UserPoint(command.id, 1L, System.currentTimeMillis())
        `when`(mockUserPointRepository.getById(command.id)).thenReturn(subUserPoint)

        // when, then
        assertDoesNotThrow { sut.validate(command) }
    }
}
