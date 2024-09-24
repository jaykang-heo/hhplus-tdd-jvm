package io.hhplus.tdd.point

import io.hhplus.tdd.dummy.DummyFindPointHistoryQueryValidator
import io.hhplus.tdd.dummy.DummyFindUserPointQueryValidator
import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.command.UsePointCommand
import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.TransactionType
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.ports.PointHistoryRepository
import io.hhplus.tdd.point.ports.UserPointRepository
import io.hhplus.tdd.point.query.FindPointHistoryQuery
import io.hhplus.tdd.point.query.FindUserPointQuery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.random.Random

class PointServiceTest {

    private val mockUserPointRepository = mock(UserPointRepository::class.java)
    private val mockPointHistoryRepository = mock(PointHistoryRepository::class.java)
    private val dummyFindUserPointQueryValidator = DummyFindUserPointQueryValidator()
    private val dummyFindPointHistoryQueryValidator = DummyFindPointHistoryQueryValidator()
    private val sut = PointService(
        mockUserPointRepository,
        mockPointHistoryRepository,
        dummyFindUserPointQueryValidator,
        dummyFindPointHistoryQueryValidator
    )

    @Test
    @DisplayName("포인트 충전 명령을 내리면, 저장 명령을 내린 포인트 금액과 함께 유저 포인트를 반환한다")
    fun `when charge command, then return user point with intended amount`() {
        // given
        val id = Random.nextLong()
        val amount = Random.nextLong()
        val command = ChargePointCommand(id, amount)
        val expected = UserPoint(id, amount, System.currentTimeMillis())
        `when`(mockUserPointRepository.charge(command)).thenReturn(expected)

        // when
        val actual = sut.charge(command)

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @DisplayName("포인트 사용 명령을 내리면, 사용 명령을 내린 포인트 금액과 함께 유저 포인트를 반환한다")
    fun `when use command, then return user point with intended amount`() {
        // given
        val id = Random.nextLong()
        val amount = Random.nextLong()
        val command = UsePointCommand(id, amount)
        val expected = UserPoint(id, amount, System.currentTimeMillis())
        `when`(mockUserPointRepository.use(command)).thenReturn(expected)

        // when
        val actual = sut.use(command)

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @DisplayName("포인트를 조회하면, 유저 포인트가 반환된다")
    fun `when find point, then return user point`() {
        // given
        val id = Random.nextLong()
        val amount = Random.nextLong()
        val query = FindUserPointQuery(id)
        val expected = UserPoint(id, amount, System.currentTimeMillis())
        `when`(mockUserPointRepository.getById(id)).thenReturn(expected)

        // when
        val actual = sut.findUserPoint(query)

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @DisplayName("포인트 히스토리를 조회하면, 포인트 히스토리 리스트가 반환된다")
    fun `when find point history, then return point history list`() {
        // given
        val id = Random.nextLong()
        val userId = Random.nextLong()
        val amount = Random.nextLong()
        val transactionType = TransactionType.CHARGE
        val query = FindPointHistoryQuery(userId)
        val expected = listOf(PointHistory(id, userId, transactionType, amount, System.currentTimeMillis()))
        `when`(mockPointHistoryRepository.findById(userId)).thenReturn(expected)

        // when
        val actual = sut.findPointHistoryList(query)

        // then
        assertThat(actual).isEqualTo(expected)
    }
}
