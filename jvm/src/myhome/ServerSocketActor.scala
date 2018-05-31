package myhome

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.{IO, Tcp}

object ServerSocketActor {
  case class SetValue(device: IpAddress, port: Int, value: Int)
}


class ServerSocketActor extends Actor with ActorLogging {
  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("0.0.0.0", 9000))

  private var handlers = Map[IpAddress, ActorRef]()

  def receive: Receive = {
    case b @ Bound(localAddress) =>
      context.parent ! b

    case CommandFailed(_: Bind) =>
      log.info("Command failed.")
      context.stop(self)

    case c @ Connected(remote, local) =>
      log.info(
        "Connection received from hostname: " + remote.getHostName + " address: " + remote.getAddress.toString)
      val ip = IpAddress(remote.getAddress)
      val handler =
        context.actorOf(Props(new DeviceConnectionHandler(ip, sender())))
      val connection = sender()
      handlers += ip -> handler
      connection ! Register(handler)


    case m: myhome.Message =>
      log.info(s"Received message $m")
      handlers(m.toDevice) ! m

    case s @ ServerSocketActor.SetValue(device, port, value) =>
      log.info(s"received message from ApiImpl: $s")
      handlers(device) ! DeviceConnectionHandler.SetValue(port, value)
  }
}
