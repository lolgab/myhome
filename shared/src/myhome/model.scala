package myhome

import java.net.InetAddress

import com.thoughtworks.binding.Binding.Var

trait Function

sealed trait RegulationFunc extends Function
final case class Inc(value: Int) extends RegulationFunc
final case class Set(value: Int) extends RegulationFunc
final case class Dec(value: Int) extends RegulationFunc

sealed trait SwitchFunc extends Function
case object On  extends SwitchFunc
case object Off extends SwitchFunc

trait Trigger

sealed trait SwitchTrigger extends Trigger
case object OnOn extends SwitchTrigger
case object OnOff extends SwitchTrigger

sealed trait RegulationTrigger extends Trigger
final case class OnLessThan(value: Int)        extends RegulationTrigger
final case class OnMoreThan(value: Int)        extends RegulationTrigger

sealed trait InOut
case object In  extends InOut
case object Out extends InOut

sealed trait Type
case object Switch     extends Type
case object Regulation extends Type

case class Port(id: Id, inOut: InOut, portType: Type, value: Var[Int])

case class IpAddress(value: Int) extends AnyVal {
  override def toString: String =
    s"${(value >> 24) & 255}.${(value >> 16) & 255}.${(value >> 8) & 255}.${value & 255}"
}
case object IpAddress {
  def apply(add: InetAddress): IpAddress = {
    val b = add.getAddress
    apply(b(0) & 0xFF, b(1) & 0xFF, b(2) & 0xFF, b(3) & 0xFF)
  }

  def apply(first: Int, second: Int, third: Int, fourth: Int): IpAddress =
    apply(first << 24 | second << 16 | third << 8 | fourth)

  def apply(ipStr: String): IpAddress = {
    val nums = ipStr.split("\\.").map(_.toInt)
    apply(nums(0), nums(1), nums(2), nums(3))
  }
}

case class Device(id: IpAddress, ports: Seq[Port])

sealed trait Message {
  val fromDevice: IpAddress
  val fromPort: Id
  val toDevice: IpAddress
  val toPort: Id
  val trigger: Trigger
  val func: Function
}

case class RegulationToRegulationMessage(fromDevice: IpAddress,
                                         fromPort: Id,
                                         toDevice: IpAddress,
                                         toPort: Id,
                                         trigger: RegulationTrigger,
                                         func: RegulationFunc)
    extends Message

case class RegulationToSwitchMessage(fromDevice: IpAddress,
                                     fromPort: Id,
                                     toDevice: IpAddress,
                                     toPort: Id,
                                     trigger: RegulationTrigger,
                                     func: SwitchFunc)
    extends Message

case class SwitchToRegulationMessage(fromDevice: IpAddress,
                                     fromPort: Id,
                                     toDevice: IpAddress,
                                     toPort: Id,
                                     trigger: SwitchTrigger,
                                     func: RegulationFunc)
    extends Message

case class SwitchToSwitchMessage(fromDevice: IpAddress,
                                 fromPort: Id,
                                 toDevice: IpAddress,
                                 toPort: Id,
                                 trigger: SwitchTrigger,
                                 func: SwitchFunc)
    extends Message

object ModelOps {
  def set(devices: Iterable[Device], idD: IpAddress, idP: Id, value: Int): Unit = {
    devices
      .find(_.id == idD)
      .foreach(_.ports.find(_.id == idP).map(_.value) match {
        case Some(v) => v.value = value
        case _       =>
      })
  }
}

//case class Room(name: String, devices: Seq[Device])
