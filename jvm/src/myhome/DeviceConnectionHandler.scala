package myhome

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.util.ByteString
import com.thoughtworks.binding.Binding.Var

object DeviceConnectionHandler {
  case class SetValue(port: Int, value: Int)
}

class DeviceConnectionHandler(deviceAddress: IpAddress, deviceActor: ActorRef)
    extends Actor
    with ActorLogging {

  import akka.io.Tcp._
  def receive: Receive = {
    case m: myhome.Message =>
      val valueStr = m match {
        case mp: RegulationToSwitchMessage =>
          mp.func match {
            case Off => 0
            case On  => 1
          }
        case mp: SwitchToSwitchMessage =>
          mp.func match {
            case Off => 0
            case On  => 1
          }
        case mp: RegulationToRegulationMessage =>
          mp.func match {
            case Set(v) => v
            case Inc(v) =>
              ApiImpl.myDevices
                .find(_.id == deviceAddress)
                .flatMap(_.ports.find(_.id == m.toPort))
                .map(_.value.value)
                .get + v
            case Dec(v) =>
              ApiImpl.myDevices
                .find(_.id == deviceAddress)
                .flatMap(_.ports.find(_.id == m.toPort))
                .map(_.value.value)
                .get - v
          }
        case mp: SwitchToRegulationMessage =>
          mp.func match {
            case Set(v) => v
            case Inc(v) =>
              ApiImpl.myDevices
                .find(_.id == deviceAddress)
                .flatMap(_.ports.find(_.id == m.toPort))
                .map(_.value.value)
                .get + v
            case Dec(v) =>
              ApiImpl.myDevices
                .find(_.id == deviceAddress)
                .flatMap(_.ports.find(_.id == m.toPort))
                .map(_.value.value)
                .get - v
          }
      }

      val toSend = s"${m.toPort},$valueStr\n"
      log.info(s"sending $toSend to $deviceActor")
      deviceActor ! Write(ByteString.fromString(toSend))

    case DeviceConnectionHandler.SetValue(port: Int, value: Int) =>
      val toSend = s"$port,$value\n"
      log.info(s"sending $toSend to $deviceActor")
      deviceActor ! Write(ByteString.fromString(toSend))

    case Received(data) =>
      val msg = data.utf8String
      log.info(s"received a message: $msg")
      val reg = "[0-9]+,[0-9]+,[0-9]+,[0-9]+(;[0-9]+,[0-9]+,[0-9]+,[0-9]+)*".r
      if (reg.findFirstIn(msg).isEmpty) {
        log.info(s"message incorrect: $msg")
        deviceActor ! Write(ByteString('\n'))
      } else {
        val portsStrings = msg.trim.split(";")
        val ports = for (p <- portsStrings; v = p.split(","))
          yield
            Port(v(0).toInt,
                 if (v(1).toInt == 0) In else Out,
                 if (v(2).toInt == 0) Regulation else Switch,
                 Var(v(3).toInt))

        for (m <- ApiImpl.myMessages) {
          log.info(s"m.fromDevice = ${m.fromDevice}")
          log.info(s"deviceAddress = $deviceAddress")

          if (m.fromDevice == deviceAddress) {
            val oldSenderValue: Option[Int] = ApiImpl._myDevices
              .find(_.id == m.fromDevice)
              .flatMap(_.ports.find(_.id == m.fromPort).map(_.value.value))
            val newSenderValue = ports.find(_.id == m.fromPort).map(_.value)
            log.info(s"pValue = ${newSenderValue.map(_.value)}")
            val trig: Option[Boolean] = m match {
              case mp: RegulationToSwitchMessage =>
                mp.trigger match {
                  case OnLessThan(v: Int) =>
                    for (oldV <- oldSenderValue; newV <- newSenderValue) yield {
                      oldV >= v &&
                      newV.value < v
                    }

                  case OnMoreThan(v: Int) =>
                    for (oldV <- oldSenderValue; newV <- newSenderValue) yield {
                      oldV <= v &&
                        newV.value > v
                    }
                }

              case mp: RegulationToRegulationMessage =>
                mp.trigger match {
                  case OnLessThan(v: Int) =>
                    for (oldV <- oldSenderValue; newV <- newSenderValue) yield {
                      oldV >= v &&
                        newV.value < v
                    }

                  case OnMoreThan(v: Int) =>
                    for (oldV <- oldSenderValue; newV <- newSenderValue) yield {
                      oldV <= v &&
                        newV.value > v
                    }
                }

              case mp: SwitchToSwitchMessage =>
                mp.trigger match {
                  case OnOn =>
                    for (oldV <- oldSenderValue; newV <- newSenderValue) yield {
                      oldV != 1 &&
                        newV.value == 1
                    }

                  case OnOff =>
                    for (oldV <- oldSenderValue; newV <- newSenderValue) yield {
                      oldV != 0 &&
                        newV.value == 0
                    }
                }
              case mp: SwitchToRegulationMessage =>
                mp.trigger match {
                  case OnOn =>
                    for (oldV <- oldSenderValue; newV <- newSenderValue) yield {
                      oldV != 1 &&
                        newV.value == 1
                    }

                  case OnOff =>
                    for (oldV <- oldSenderValue; newV <- newSenderValue) yield {
                      oldV != 0 &&
                        newV.value == 0
                    }
                }

            }

            for (t <- trig; if t) {
              log.info(s"trigger ok for $m")
              context.parent ! m
            }
          }
        }
        log.info(s"calling receivedState with ${Device(deviceAddress, ports.toSeq)}")
        ApiImpl.receivedState(Device(deviceAddress, ports.toSeq))
      }
//      sender() ! Write(
//        ByteString.fromString(s"Hi from server! mi hai scritto ${data.utf8String}\n"))
  }
}
