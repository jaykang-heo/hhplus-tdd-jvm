package io.hhplus.tdd.database

import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.TransactionType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.random.Random

class PointHistoryTableTest {

    private val sut = PointHistoryTable()

    @Test
    @DisplayName("포인트 충전 히스토리를 삽입하면, 삽입한 포인트 히스토리를 반환한다")
    fun `when insert charge point history, then return inserted point history`() {
        // given
        val id = Random.nextLong()
        val amount = Random.nextLong()
        val transactionType = TransactionType.CHARGE
        val updateMillis = System.currentTimeMillis()

        // when
        val actual = sut.insert(id, amount, transactionType, updateMillis)

        // then
        val expected = PointHistory(0, id, transactionType, amount, updateMillis)
        assertThat(actual).usingRecursiveComparison().ignoringFields("id", "timeMillis").isEqualTo(expected)
        assertThat(actual.timeMillis).isNotNull()
        assertThat(actual.timeMillis).isCloseTo(expected.timeMillis, within(1000L))
    }

    @Test
    @DisplayName("포인트 사용 히스토리를 삽입하면, 삽입한 포인트 히스토리를 반환한다")
    fun `when insert use point history, then return inserted point history`() {
        // given
        val id = Random.nextLong()
        val amount = Random.nextLong()
        val transactionType = TransactionType.USE
        val updateMillis = System.currentTimeMillis()

        // when
        val actual = sut.insert(id, amount, transactionType, updateMillis)

        // then
        val expected = PointHistory(0, id, transactionType, amount, updateMillis)
        assertThat(actual).usingRecursiveComparison().ignoringFields("id", "timeMillis").isEqualTo(expected)
        assertThat(actual.timeMillis).isNotNull()
        assertThat(actual.timeMillis).isCloseTo(expected.timeMillis, within(1000L))
    }

    @Test
    @DisplayName("포인트 충전 히스토리가 저장되어 있으면, 포인트 충전 히스토리를 조회할 수 있다")
    fun `when select insert charge point history, then return point history list that includes charge point history`() {
        // given
        val id = Random.nextLong()
        val amount = Random.nextLong()
        val transactionType = TransactionType.CHARGE
        val updateMillis = System.currentTimeMillis()
        sut.insert(id, amount, transactionType, updateMillis)

        // when
        val actual = sut.selectAllByUserId(id)

        // then
        val expected = TransactionType.CHARGE
        assertThat(actual.first().type).isEqualTo(expected)
    }

    @Test
    @DisplayName("포인트 사용 히스토리가 저장되어 있으면, 포인트 충전 히스토리를 조회할 수 있다")
    fun `when select insert use point history, then return point history list that includes use point history`() {
        // given
        val id = Random.nextLong()
        val amount = Random.nextLong()
        val transactionType = TransactionType.USE
        val updateMillis = System.currentTimeMillis()
        sut.insert(id, amount, transactionType, updateMillis)

        // when
        val actual = sut.selectAllByUserId(id)

        // then
        val expected = TransactionType.USE
        assertThat(actual.first().type).isEqualTo(expected)
    }

    @Test
    @DisplayName("포인트 히스토리를 여러개 저장하면, 커서가 증감한다")
    fun `when insert many point histories, then cursor is incremented`() {
        // given
        val id = Random.nextLong()
        val amount = Random.nextLong()
        val transactionType = TransactionType.USE
        val updateMillis = System.currentTimeMillis()
        val savedPointHistory = sut.insert(id, amount, transactionType, updateMillis)

        // when
        val actual = sut.insert(id, amount, transactionType, updateMillis)

        // then
        assertThat(actual.id).isGreaterThan(savedPointHistory.id)
    }

    @Test
    @DisplayName("포인트 히스토리가 존재하는 유저를 조회하면, 포인트 내역 리스트를 반환한다")
    fun `when select existing point history, then return point history list`() {
        // given
        val id = Random.nextLong()
        val amount = Random.nextLong()
        val transactionType = TransactionType.USE
        val updateMillis = System.currentTimeMillis()
        sut.insert(id, amount, transactionType, updateMillis)

        // when
        val actual = sut.selectAllByUserId(id)

        // then
        val expected = listOf(PointHistory(0, id, transactionType, amount, updateMillis))
        assertThat(actual).usingRecursiveComparison().ignoringFields("id", "timeMillis").isEqualTo(expected)
    }

    @Test
    @DisplayName("포인트 히스토리가 존재하지 않는 유저를 조회하면, 빈 리스트를 반환한다")
    fun `when select not existing point history, then return empty list`() {
        // given
        val id = Random.nextLong()

        // when
        val actual = sut.selectAllByUserId(id)

        // then
        val expected = emptyList<PointHistory>()
        assertThat(actual).isEqualTo(expected)
    }
}
