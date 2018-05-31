package myhome.pages.devices

import com.thoughtworks.binding.Binding.{Constants, Var}
import myhome._
import org.scalajs.dom.raw.Event

object Controller {
  private def func(f: Var[Int] => Unit, devs: Seq[Device], dev: IpAddress, port: Id)(e: Event): Unit = {
    for {
      d <- devs.find(_.id == dev)
      p <- d.ports.find(_.id == port)
    } f(p.value)
  }

  def toggle(devs: Seq[Device], dev: IpAddress, port: Id): Event => Unit = {
    func(v => v.value = 1 - v.value, devs, dev, port)
  }

  def set(devs: Seq[Device], dev: IpAddress, port: Id, value: Int): Event => Unit = {
    func(_.value = value, devs, dev, port)
  }

  def set(devs: Constants[Device], dev: IpAddress, port: Id, value: Int): Unit = {
    for {
      d <- devs.value.find(_.id == dev)
      p <- d.ports.find(_.id == port)
    } p.value.value = value
  }
}
