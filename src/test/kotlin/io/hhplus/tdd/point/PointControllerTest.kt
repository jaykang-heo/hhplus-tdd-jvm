package io.hhplus.tdd.point

import com.fasterxml.jackson.databind.ObjectMapper
import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.command.UsePointCommand
import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.TransactionType
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.ports.IPointService
import io.hhplus.tdd.point.query.FindPointHistoryQuery
import io.hhplus.tdd.point.query.FindUserPointQuery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PointController::class)
class PointControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var pointService: IPointService

    @Test
    @DisplayName("포인트를 조회하면, 유저 포인트를 반환한다")
    fun `when find user point with user id, then return user point`() {
        // given
        val userId = 1L
        val expectedUserPoint = UserPoint(id = userId, point = 1000L, updateMillis = 1_700_000_000_000L)
        `when`(pointService.findUserPoint(FindUserPointQuery(userId))).thenReturn(expectedUserPoint)

        // when
        val result = mockMvc.perform(get("/point/$userId"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()

        // then
        val responseContent = result.response.contentAsString
        val actualUserPoint = objectMapper.readValue(responseContent, UserPoint::class.java)
        assertThat(actualUserPoint).isEqualTo(expectedUserPoint)
    }

    @Test
    @DisplayName("포인트 내역을 유저 아이디로 조회하면, 포인트 내역을 반환한다")
    fun `when get point histories with user id, then return point history list`() {
        // given
        val userId = 2L
        val histories = listOf(
            PointHistory(id = 1L, userId = userId, amount = 500L, type = TransactionType.CHARGE, timeMillis = 1_700_000_000_000L),
            PointHistory(id = 2L, userId = userId, amount = 200L, type = TransactionType.USE, timeMillis = 1_700_000_000_500L)
        )
        `when`(pointService.findPointHistoryList(FindPointHistoryQuery(userId))).thenReturn(histories)

        // when
        val result = mockMvc.perform(get("/point/$userId/histories"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()

        // then
        val responseContent = result.response.contentAsString
        val actualHistories = objectMapper.readValue(responseContent, Array<PointHistory>::class.java).toList()
        assertThat(actualHistories).isEqualTo(histories)
    }

    @Test
    @DisplayName("포인트를 충전하면, 유저 포인트가 반환된다")
    fun `when charge point, then return user point`() {
        // given
        val userId = 3L
        val chargeAmount = 300L
        val command = ChargePointCommand(userId, chargeAmount)
        val expectedUserPoint = UserPoint(id = userId, point = chargeAmount, updateMillis = 1_700_000_001_000L)

        `when`(pointService.charge(command)).thenReturn(expectedUserPoint)

        // when
        val result = mockMvc.perform(
            patch("/point/$userId/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chargeAmount))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()

        // then
        val responseContent = result.response.contentAsString
        val actualUserPoint = objectMapper.readValue(responseContent, UserPoint::class.java)
        assertThat(actualUserPoint).isEqualTo(expectedUserPoint)
    }

    @Test
    @DisplayName("포인트를 사용하면, 유저 포인트를 반환한다")
    fun `when use point, then return user point`() {
        // given
        val userId = 4L
        val useAmount = 150L
        val command = UsePointCommand(userId, useAmount)
        val expectedUserPoint = UserPoint(id = userId, point = -useAmount, updateMillis = 1_700_000_002_000L)
        `when`(pointService.use(command)).thenReturn(expectedUserPoint)

        // when
        val result = mockMvc.perform(
            patch("/point/$userId/use")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(useAmount))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()

        // then
        val responseContent = result.response.contentAsString
        val actualUserPoint = objectMapper.readValue(responseContent, UserPoint::class.java)
        assertThat(actualUserPoint).isEqualTo(expectedUserPoint)
    }
}
