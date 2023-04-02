package core

import spinal.core._
import spinal.lib._
import config._

case class CrashDetector()(implicit p: Parameters) extends Component {
  private val barriersNum     = p(MaxBarrierNum)
  private val barrierPosWidth = log2Up(p(LedMatrixColNum))

  val io = new Bundle {
    val dinoBottom  = in UInt (log2Up(p(LedMatrixRowNum)) bits)
    val barriersPos = in Vec (UInt(barrierPosWidth bits), barriersNum)
    val barriersTop = in Vec (UInt(barrierPosWidth bits), barriersNum)
    val crashed     = out Bool ()
  }

  io.crashed := (io.barriersPos zip io.barriersTop)
    .map { case (pos, top) =>
      pos === p(DinoX) && io.dinoBottom >= top
    }
    .reduce(_ || _)
}

case class Controller()(implicit p: Parameters) extends Component {
  private val barriersNum     = p(MaxBarrierNum)
  private val barrierPosWidth = log2Up(p(LedMatrixColNum))

  val io = new Bundle {
    val pause       = in Bool ()
    val dinoBottom  = in UInt (log2Up(p(LedMatrixRowNum)) bits)
    val barriersPos = in Vec (UInt(barrierPosWidth bits), barriersNum)
    val barriersTop = in Vec (UInt(barrierPosWidth bits), barriersNum)
    val score       = Vec(out UInt (4 bits), p(SevenSegmentNum))
    val gameover    = out Bool ()
  }

  val scoreBoard    = ScoreBoard()
  val crashDetector = CrashDetector()

  scoreBoard.io.increase       := !io.pause
  crashDetector.io.dinoBottom  := io.dinoBottom
  crashDetector.io.barriersPos := io.barriersPos
  crashDetector.io.barriersTop := io.barriersTop

  val gameover = RegInit(False).setWhen(crashDetector.io.crashed.rise)
  io.gameover := gameover

  io.score := scoreBoard.io.scoreDec
}
