package myhome

import com.thoughtworks.binding.Binding.Var
import upickle.default._

object upickleModelImplicits {
  implicit def rwInc = macroRW[Inc]
  implicit def rwSet = macroRW[Set]
  implicit def rwDec = macroRW[Dec]

  implicit def rwRegulationFunc =
    ReadWriter.merge[RegulationFunc](rwInc, rwSet, rwDec)

  implicit def rwOn  = macroRW[On.type]
  implicit def rwOff = macroRW[Off.type]
//  implicit def rwToggle = macroRW[Toggle.type]

  implicit def rwSwitchFunc = ReadWriter.merge[SwitchFunc](rwOn, rwOff /*, rwToggle*/ )

  implicit def rwOnOn  = macroRW[OnOn.type]
  implicit def rwOnOff = macroRW[OnOff.type]
//  implicit def rwOnToggle = macroRW[OnToggle.type]

  implicit def rwSwitchTrigger = ReadWriter.merge[SwitchTrigger](rwOnOn, rwOnOff)

//  implicit def rwOnChange   = macroRW[OnChange.type]
  implicit def rwOnLessThan = macroRW[OnLessThan]
  implicit def rwOnMoreThan = macroRW[OnMoreThan]

  implicit def rwRegulationTrigger =
    ReadWriter.merge[RegulationTrigger]( rwOnLessThan,
                                        rwOnMoreThan)

  implicit def rwIn    = macroRW[In.type]
  implicit def rwOut   = macroRW[Out.type]
  implicit def rwInOut = ReadWriter.merge[InOut](rwIn, rwOut)

  implicit def varReadWrite[T: ReadWriter]: ReadWriter[Var[T]] =
    readwriter[T].bimap[Var[T]](_.value, Var(_))

  implicit def rwSwitch     = macroRW[Switch.type]
  implicit def rwRegulation = macroRW[Regulation.type]

  implicit def rwType = ReadWriter.merge[Type](rwSwitch, rwRegulation)

  implicit def rwPort = macroRW[Port]

  implicit def rwIpAddress = macroRW[IpAddress]

  implicit def rwDevice = macroRW[Device]

  implicit def rwRegulationToRegulationMessage = macroRW[RegulationToRegulationMessage]
  implicit def rwRegulationToSwitchMessage     = macroRW[RegulationToSwitchMessage]
  implicit def rwSwitchToRegulationMessage     = macroRW[SwitchToRegulationMessage]
  implicit def rwSwitchToSwitchMessage         = macroRW[SwitchToSwitchMessage]

  implicit def rwMessage =
    ReadWriter.merge[Message](rwRegulationToRegulationMessage,
                              rwRegulationToSwitchMessage,
                              rwSwitchToRegulationMessage,
                              rwSwitchToSwitchMessage)
}
