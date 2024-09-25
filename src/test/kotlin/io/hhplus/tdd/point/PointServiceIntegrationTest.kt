package io.hhplus.tdd.point

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.query.FindUserPointQuery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@SpringBootTest
class PointServiceIntegrationTest(
    @Autowired val sut: PointService
) {
    @Test
    @DisplayName("포인트를 동시에 여러번 충전하면, 모두 정상적으로 충전된다")
    fun `when charge many points at the same time, charge successfully`() {
        // given
        val userId = Random.nextLong(1, Long.MAX_VALUE)
        val query = FindUserPointQuery(userId)
        val chargeAmount = 100L
        val numberOfThreads = 10
        val command = ChargePointCommand(userId, chargeAmount)

        // when
        val futures = (1..numberOfThreads).map {
            CompletableFuture.runAsync {
                sut.charge(command)
            }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).join()

        // then
        val actual = sut.findUserPoint(query).point
        val expectedFinalPoint = chargeAmount * numberOfThreads
        assertThat(actual).isEqualTo(expectedFinalPoint)
    }

    @Test
    @DisplayName("서로 다른 유저들의 충전 명령은 서로 간섭하지 않는다")
    fun `when different users charge points, then do not interfere each other`() {
        // given
        val userCount = 5
        val randomUserIds = (1..userCount).map { Random.nextLong(1, Long.MAX_VALUE) }
        val chargeAmount = 100L
        val chargesPerUser = 10

        // when
        val futures = randomUserIds.flatMap { userId ->
            (1..chargesPerUser).map {
                CompletableFuture.runAsync {
                    sut.charge(ChargePointCommand(userId, chargeAmount))
                }
            }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).get(10, TimeUnit.SECONDS)

        // then
        randomUserIds.forEach { userId ->
            val actual = sut.findUserPoint(FindUserPointQuery(userId)).point
            assertThat(actual).isEqualTo(chargeAmount * chargesPerUser)
        }
    }

    @Test
    @DisplayName("여러개의 포인트 금액으로 동시에 충전했을때, 정상적으로 충전한다")
    fun `when concurrent charges with varying amounts for a user, then charge successfully`() {
        // given
        val userId = Random.nextLong(1, Long.MAX_VALUE)
        val initialPoint = sut.findUserPoint(FindUserPointQuery(userId)).point
        val chargeAmounts = listOf(50L, 100L, 150L, 200L, 250L)

        val expectedTotal = initialPoint + chargeAmounts.sum()

        // when
        val futures = chargeAmounts.map { amount ->
            CompletableFuture.runAsync {
                sut.charge(ChargePointCommand(userId, amount))
            }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).get(10, TimeUnit.SECONDS)

        // then
        val actual = sut.findUserPoint(FindUserPointQuery(userId)).point
        assertThat(actual).isEqualTo(expectedTotal)
    }
}
