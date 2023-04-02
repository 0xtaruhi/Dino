package driver

import spinal.core._
import spinal.lib._

case class LEDMatrixDriver(
    val rowNum: Int,
    val colNum: Int
) extends Component {
  val io = new Bundle {
    val rows           = Vec(in Bits (colNum bits), rowNum)
    val currentRow     = out Bits (rowNum bits) // 1-hot
    val currentRowInfo = out Bits (colNum bits)
  }

  val OHCounter = RegInit(B(rowNum bits, 0 -> true, default -> false))
  OHCounter := OHCounter.rotateLeft(1)

  val currentRow = OHCounter.asBits
  io.currentRowInfo := MuxOH(currentRow.asBools, io.rows)
  io.currentRow     := currentRow
}

case class LEDMatrixSimpleDriver(
    val rowNum: Int,
    val colNum: Int,
    val OHMode: Boolean = true
) extends Component {
  val io = new Bundle {
    val rowIndex       =
      if (OHMode) in UInt (log2Up(rowNum) bits) else in UInt (rowNum bits)
    val row            = in Bits (colNum bits)
    val currentRow     = out Bits (rowNum bits)
    val currentRowInfo = out Bits (colNum bits)
  }

  if (OHMode) {
    io.currentRow := UIntToOh(io.rowIndex)
  } else {
    io.currentRow := io.rowIndex.asBits
  }
  io.currentRowInfo := io.row
}
