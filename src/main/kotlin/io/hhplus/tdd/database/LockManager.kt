package io.hhplus.tdd.database

import org.springframework.stereotype.Component
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Component
class LockManager {
    private val lock = ReentrantLock()

    fun <T> executeWithLock(block: () -> T): T {
        return lock.withLock {
            block()
        }
    }
}
