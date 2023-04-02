package config

case object LedMatrixRowNum               extends Field[Int]
case object LedMatrixColNum               extends Field[Int]
case object SevenSegmentNum               extends Field[Int]
case object CyclePerScoreIncrease         extends Field[Int]
case object DinoHeight                    extends Field[Int]
case object DinoX                         extends Field[Int]
case object DinoInitSpeed                 extends Field[Int]
case object Gravity                       extends Field[Int]
case object GroundY                       extends Field[Int]
case object MaxBarrierNum                 extends Field[Int]
case object ControlPrecision              extends Field[Int]
case object BarrierInitSpeed              extends Field[Int]
case object BarrierMaxSpeed               extends Field[Int]
case object BarrierSpeedStep              extends Field[Int]
case object CyclePerSpeedIncrease         extends Field[Int]
case object InitCreationCycle             extends Field[Int]
case object MinCreationCycle              extends Field[Int]
case object CreationCycleStep             extends Field[Int]
case object CyclePerCreationCycleIncrease extends Field[Int]

class DefaultConfig
    extends Config((site, here, up) => {
      case LedMatrixRowNum               => 16
      case LedMatrixColNum               => 16
      case SevenSegmentNum               => 4
      case CyclePerScoreIncrease         => 200
      case DinoHeight                    => 4
      case DinoX                         => 3
      case DinoInitSpeed                 => 313
      case Gravity                       => 1
      case GroundY                       => 14
      case MaxBarrierNum                 => 1
      case ControlPrecision              => 13
      case BarrierInitSpeed              => 50
      case BarrierMaxSpeed               => 150
      case BarrierSpeedStep              => 1
      case CyclePerSpeedIncrease         => 300
      case InitCreationCycle             => 1000
      case MinCreationCycle              => 400
      case CreationCycleStep             => 1
      case CyclePerCreationCycleIncrease => 100
    })
