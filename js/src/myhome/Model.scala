package myhome

import autowire._
import upickleModelImplicits._
import com.thoughtworks.binding.{Binding, dom}
import com.thoughtworks.binding.Binding.Var
import myhome.JsUtils._

import scala.annotation.tailrec
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

object Model {
  val _devices: Var[Seq[Device]] = Var(Seq())

  @dom
  def devices: Binding[Seq[Device]] = _devices.bind

  private def fetchDevices(): Unit = {
    Ajaxer[Api].devices().call().onComplete {
      case Success(seq) =>
        _devices.value = seq
      case Failure(_) =>
        fetchDevices()
    }
  }
  fetchDevices()

  private def updateDevices(): Unit = {
    Ajaxer[Api].devices().call().onComplete {
      case Success(seq) =>
        for (rDev <- seq; dev <- _devices.value; if rDev.id == dev.id; rPort <- rDev.ports;
             port <- dev.ports
             if rPort.id == port.id /*&& port.inOut == In*/ && rPort.value.value != port.value.value) {
          port.value.value = rPort.value.value
        }
      case Failure(_) =>
        fetchDevices()
    }
  }
  scalajs.js.timers.setInterval(1000)(updateDevices())

  private val _messages: Var[Seq[Message]] = Var(Seq())

  @dom
  def messages: Binding[Seq[Message]] = _messages.bind

  private def fetchMessages(): Unit = {
    Ajaxer[Api].messages().call().onComplete {
      case Success(seq) =>
        _messages.value = seq
      case Failure(_) =>
        fetchDevices()
    }
  }
  fetchMessages()

  private def updateMessages(): Unit = {
    Ajaxer[Api].messages().call().onComplete {
      case Success(seq) =>
        if (seq != _messages.value) _messages.value = seq
      case Failure(_) =>
    }
  }
  scalajs.js.timers.setInterval(1000)(updateMessages())
/*
  private val setValues = Binding[Unit] {
    def s(dId: IpAddress, pId: Id, v: Var[Int]) = Binding {
      Ajaxer[Api].set(dId, pId, v.value).call()
    }

    val seq = for {
      d    <- devices.bind
      port <- d.ports
    } yield (d.id, port)

    val res = c(seq) map {
      case (dId, Port(pId, Out, Switch, value)) =>
        s(dId, pId, value).bind
      case _ => Binding[Unit]()
    }

    res.watch()
  }
  setValues.watch()
*/
}
