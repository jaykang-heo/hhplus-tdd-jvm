package io.hhplus.tdd.point

import com.fasterxml.jackson.databind.ObjectMapper
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.model.UserPoint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@SpringBootTest
@AutoConfigureMockMvc
class PointControllerIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val userPointTable: UserPointTable,
    @Autowired private val objectMapper: ObjectMapper
) {

    @Test
    @DisplayName("한명의 유저에게 동시에 여러번 충전했을때, 정상적으로 충전한다")
    fun `when charge multiple times at the same time for a user, then charge successfully`() {
        // given
        val userId = Random.nextLong(1, Long.MAX_VALUE)
        val chargeAmount = 100L
        val numberOfOperations = 10

        // when
        val futures = (1..numberOfOperations).map {
            CompletableFuture.runAsync {
                mockMvc.perform(
                    patch("/point/$userId/charge")
                        .content(chargeAmount.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
            }
        }

        CompletableFuture.allOf(*futures.toTypedArray()).get(10, TimeUnit.SECONDS)

        // then
        val getResult = mockMvc.perform(
            get("/point/$userId")
        )
            .andExpect(status().isOk)
            .andReturn()

        val responseContent = getResult.response.contentAsString
        val userPoint = objectMapper.readValue(responseContent, UserPoint::class.java)
        val actual = userPoint.point
        val expected = chargeAmount * numberOfOperations
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @DisplayName("한명의 유저에게 동시에 여러 금액으로 충전했을때, 정상적으로 충전한다")
    fun `when charge multiple times with different amount at the same time for a user, then charge successfully`() {
        // given
        val userId = Random.nextLong(1, Long.MAX_VALUE)
        userPointTable.insertOrUpdate(userId, 0)
        val chargeAmounts = listOf(50L, 100L, 150L, 200L, 250L)

        // when
        val expectedTotal = chargeAmounts.sum()
        val futures = chargeAmounts.map { amount ->
            CompletableFuture.runAsync {
                mockMvc.perform(
                    patch("/point/$userId/charge")
                        .content(amount.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
            }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).get(10, TimeUnit.SECONDS)

        // then
        val getResult = mockMvc.perform(
            get("/point/$userId")
        )
            .andExpect(status().isOk)
            .andReturn()

        val responseContent = getResult.response.contentAsString
        val userPoint = objectMapper.readValue(responseContent, UserPoint::class.java)
        val actual = userPoint.point
        val expected = expectedTotal
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @DisplayName("여러 유저들에게 동시에 여러 금액으로 충전했을때, 정상적으로 충전한다")
    fun `when charge multiple times with different amount for multiple users, then charge successfully`() {
        // given
        val userCount = 5
        val randomUserIds = (1..userCount).map { Random.nextLong(1, Long.MAX_VALUE) }
        randomUserIds.forEach { userPointTable.insertOrUpdate(it, 0) }
        val chargeAmount = 100L
        val chargesPerUser = 10

        // when
        val futures = randomUserIds.flatMap { userId ->
            (1..chargesPerUser).map {
                CompletableFuture.runAsync {
                    mockMvc.perform(
                        patch("/point/$userId/charge")
                            .content(chargeAmount.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                        .andExpect(status().isOk)
                }
            }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).get(10, TimeUnit.SECONDS)

        // then
        randomUserIds.forEach { userId ->
            val getResult = mockMvc.perform(
                get("/point/$userId")
            )
                .andExpect(status().isOk)
                .andReturn()

            val responseContent = getResult.response.contentAsString
            val userPoint = objectMapper.readValue(responseContent, UserPoint::class.java)
            val finalPoint = userPoint.point
            val expected = chargeAmount * chargesPerUser
            assertThat(finalPoint).isEqualTo(expected)
        }
    }
}
