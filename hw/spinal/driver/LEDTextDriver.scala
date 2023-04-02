package driver

import spinal.core._
import spinal.lib._

case class LEDTextDriver(
    val displayString: String = "Hello World!"
) extends Component {
  val io = new Bundle {
    // Register Select: 0 for write command register, 1 for write data register
    val rs   = out Bool ()
    // Read/Write: 0 for write, 1 for read
    val rw   = out Bool ()
    val en   = out Bool ()
    val data = out Bits (8 bits)
    val rst  = out Bool ()
  }

  io.rs := True
  io.rw := False

  io.rst := ClockDomain.current.isResetActive

  def charToAscii(c: Char): Int = c.toInt

  val textBuffer    = Vec(
    displayString.map(charToAscii(_) - 0x20).map(B(_, 8 bits))
  )
  val textBufferPtr = Reg(UInt(log2Up(textBuffer.length) bits)) init (0)

  val enable = RegInit(False)
  enable := ~enable

  when(enable.rise()) {
    textBufferPtr := Mux(
      textBufferPtr === textBuffer.length - 1,
      U(0, textBufferPtr.getWidth bits),
      textBufferPtr + 1
    )
  }

  io.data := RegNext(Mux(enable, textBuffer(textBufferPtr), B(0, 8 bits)))
  io.en   := enable
}
