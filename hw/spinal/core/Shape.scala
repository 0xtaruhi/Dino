package core

import spinal.core._
import spinal.lib._

// T <: BitVector or T is Int
abstract class Shape[T <: BitVector] {
  def isFilled(row: T, col: T): Bool
}

class Rectangle[T <: BitVector](
    val startRow: T,
    val startCol: T,
    val endRow: T,
    val endCol: T,
    val display: Bool = True
) extends Shape[T] {

  override def isFilled(row: T, col: T): Bool = {
    val result = Bool()
    result := display &&
      row.asBits.asUInt >= startRow.asBits.asUInt &&
      row.asBits.asUInt <= endRow.asBits.asUInt &&
      col.asBits.asUInt >= startCol.asBits.asUInt &&
      col.asBits.asUInt <= endCol.asBits.asUInt
    result
  }
}

object Rectangle {
  def apply[T <: BitVector](
      startRow: T,
      startCol: T,
      endRow: T,
      endCol: T,
      display: Bool = True
  ): Rectangle[T] = {
    val rectangle = new Rectangle(startRow, startCol, endRow, endCol, display)
    rectangle
  }

  def apply(drawInfo: DrawInfo): Rectangle[UInt] = {
    Rectangle(
      drawInfo.startRow,
      drawInfo.startCol,
      drawInfo.endRow,
      drawInfo.endCol,
      drawInfo.display
    )
  }
}
