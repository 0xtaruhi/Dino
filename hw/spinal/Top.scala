package dino

import spinal.core._
import spinal.lib._
import driver.LEDMatrixDriver
import core._
import config._
import driver.{LEDMatrixSimpleDriver, SegmentConverter}
import driver.ScanSevenSegmentDriver

case class Top()(implicit p: Parameters) extends Component {
  val io = new Bundle {
    val jump           = in Bool ()
    val pause          = in Bool ()
    val gameover       = out Bool ()
    val currentRow     = out Bits (16 bits)
    val currentRowInfo = out Bits (16 bits)
    val segment        = out Bits (8 bits)
    val segmentSel     = out Bits (p(SevenSegmentNum) bits)
  }

  val LedDriver              = LEDMatrixSimpleDriver(
    p(LedMatrixRowNum),
    p(LedMatrixColNum),
    OHMode = false
  )
  val drawer                 = Drawer()
  val dino                   = Dino()
  val ground                 = Ground()
  val controller             = Controller()
  val scanSevenSegmentDriver = ScanSevenSegmentDriver(p(SevenSegmentNum))
  val barriers               = Barriers()

  val pause = io.pause || controller.io.gameover

  io.currentRow     := B"16'b1".rotateLeft(drawer.io.currentRow)
  io.currentRowInfo := drawer.io.currentRowInfo

  (scanSevenSegmentDriver.io.value zip controller.io.score.reverse).foreach {
    case (value, score) =>
      value := score
  }

  io.gameover              := controller.io.gameover
  controller.io.pause      := pause
  controller.io.dinoBottom := dino.io.drawInfo.endRow

  for (i <- 0 until p(MaxBarrierNum)) {
    controller.io.barriersPos(i) := barriers.io.drawInfos(i).startCol
    controller.io.barriersTop(i) := barriers.io.drawInfos(i).startRow
  }

  io.segment    := scanSevenSegmentDriver.io.segment
  io.segmentSel := scanSevenSegmentDriver.io.sel

  drawer.io.dinoDrawInfo <> dino.io.drawInfo
  drawer.io.groundDrawInfo <> ground.io.drawInfo
  drawer.io.barriersDrawInfos <> barriers.io.drawInfos

  dino.io.jump  := io.jump
  dino.io.pause := pause

  barriers.io.pause := pause
}
