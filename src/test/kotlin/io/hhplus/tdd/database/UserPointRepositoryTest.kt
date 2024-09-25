package io.hhplus.tdd.database

import io.hhplus.tdd.fake.FakeLockManager
import io.hhplus.tdd.fake.FakePointHistoryTable
import io.hhplus.tdd.fake.FakeUserPointTable
import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.command.UsePointCommand
import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.TransactionType
import io.hhplus.tdd.point.model.UserPoint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.random.Random

class UserPointRepositoryTest {

    private val fakeUserPointTable: UserPointTable = FakeUserPointTable()
    private val fakePointHistoryTable: PointHistoryTable = FakePointHistoryTable()
    private val fakeLockManager = FakeLockManager()
    private val sut = UserPointRepositoryImpl(fakeUserPointTable, fakePointHistoryTable, fakeLockManager)

    @Test
    @DisplayName("포인트를 충전하면, 포인트 내역과 유저 포인트를 저장하고 유저 포인트를 반환한다")
    fun `when charge point, then save point history, user point, and return saved user point`() {
        // given
        val id = Random.nextLong()
        val amount = 100L
        val command = ChargePointCommand(id, amount)

        // when
        val actual = sut.charge(command)

        // then
        val expectedUserPoint = UserPoint(id, amount, FakeUserPointTable.fixedUpdateMillis)
        assertThat(actual).usingRecursiveComparison().isEqualTo(expectedUserPoint)

        val histories = fakePointHistoryTable.selectAllByUserId(id)
        assertThat(histories).hasSize(1)
        val actualPointHistory = histories.first()
        val expectedPointHistory = PointHistory(1, id, TransactionType.CHARGE, amount, FakePointHistoryTable.fixedUpdateMillis)
        assertThat(actualPointHistory).usingRecursiveComparison().isEqualTo(expectedPointHistory)
    }

    @Test
    @DisplayName("포인트를 여러번 충전하면, 합산된다")
    fun `when charge point multiple times, then accumulate`() {
        // given
        val id = Random.nextLong()
        val amount = 100L
        val command = ChargePointCommand(id, amount)

        // when
        val actual = repeat(5) { sut.charge(command) }

        // then
        val expectedUserPoint = UserPoint(id, amount * 5, FakeUserPointTable.fixedUpdateMillis)
        assertThat(actual).usingRecursiveComparison().isEqualTo(expectedUserPoint)

        val histories = fakePointHistoryTable.selectAllByUserId(id)
        assertThat(histories).hasSize(5)
    }

    @Test
    @DisplayName("포인트를 사용하면, 포인트 내역과 유저 포인트를 저장하고 유저 포인트를 반환한다")
    fun `when use point, then save point history, user point, and return saved user point`() {
        // given
        val id = Random.nextLong()
        val amount = 100L
        val command = UsePointCommand(id, amount)

        // when
        val actual = sut.use(command)

        // then
        val expectedUserPoint = UserPoint(id, -amount, FakeUserPointTable.fixedUpdateMillis)
        assertThat(actual).usingRecursiveComparison().isEqualTo(expectedUserPoint)

        val histories = fakePointHistoryTable.selectAllByUserId(id)
        assertThat(histories).hasSize(1)
        val actualPointHistory = histories.first()
        val expectedPointHistory = PointHistory(1, id, TransactionType.USE, amount, FakePointHistoryTable.fixedUpdateMillis)
        assertThat(actualPointHistory).usingRecursiveComparison().isEqualTo(expectedPointHistory)
    }

    @Test
    @DisplayName("포인트를 여러번 사용하면, 모두 사용 처리되어 차감된다")
    fun `when use point multiple times, then deduct all points`() {
        // given
        val id = Random.nextLong()
        val amount = 100L
        val command = UsePointCommand(id, amount)

        // when
        repeat(5) { sut.use(command) }

        // then
        val expectedUserPoint = UserPoint(id, -amount * 5, FakeUserPointTable.fixedUpdateMillis)
        val actual = sut.getById(id)
        assertThat(actual).usingRecursiveComparison().isEqualTo(expectedUserPoint)
        val histories = fakePointHistoryTable.selectAllByUserId(id)
        assertThat(histories).hasSize(5)
    }

    @Test
    @DisplayName("포인트를 조회하면, 유저 포인트를 반환한다")
    fun `when find point, then return user point`() {
        // given
        val id = Random.nextLong()
        val amount = 100L
        val command = UsePointCommand(id, amount)
        sut.use(command)

        // when
        val actual = sut.getById(id)

        // then
        val expected = UserPoint(id, -amount, FakeUserPointTable.fixedUpdateMillis)
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }
}
