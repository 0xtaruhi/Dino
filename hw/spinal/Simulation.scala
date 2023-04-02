package dino

import spinal.core._
import spinal.core.sim._

import config._

object Simulation {
  def main(args: Array[String]): Unit = {
    val spinalConfig = SpinalConfig(
      defaultConfigForClockDomains =
        ClockDomainConfig(resetKind = SYNC, resetActiveLevel = LOW)
    )
    val simConfig    = SimConfig.withConfig(spinalConfig).withWave
    implicit val p   = (new DefaultConfig).toInstance
    simConfig
      .compile {
        val dut = Top()
        dut
      }
      .doSim { dut =>
        dut.clockDomain.deassertReset()
        dut.clockDomain.forkStimulus(10)
        for (i <- 0 until 100) {
          dut.clockDomain.waitSampling()
        }
        dut.io.jump #= true
        for (i <- 0 until 5000) {
          dut.clockDomain.waitSampling()
        }
      }
  }
}
