package io.hhplus.tdd.database

import io.hhplus.tdd.point.model.UserPoint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.random.Random

class UserPointTableTest {

    private val sut = UserPointTable()

    @Test
    @DisplayName("포인트를 삽입하면, 정상적으로 저장한다")
    fun `when insert point, then save point successfully`() {
        // given
        val id = Random.nextLong()
        val amount = Random.nextLong()

        // when
        val actual = sut.insertOrUpdate(id, amount)

        // then
        val expected = UserPoint(id, amount, System.currentTimeMillis())
        assertThat(actual).usingRecursiveComparison().ignoringFields("updateMillis").isEqualTo(expected)
        assertThat(actual.updateMillis).isNotNull()
        assertThat(actual.updateMillis).isCloseTo(expected.updateMillis, within(1000L))
    }

    @Test
    @DisplayName("이미 존재하는 포인트를 다시 한번 삽입하면, 업데이트한다")
    fun `when insert point on existing point, then update successfully`() {
        // given
        val id = Random.nextLong()
        val amount = Random.nextLong()
        val expectedAmount = Random.nextLong()
        val savedUserPoint = sut.insertOrUpdate(id, amount)
        assertThat(savedUserPoint.point).isEqualTo(amount)

        // when
        val actual = sut.insertOrUpdate(id, expectedAmount)

        // then
        val expected = UserPoint(id, expectedAmount, System.currentTimeMillis())
        assertThat(actual).usingRecursiveComparison().ignoringFields("updateMillis").isEqualTo(expected)
        assertThat(actual.updateMillis).isNotNull()
        assertThat(actual.updateMillis).isCloseTo(expected.updateMillis, within(1000L))
    }

    @Test
    @DisplayName("저장된 포인트를 조회하면, 저장된 포인트를 반환한다")
    fun `when select existing point, then return saved point`() {
        // given
        val id = Random.nextLong()
        val amount = Random.nextLong()
        sut.insertOrUpdate(id, amount)

        // when
        val actual = sut.selectById(id)

        // then
        val expected = UserPoint(id, amount, System.currentTimeMillis())
        assertThat(actual).usingRecursiveComparison().ignoringFields("updateMillis").isEqualTo(expected)
        assertThat(actual.updateMillis).isNotNull()
        assertThat(actual.updateMillis).isCloseTo(expected.updateMillis, within(1000L))
    }

    @Test
    @DisplayName("저장되지 않은 포인트를 조회하면, 빈 포인트를 반환한다")
    fun `when select not existing point, then return empty user point`() {
        // given
        val id = Random.nextLong()

        // when
        val actual = sut.selectById(id)

        // then
        val expected = UserPoint(id, 0, System.currentTimeMillis())
        assertThat(actual).usingRecursiveComparison().ignoringFields("updateMillis").isEqualTo(expected)
        assertThat(actual.updateMillis).isNotNull()
        assertThat(actual.updateMillis).isCloseTo(expected.updateMillis, within(1000L))
    }
}
