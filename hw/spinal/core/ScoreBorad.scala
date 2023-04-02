package core

import spinal.core._
import spinal.lib._
import config._

case class ScoreBoard()(implicit p: Parameters) extends Component {
  private val scoreVecSize   = p(SevenSegmentNum)
  private val increaseCycles = p(CyclePerScoreIncrease)

  val io = new Bundle {
    val increase = in Bool ()
    val scoreDec = Vec(out(UInt(4 bits)), scoreVecSize)
  }

  val scoreDecReg = Vec(Reg(UInt(4 bits)) init (0), scoreVecSize)
  val isMax       = scoreDecReg.map(_ === 9).reduce(_ && _)
  val counter     = RegInit(U(0, log2Up(increaseCycles) bits))

  def increaseScore(): Unit = {
    scoreDecReg(0) := scoreDecReg(0) + 1
    for (i <- 1 until scoreVecSize) {
      when(scoreDecReg.take(i).map(_ === 9).andR) {
        scoreDecReg(i) := scoreDecReg(i) + 1
        scoreDecReg.take(i).foreach(_ := 0)
      }
    }
  }

  when(io.increase && !isMax) {
    counter := counter + 1
    when(counter === increaseCycles - 1) {
      counter := 0
      increaseScore()
    }
  }

  io.scoreDec := scoreDecReg
}
