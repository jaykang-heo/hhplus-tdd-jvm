package io.hhplus.tdd.database

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Component
class LockManager {
    private val locks = ConcurrentHashMap<Long, ReentrantLock>()

    fun <T> executeWithLock(userId: Long, block: () -> T): T {
        val lock = locks.computeIfAbsent(userId) { ReentrantLock() }

        return lock.withLock {
            try {
                block()
            } finally {
                if (!lock.hasQueuedThreads()) {
                    locks.remove(userId, lock)
                }
            }
        }
    }
}
