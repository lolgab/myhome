//package myhome
//
//object model {
//  sealed trait InOut
//  sealed trait In extends InOut
//  sealed trait Out extends InOut
//
//  sealed trait Func[T]
//  type FuncRegulation = Func[Byte]
//  type FuncSwitch = Func[Boolean]
//
//  final case class Inc(value: Byte) extends FuncRegulation
//  final case class Set(value: Byte) extends FuncRegulation
//  final case class Dec(value: Byte) extends FuncRegulation
//  final case object Max             extends FuncRegulation
//  final case object Min             extends FuncRegulation
//
//  final case object On     extends FuncSwitch
//  final case object Off    extends FuncSwitch
//  final case object Toggle extends FuncSwitch
//
//  sealed trait Trigger[T]
//  type RegulationTrigger = Trigger[Byte]
//  type SwitchTrigger = Trigger[Boolean]
//
//  final case object OnOn extends SwitchTrigger
//  final case object OnToggle extends SwitchTrigger
//  final case object OnOff extends SwitchTrigger
//
//  final case class OnChange(value: Byte) extends RegulationTrigger
//  final case class OnLessThan(value: Byte) extends RegulationTrigger
//  final case class OnMoreThan(value: Byte) extends RegulationTrigger
//  final case object OnMax extends RegulationTrigger
//  final case object OnMin extends RegulationTrigger
//
//  final case class Port[Dir <: InOut, T](id: Int)
//
//  //  final case class Device
//
//  final case class Message[T, U](from: Port[In, T], to: Port[Out, U], trigger: Trigger[T], func: Func[U])
//
//  val inReg = Port[In, Byte](1)
//  val outReg = Port[Out, Byte](2)
//  val inSwitch = Port[In, Boolean](3)
//  val outSwitch = Port[Out, Boolean](4)
//}