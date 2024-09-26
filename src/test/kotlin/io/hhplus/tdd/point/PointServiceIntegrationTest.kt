package io.hhplus.tdd.point

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.command.UsePointCommand
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

    @Test
    @DisplayName("포인트를 동시에 충전하고 사용할 때, 최종 잔액이 정확하다")
    fun `when concurrently charging and using points, final balance is correct`() {
        // given
        val userId = Random.nextLong(1, Long.MAX_VALUE)
        val initialCharge = 1000L
        sut.charge(ChargePointCommand(userId, initialCharge))
        val chargeAmount = 100L
        val useAmount = 50L
        val numberOfThreads = 20

        // when
        val futures = (1..numberOfThreads).map { index ->
            CompletableFuture.runAsync {
                if (index % 2 == 0) {
                    sut.charge(ChargePointCommand(userId, chargeAmount))
                } else {
                    sut.use(UsePointCommand(userId, useAmount))
                }
            }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).join()

        // then
        val actual = sut.findUserPoint(FindUserPointQuery(userId)).point
        val expectedFinalPoint = initialCharge + (chargeAmount * 10) - (useAmount * 10)
        assertThat(actual).isEqualTo(expectedFinalPoint)
    }

    @Test
    @DisplayName("여러 스레드에서 동시에 포인트를 사용할 때, 잔액이 음수가 되지 않는다")
    fun `when concurrently using points from multiple threads, balance never becomes negative`() {
        // given
        val userId = Random.nextLong(1, Long.MAX_VALUE)
        val initialBalance = 1000L
        sut.charge(ChargePointCommand(userId, initialBalance))

        val useAmount = 10L
        val numberOfThreads = 150

        // when
        val futures = (1..numberOfThreads).map {
            CompletableFuture.runAsync {
                try {
                    sut.use(UsePointCommand(userId, useAmount))
                } catch (_: RuntimeException) {
                }
            }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).join()

        // then
        val finalBalance = sut.findUserPoint(FindUserPointQuery(userId)).point
        assertThat(finalBalance).isGreaterThanOrEqualTo(0)
        assertThat(finalBalance).isLessThan(useAmount)
    }

    @Test
    @DisplayName("포인트 사용 중 예외가 발생해도 다른 트랜잭션에 영향을 주지 않는다")
    fun `when exception occurs during point use, it does not affect other transactions`() {
        // given
        val userId = Random.nextLong(1, Long.MAX_VALUE)
        val initialBalance = 100L
        sut.charge(ChargePointCommand(userId, initialBalance))

        val validUseAmount = 10L
        val invalidUseAmount = 1000L
        val numberOfThreads = 20

        // when
        val futures = (1..numberOfThreads).map { index ->
            CompletableFuture.runAsync {
                try {
                    if (index % 2 == 0) {
                        sut.use(UsePointCommand(userId, validUseAmount))
                    } else {
                        sut.use(UsePointCommand(userId, invalidUseAmount))
                    }
                } catch (_: RuntimeException) {
                }
            }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).join()

        // then
        val finalBalance = sut.findUserPoint(FindUserPointQuery(userId)).point
        val expectedFinalBalance = initialBalance - (validUseAmount * (numberOfThreads / 2))
        assertThat(finalBalance).isEqualTo(expectedFinalBalance)
    }
}
