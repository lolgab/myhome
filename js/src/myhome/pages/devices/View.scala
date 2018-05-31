package myhome.pages.devices

import com.thoughtworks.binding.{Binding, dom}
import myhome.JsUtils._
import myhome._
import org.scalajs.dom.Event
import org.scalajs.dom.html.Input
import autowire._
import upickleModelImplicits._
import scalajs.concurrent.JSExecutionContext.Implicits.queue

object View {
  def set(device: IpAddress, pId: Id, value: Int) = {
    for {
      d <- Model._devices.value.find(_.id == device)
      p <- d.ports.find(_.id == pId)
    } {
      p.value.value = value
      Ajaxer[Api].set(device, pId, value).call()
    }
  }

  def toggle(device: IpAddress, pId: Id) = {
    for {
      d <- Model._devices.value.find(_.id == device)
      p <- d.ports.find(_.id == pId)
    } {
      Ajaxer[Api].set(device, pId, 1 - p.value.value).call()
      p.value.value = 1 - p.value.value
    }
  }


  import Model._

  @dom
  def switchOut(value: Binding[Int], onchange: Event => Unit) = {
    <span class="switch badge right">
      <label>
        Off
        <input type="checkbox" checked={if(value.bind == 1) true else false} onchange={onchange}></input>
        <span class="lever"></span>
        On
      </label>
    </span>
  }
  @dom def portLi(device: Device, port: Port) = port match {
    case Port(pId, Out, Switch, v) =>
      //Some(pId -> v)
      def checkedValue = Binding { v.bind }
      <li class="collection-item">
        Port {pId.toString}
        {switchOut(v, (e: Event) => toggle(device.id, pId)).bind}
      </li>
//      <li class="collection-item">Porta di uscita switch</li>
    case Port(pId, In, Switch, v) =>
      <li class="collection-item">
        Port {pId.toString}
        <span class="badge right">
          <i class="small material-icons">{if(v.bind == 0) "flash_off" else "flash_on"}</i>
        </span>
      </li>
    case Port(pId, In, Regulation, v) =>
      <li class="collection-item">Port {pId.toString}
        <span class="badge right progress">
          <span class="determinate" style={s"width: ${v.bind * 100.0 / 255}%"}></span>
        </span>
      </li>
    case Port(pId, Out, Regulation, v) =>
      <li class="collection-item">
        Port {pId.toString}
        <span class="badge right range-field">
          <input type="range" min="0" max="255" value={v.bind.toString} onchange={(e: Event) => {
            set(device.id, pId, e.currentTarget.asInstanceOf[Input].valueAsNumber)
        }}/>
        </span>
      </li>
  }
  @dom
  def ports(device: Device) =
    for (port <- c(device.ports)) yield portLi(device, port).bind

  /*
   *
  @dom
  def ports(device: Device) = {
    val switches = for {
      allPort <- device.ports
      (pId, v) <- allPort match {
        case Port(pId, Out, Switch, v) =>
          //Some(pId -> v)
          def checkedValue = Binding { v.bind }
          <li class="collection-item">{badge(v, Controller.toggle(devices.bind, device.id, pId)).bind}</li>

        case Port(pId, In, Switch, v) =>
        case Port(pId, In, Regulation, v) =>
        case Port(pId, Out, Switch, v) =>

      }
    } yield (pId, v)

    for ((pId, v) <- c(switches)) yield {
      def checkedValue = Binding { v.bind }
      <li class="collection-item">{badge(v, Controller.toggle(devices.bind, device.id, pId)).bind}</li>
    }
  }
   * */

  @dom
  def render = {
    <div> {
    for {
      d <- c(devices.bind)
    } yield <ul class="collection">
      <li class="collection-header">Device
        {d.id.toString}
      </li>{ports(d).bind}
    </ul>
    }</div>
  }

}
