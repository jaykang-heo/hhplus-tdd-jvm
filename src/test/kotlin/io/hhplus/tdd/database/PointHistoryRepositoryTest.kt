package io.hhplus.tdd.database

import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.TransactionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.random.Random

class PointHistoryRepositoryTest {

    private val mockPointHistoryTable = mock(PointHistoryTable::class.java)
    private val sut = PointHistoryRepositoryImpl(mockPointHistoryTable)

    @Test
    @DisplayName("포인트 내역을 조회하면, 포인트 내역 리스트를 반환한다")
    fun `when find point history, then return point history list`() {
        // given
        val id = Random.nextLong()
        val expected = listOf(PointHistory(id, Random.nextLong(), TransactionType.CHARGE, Random.nextLong(), System.currentTimeMillis()))
        `when`(mockPointHistoryTable.selectAllByUserId(id)).thenReturn(expected)

        // when
        val actual = sut.findById(id)

        // then
        assertThat(actual).isEqualTo(expected)
    }
}
