import dino._
import driver._
import config._
import spinal.core._

object VerilogEmit {
  def main(args: Array[String]): Unit = {
    val config = SpinalConfig(
      mode = Verilog,
      targetDirectory = "hw/spinal/generated",
      defaultConfigForClockDomains =
        ClockDomainConfig(resetKind = SYNC, resetActiveLevel = LOW)
    )

    implicit val p = (new DefaultConfig).toInstance

    config.generate(
      LEDTextDriver(
        " Fudan University ** Micro Electronics ** Zhang Zhengyi ** 2023 "
      )
    )
    config.generate(Top()).printPruned()
  }
}
