package core

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._

import config._

case class Dino()(implicit val p: Parameters) extends Component {
  val io = new Bundle {
    val jump     = in Bool ()
    val pause    = in Bool ()
    val drawInfo = master(DrawInfo())
  }

  val pos       = UInt(log2Up(p(LedMatrixRowNum)) bits)
  val actualPos = RegInit(
    U(p(GroundY) - 1, pos.getWidth bits) << p(ControlPrecision)
  )
  pos := actualPos >> p(ControlPrecision)

  val jumpFsm = new StateMachine {
    val speed = RegInit(S(0, actualPos.getWidth bits))

    val idle: State = new State with EntryPoint {
      onEntry {
        speed := 0
      }
      whenIsActive {
        when(io.jump && !io.pause) {
          goto(jumping)
        }
      }
      onExit(speed := p(DinoInitSpeed))
    }

    val jumping: State = new State {
      whenIsActive {
        when(!io.pause) {
          actualPos := (actualPos.asSInt - speed).asUInt
          speed     := speed - p(Gravity)
          when(speed === -p(DinoInitSpeed)) {
            goto(idle)
          }
        } otherwise {
          actualPos := actualPos
          speed     := speed
        }
      }
    }
  }

  io.drawInfo.startRow := pos - p(DinoHeight) + 1
  io.drawInfo.startCol := p(DinoX)
  io.drawInfo.endRow   := pos
  io.drawInfo.endCol   := p(DinoX)
  io.drawInfo.display  := True
}
