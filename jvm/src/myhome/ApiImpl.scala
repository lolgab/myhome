package myhome

import akka.actor.Actor
import akka.stream.scaladsl.Source
import upickle.default._
import upickleModelImplicits._
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Flow
import akka.http.scaladsl.model.ws
import com.thoughtworks.binding.Binding.Var
import ujson.Js

import scala.concurrent.Future

object ApiImpl extends Api {
  var _myDevices = Seq[Device]()

  def myDevices                         = _myDevices.synchronized { _myDevices }
  def myDevices_=(v: Seq[Device]): Unit = _myDevices.synchronized { _myDevices = v }

//    Seq(
//      Device(IpAddress(192, 168, 0, 12),
//             Seq(Port(0, Out, Switch, Var(1)), Port(1, Out, Switch, Var(0)))),
//      Device(
//        IpAddress(192, 168, 0, 15),
//        Seq(Port(0, Out, Regulation, Var(233)),
//            Port(1, In, Switch, Var(0)),
//            Port(2, Out, Regulation, Var(10)))
//      )
//    )

  var _myMessages = Seq[Message](
//    RegulationToSwitchMessage(IpAddress(192, 168, 0, 11),
//                              1,
//                              IpAddress(192, 168, 0, 11),
//                              0,
//                              OnLessThan(50),
//                              Off),
//    RegulationToSwitchMessage(IpAddress(192, 168, 0, 11),
//                              1,
//                              IpAddress(192, 168, 0, 11),
//                              0,
//                              OnMoreThan(150),
//                              On)
  )

  def myMessages                         = _myMessages.synchronized { _myMessages }
  def myMessages_=(v: Seq[Message]): Unit = _myMessages.synchronized { _myMessages = v }

  def devices: Seq[Device] = {
    myDevices
  }

  def set(device: IpAddress, port: Id, value: Int): Unit = {
    ModelOps.set(myDevices, device, port, value)
    println(s"sending ${(device, port, value)} to serverSocketActor!")
    Server.serverSocketActor ! ServerSocketActor.SetValue(device, port, value)
  }

  def addDevice(device: Device): Unit = {
    println(s"adding device $device")
    myDevices = myDevices :+ device
    println(s"devices = $myDevices")
  }

  def receivedState(state: Device): Unit = {
    val d     = state.id
    val ports = state.ports
    myDevices.find(_.id == d) match {
      case Some(dev) =>
        for (p <- ports) {
          dev.ports.find(_.id == p.id).foreach(_.value.value = p.value.value)
        }
      case None => addDevice(state)
    }
  }

  def messages = myMessages

  def addMessage(message: Message): Unit = {
    myMessages +:= message
  }

  def removeMessage(message: Message): Unit = {
    myMessages = myMessages.filterNot(_ == message)
  }
}
