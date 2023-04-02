package core

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._
import config._
import util.LSFR

case class Barrier()(implicit p: Parameters) extends Component {
  private val actualPosWidth = log2Up(p(LedMatrixColNum)) + p(ControlPrecision)
  private val speedWidth     = log2Up(p(BarrierMaxSpeed))

  val io = new Bundle {
    val pause    = in Bool ()
    val create   = in Bool ()
    val speed    = in UInt (speedWidth bits)
    val height   = in UInt (log2Up(p(LedMatrixRowNum)) bits)
    val drawInfo = master(DrawInfo())
  }

  val actualPos  = RegInit(U(0, actualPosWidth bits))
  val pos        = actualPos >> p(ControlPrecision)
  val displayReg = RegInit(False)
  val height     = RegInit(U(0, log2Up(p(LedMatrixRowNum)) bits))

  val barrierFsm = new StateMachine {
    val idle: State    = new State with EntryPoint {
      whenIsActive {
        when(io.create && !io.pause) {
          goto(display)
        }
      }
      onExit {
        actualPos  := U(actualPosWidth bits, default -> true)
        displayReg := True
        height     := io.height
      }
    }
    val display: State = new State {
      whenIsActive {
        when(!io.pause) {
          actualPos := actualPos - io.speed
          when(actualPos <= io.speed) {
            goto(idle)
          }
        } otherwise {
          actualPos := actualPos
        }
      }
      onExit {
        displayReg := False
      }
    }
  }

  io.drawInfo.startRow := p(GroundY) - height
  io.drawInfo.startCol := pos
  io.drawInfo.endRow   := p(GroundY) - 1
  io.drawInfo.endCol   := pos
  io.drawInfo.display  := displayReg
}

case class Barriers()(implicit p: Parameters) extends Component {
  val io = new Bundle {
    val pause     = in Bool ()
    val drawInfos = Vec(master(DrawInfo()), p(MaxBarrierNum))
  }

  private val speedWidth = log2Up(p(BarrierMaxSpeed))

  val barriers   = (0 until p(MaxBarrierNum)).map(_ => Barrier())
  val speed      = RegInit(U(p(BarrierInitSpeed), speedWidth bits))
  val isMaxSpeed = speed >= p(BarrierMaxSpeed)

  val speedCounter = RegInit(U(0, log2Up(p(CyclePerSpeedIncrease)) bits))
  when(!isMaxSpeed && !io.pause) {
    when(speedCounter === p(CyclePerSpeedIncrease) - 1) {
      speedCounter := 0
      speed        := speed + p(BarrierSpeedStep)
    } otherwise {
      speedCounter := speedCounter + 1
    }
  } otherwise {
    speed        := speed
    speedCounter := speedCounter
  }

  val createTrigger = RegInit(
    False
  ) // High for one cycle when a barrier should be created
  val createCounter = RegInit(U(0, log2Up(p(InitCreationCycle)) bits))
  val createGap     = RegInit(
    U(p(InitCreationCycle))
  ) // The gap between two barriers creation
  val createGapCounter = RegInit(
    U(0, log2Up(p(InitCreationCycle)) bits)
  ) // The counter for decreasing the gap
  val isMinCreateGap = createGap <= p(MinCreationCycle)

  when(!isMinCreateGap && !io.pause) {
    when(createGapCounter === p(CyclePerCreationCycleIncrease) - 1) {
      createGapCounter := 0
      createGap        := createGap - p(CreationCycleStep)
    } otherwise {
      createGapCounter := createGapCounter + 1
    }
  } otherwise {
    createGapCounter := createGapCounter
    createGap        := createGap
  }

  when(!io.pause) {
    when(createCounter === createGap - 1) {
      createCounter := 0
      createTrigger := True
    } otherwise {
      createCounter := createCounter + 1
      createTrigger := False
    }
  } otherwise {
    createCounter := createCounter
    createTrigger := False
  }

  val createSel     = RegInit(B(1, p(MaxBarrierNum) bits))
  val createSignals = createSel.asBools.map(_ && createTrigger)
  when(createTrigger.rise()) {
    createSel.rotateLeft(1)
  } otherwise {
    createSel := createSel
  }

  val lsfr      = LSFR(15, Seq(0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14))
  val lsfrValue = lsfr.io.value.asUInt

  barriers.foreach { barrier =>
    barrier.io.pause  := io.pause
    barrier.io.speed  := speed
    barrier.io.height := lsfrValue(1 downto 0).resize(
      barrier.io.height.getWidth
    ) + 1
  }

  (barriers zip createSignals).foreach { case (barrier, createSignal) =>
    barrier.io.create := createSignal
  }

  (io.drawInfos zip barriers).foreach { case (drawInfo, barrier) =>
    drawInfo := barrier.io.drawInfo
  }
}
