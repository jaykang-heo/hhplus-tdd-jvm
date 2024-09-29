package io.hhplus.tdd.point

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.modifier.ChargePointCommandPreModifier
import io.hhplus.tdd.point.ports.UserPointRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.random.Random

class ChargePointCommandPreModifierTest {
    private val mockUserPointRepository = mock(UserPointRepository::class.java)
    private val sut = ChargePointCommandPreModifier(mockUserPointRepository)

    @Test
    @DisplayName("충전하려는 포인트가 최대 금액을 초과할시에 최대치까지만 충전하도록 명령을 수정한다")
    fun `when charge command amount will make user point exceed limit, then revise command amount`() {
        // given
        val id = 1L
        val amount = Random.nextLong(1, Long.MAX_VALUE)
        val command = ChargePointCommand(id, amount)
        val stubUserPoint = UserPoint(1, Long.MAX_VALUE, System.currentTimeMillis())
        `when`(mockUserPointRepository.getById(id)).thenReturn(stubUserPoint)

        // when
        val actual = sut.modify(command)

        // then
        assertThat(actual.amount).isEqualTo(0)
    }

    @Test
    @DisplayName("충전하려는 포인트가 최대 금액을 초과하지 않으면, 충전 명령 금액을 수정하지 않는다")
    fun `when charge command amount will not exceed limit, then do not revise command amount`() {
        // given
        val id = 1L
        val amount = Random.nextLong(1, 100)
        val command = ChargePointCommand(id, amount)
        val stubUserPoint = UserPoint(1, 100, System.currentTimeMillis())
        `when`(mockUserPointRepository.getById(id)).thenReturn(stubUserPoint)

        // when
        val actual = sut.modify(command)

        // then
        assertThat(actual.amount).isEqualTo(amount)
    }
}
