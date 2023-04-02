package util

import spinal.core._
import spinal.lib._

case class LSFR(
    val width: Int,
    val taps: Seq[Int],
    val init: BigInt = 1
) extends Component {
  val io = new Bundle {
    val value = out Bits (width bits)
  }

  val reg = Reg(Bits(width bits)) init (init)
  val xor = taps.map(reg(_)).reduce(_ ^ _)
  reg      := xor ## reg(width - 1 downto 1)
  io.value := reg
}
