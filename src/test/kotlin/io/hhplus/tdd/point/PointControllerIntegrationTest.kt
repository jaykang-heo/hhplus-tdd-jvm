package io.hhplus.tdd.point

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.model.UserPoint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointControllerIntegrationTest(
    @Autowired private val webTestClient: WebTestClient,
    @Autowired private val userPointTable: UserPointTable
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
                webTestClient.patch()
                    .uri("/point/$userId/charge")
                    .bodyValue(chargeAmount)
                    .exchange()
                    .expectStatus().isOk
            }
        }

        CompletableFuture.allOf(*futures.toTypedArray()).get(10, TimeUnit.SECONDS)

        // then
        val actual = webTestClient.get()
            .uri("/point/$userId")
            .exchange()
            .expectStatus().isOk
            .expectBody(UserPoint::class.java)
            .returnResult()
            .responseBody?.point ?: 0
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
                webTestClient.patch()
                    .uri("/point/$userId/charge")
                    .bodyValue(amount)
                    .exchange()
                    .expectStatus().isOk
            }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).get(10, TimeUnit.SECONDS)

        // then
        val expected = webTestClient.get()
            .uri("/point/$userId")
            .exchange()
            .expectStatus().isOk
            .expectBody(UserPoint::class.java)
            .returnResult()
            .responseBody?.point!!
        assertThat(expected).isEqualTo(expectedTotal)
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
                    webTestClient.patch()
                        .uri("/point/$userId/charge")
                        .bodyValue(chargeAmount)
                        .exchange()
                        .expectStatus().isOk
                }
            }
        }

        CompletableFuture.allOf(*futures.toTypedArray()).get(10, TimeUnit.SECONDS)

        // then
        randomUserIds.forEach { userId ->
            val finalPoint = webTestClient.get()
                .uri("/point/$userId")
                .exchange()
                .expectStatus().isOk
                .expectBody(UserPoint::class.java)
                .returnResult()
                .responseBody?.point!!
            val expected = chargeAmount * chargesPerUser
            assertThat(finalPoint).isEqualTo(expected)
        }
    }
}
