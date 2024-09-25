package io.hhplus.tdd.point

import io.hhplus.tdd.point.command.ChargePointCommand
import io.hhplus.tdd.point.command.UsePointCommand
import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.ports.IPointService
import io.hhplus.tdd.point.query.FindPointHistoryQuery
import io.hhplus.tdd.point.query.FindUserPointQuery
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/point")
class PointController(
    private val pointService: IPointService
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    fun point(
        @PathVariable id: Long
    ): UserPoint {
        val query = FindUserPointQuery(id)
        return pointService.findUserPoint(query)
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    fun history(
        @PathVariable id: Long
    ): List<PointHistory> {
        val query = FindPointHistoryQuery(id)
        return pointService.findPointHistoryList(query)
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    fun charge(
        @PathVariable id: Long,
        @RequestBody amount: Long
    ): UserPoint {
        val command = ChargePointCommand(id, amount)
        return pointService.charge(command)
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    fun use(
        @PathVariable id: Long,
        @RequestBody amount: Long
    ): UserPoint {
        val command = UsePointCommand(id, amount)
        return pointService.use(command)
    }
}
