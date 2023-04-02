package driver

import spinal.core._
import spinal.lib._

object SegmentConverter {
  val charToSegment = Map(
    '0' -> 0x3f,
    '1' -> 0x06,
    '2' -> 0x5b,
    '3' -> 0x4f,
    '4' -> 0x66,
    '5' -> 0x6d,
    '6' -> 0x7d,
    '7' -> 0x07,
    '8' -> 0x7f,
    '9' -> 0x6f,
    'A' -> 0x77,
    'B' -> 0x7c,
    'C' -> 0x39,
    'D' -> 0x5e,
    'E' -> 0x79,
    'F' -> 0x71
  )

  def apply(c: Char, lightWhenHigh: Boolean): Int =
    if (lightWhenHigh) charToSegment(c)
    else ~charToSegment(c)

  def apply(c: Char): Int = apply(c, true)

  def apply[T <: BaseType](c: T, lightWhenHigh: Boolean): Bits = {
    val result = Bits(8 bits)
    switch(c) {
      for ((k, v) <- charToSegment) {
        is(Integer.parseInt(k.toString, 16)) {
          if (lightWhenHigh) result := B(v, 8 bits)
          else result               := ~B(v, 8 bits)
        }
      }
    }
    result
  }

  def apply[T <: BaseType](c: T): Bits = apply(c, true)
}

case class SevenSegmentDriver(
    val lightWhenHigh: Boolean = true
) extends Component {
  val io = new Bundle {
    val value   = in Bits (8 bits)
    val segment = out Bits (8 bits)
  }

  io.segment := SegmentConverter(io.value, lightWhenHigh)
}

case class ScanSevenSegmentDriver(
    val sevenSegmentNum: Int,
    val lightWhenHigh: Boolean = true
) extends Component {
  val io = new Bundle {
    val value   = Vec(in UInt (4 bits), sevenSegmentNum)
    val segment = out Bits (8 bits)
    val sel     = out Bits (sevenSegmentNum bits)
  }

  val sel = RegInit(B(1, sevenSegmentNum bits))
  sel        := sel.rotateLeft(1)
  io.segment := SegmentConverter(
    io.value(
      OhMux(
        sel,
        (0 until sevenSegmentNum)
          .map(U(_, log2Up(sevenSegmentNum) bits))
      )
    ),
    lightWhenHigh
  )
  io.sel := sel
}
