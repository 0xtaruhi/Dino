package core

import spinal.core._
import spinal.lib._

import config._

case class Ground()(implicit val p: Parameters) extends Component {
  val io = new Bundle {
    val drawInfo = master(DrawInfo())
  }
  
  io.drawInfo.startRow := p(GroundY)
  io.drawInfo.startCol := 0
  io.drawInfo.endRow   := p(LedMatrixRowNum) - 1
  io.drawInfo.endCol   := p(LedMatrixColNum) - 1
  io.drawInfo.display  := True
}
