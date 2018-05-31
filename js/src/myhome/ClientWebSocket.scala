//package myhome
//import com.thoughtworks.binding.Binding.Var
//import org.scalajs.dom.raw.MessageEvent
//import org.scalajs.dom.{WebSocket, window}
//
//object ClientWebSocket {
//  private lazy val wsURL = s"ws://${window.location.host}/ws"
//
//  lazy val socket = new WebSocket(wsURL)
//
//  socket.onmessage = { (e: MessageEvent) =>
//    val json = ujson.read(e.data.toString)
//
//    val device = json("device").num.toInt
//    val port = json("port").num.toInt
////    val value = json("value")
//
//    for {
//      device <- Model.devices.value.find(_.id == device)
//      port <- device.ports.find(_.id == port)
//    } {
//      port.portType match {
//        case RegulationType(value: Var[Byte]) => value.value = json.num.toByte
//        case SwitchType(value: Var[Boolean]) => value.value = json.str == "true"
//      }
//    }
//  }
//
//}
