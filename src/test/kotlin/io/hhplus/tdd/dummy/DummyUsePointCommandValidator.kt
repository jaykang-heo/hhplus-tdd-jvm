package io.hhplus.tdd.dummy

import io.hhplus.tdd.point.validator.UsePointCommandValidator

class DummyUsePointCommandValidator : UsePointCommandValidator(DummyUserPointRepository())
