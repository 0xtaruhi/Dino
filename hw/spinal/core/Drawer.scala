package core

import spinal.core._
import spinal.lib._
import config.Parameters
import _root_.config.LedMatrixRowNum
import _root_.config.LedMatrixColNum
import config.MaxBarrierNum

case class DrawInfo()(implicit p: Parameters) extends Bundle with IMasterSlave {
  private[core] val rowWidth = log2Up(p(LedMatrixRowNum))
  private[core] val colWidth = log2Up(p(LedMatrixColNum))

  val startRow = UInt(rowWidth bits)
  val startCol = UInt(colWidth bits)
  val endRow   = UInt(rowWidth bits)
  val endCol   = UInt(colWidth bits)
  val display  = Bool()

  override def asMaster(): Unit = {
    out(startRow, startCol, endRow, endCol, display)
  }
}

case class Drawer()(implicit p: Parameters) extends Component {
  private[core] val rowWidth = log2Up(p(LedMatrixRowNum))

  val io = new Bundle {
    val currentRow        = out UInt (rowWidth bits)
    val currentRowInfo    = out Bits (p(LedMatrixColNum) bits)
    val dinoDrawInfo      = slave(DrawInfo())
    val groundDrawInfo    = slave(DrawInfo())
    val barriersDrawInfos = Vec(slave(DrawInfo()), p(MaxBarrierNum))
  }

  val shapes = Seq(
    Rectangle(io.dinoDrawInfo),
    Rectangle(io.groundDrawInfo)
  ) ++ io.barriersDrawInfos.map(Rectangle(_))

  val currentRow = RegInit(U(0, rowWidth bits))

  when(currentRow === p(LedMatrixRowNum) - 1) {
    currentRow := 0
  } otherwise { currentRow := currentRow + 1 }

  io.currentRow     := currentRow
  io.currentRowInfo := io.currentRowInfo.range.reverse
    .map(col => {
      shapes
        .map(_.isFilled(currentRow, col))
        .reduce(_ || _)
    })
    .map(_.asBits)
    .reduce(_ ## _)
}
