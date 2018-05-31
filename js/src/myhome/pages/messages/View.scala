package myhome.pages.messages

import com.thoughtworks.binding.{Binding, dom}
import myhome.JsUtils.c
import myhome._
import myhome.Model.messages

import scalajs.js.Dynamic.{global => g}
import org.scalajs.dom.raw.Event
import autowire._
import com.thoughtworks.binding.Binding.Var
import myhome.upickleModelImplicits._
import org.scalajs.dom.html.Div

import scalajs.concurrent.JSExecutionContext.Implicits.queue

object View {
  val showAddMessage = Var(false)

  @dom def triggerOf(m: Message) = m.trigger.toString

  @dom def functionOf(m: Message) = m.func.toString

  @dom def message(m: Message) = {
    <li class="collection-header">
      Message from {m.fromDevice.toString}, Port {m.fromPort.toString}<br />
      to {m.toDevice.toString}, Port {m.toPort.toString}<br />
      Trigger: {triggerOf(m).bind}<br />
      Function: {functionOf(m).bind} <br />
      <a class="btn-medium waves-effect waves-light" onclick={(_: Event) => Ajaxer[Api].removeMessage(m).call()}>Remove</a>
    </li>
  }

  @dom def addButton = {
    val button =
      <a class="btn-large waves-effect waves-light red" onclick={(e: Event) => showAddMessage.value = !showAddMessage.value }>
        Add Message
      </a>
    button
  }

  val currentFromDevice    = Var("")
  val currentToDevice      = Var("")
  val currentFromPort      = Var(0)
  val currentToPort        = Var(0)
  val currentTrigger       = Var("")
  val currentTriggerValue  = Var(0)
  val currentFunction      = Var("")
  val currentFunctionValue = Var(0)

  def confirm(): Unit = {
    val fromDevice = IpAddress(currentFromDevice.value)
    val toDevice   = IpAddress(currentToDevice.value)
    val trigger: Trigger = currentTrigger.value match {
      case "OnOn"       => OnOn
      case "OnOff"      => OnOff
      case "OnLessThan" => OnLessThan(currentTriggerValue.value)
      case "OnMoreThan" => OnMoreThan(currentTriggerValue.value)
    }
    val function: Function = currentFunction.value match {
      case "On"  => On
      case "Off" => Off
      case "Inc" => Inc(currentFunctionValue.value)
      case "Dec" => Dec(currentFunctionValue.value)
      case "Set" => Set(currentFunctionValue.value)
    }
    val message: Message = (trigger, function) match {
      case (t: SwitchTrigger, f: SwitchFunc) =>
        SwitchToSwitchMessage(fromDevice,
                              currentFromPort.value,
                              toDevice,
                              currentToPort.value,
                              t,
                              f)
      case (t: SwitchTrigger, f: RegulationFunc) =>
        SwitchToRegulationMessage(fromDevice,
                                  currentFromPort.value,
                                  toDevice,
                                  currentToPort.value,
                                  t,
                                  f)
      case (t: RegulationTrigger, f: SwitchFunc) =>
        RegulationToSwitchMessage(fromDevice,
                                  currentFromPort.value,
                                  toDevice,
                                  currentToPort.value,
                                  t,
                                  f)
      case (t: RegulationTrigger, f: RegulationFunc) =>
        RegulationToRegulationMessage(fromDevice,
                                      currentFromPort.value,
                                      toDevice,
                                      currentToPort.value,
                                      t,
                                      f)
    }
    Ajaxer[Api].addMessage(message).call()
    showAddMessage.value = false
  }

  Binding(println(s"currentFromDevice = ${currentFromDevice.bind}")).watch()
  Binding(println(s"currentToDevice = ${currentToDevice.bind}")).watch()
  Binding(println(s"currentFromPort = ${currentFromPort.bind}")).watch()
  Binding(println(s"currentToPort = ${currentToPort.bind}")).watch()
  Binding(println(s"currentTrigger = ${currentTrigger.bind}")).watch()
  Binding(println(s"currentTriggerValue = ${currentTriggerValue.bind}")).watch()
  Binding(println(s"currentFunction = ${currentFunction.bind}")).watch()
  Binding(println(s"currentFunctionValue = ${currentFunctionValue.bind}")).watch()

  @dom def triggerCiao = {
    currentTrigger.bind match {
      case "OnLessThan" | "OnMoreThan" =>
        <input placeholder="Trigger Value" id="triggerValue" oninput={(_: Event) => currentTriggerValue.value = triggerValue.value.toInt}></input>
      case _ => <!-- -->
    }
  }

  @dom def addMessage = {
    val res = { <div class="input-field col s12">
      <select id="fromDeviceSelect" onchange={(e: Event) => println(g.$("#fromDeviceSelect").`val`().asInstanceOf[String]); currentFromDevice.value = g.$("#fromDeviceSelect").`val`().asInstanceOf[String]}>
        { for(d <- c(Model.devices.bind)) yield <option value={d.id.toString}>Device {d.id.toString}</option> }
      </select>
      <label>Sender Device</label>
    </div>
    <div class="input-field col s12">
      <input placeholder="Sender Port" id="fromPortSelect" oninput={(_: Event)=> currentFromPort.value = fromPortSelect.value.toInt}>
      </input>
    </div>
    <div class="input-field col s12">
      <select id="toDeviceSelect" onchange={(e: Event) => currentToDevice.value = g.$("#toDeviceSelect").`val`().asInstanceOf[String]}>
        { for(d <- c(Model.devices.bind)) yield <option value={d.id.toString}>Device {d.id.toString}</option> }
      </select>
      <label>Receiver Device</label>
    </div>
    <div class="input-field col s12">
      <input placeholder="Receiver Port" id="toPortSelect" oninput={(_: Event) => currentToPort.value = toPortSelect.value.toInt}>
      </input>
    </div>
      <div class="input-field col s12">
        <select id="triggerSelect" onchange={(e: Event) => currentTrigger.value = g.$("#triggerSelect").`val`().asInstanceOf[String]}>
          <option value="OnOn">On On</option>
          <option value="OnOff">On Off</option>
          <option value="OnLessThan">On Less Than</option>
          <option value="OnMoreThan">On More Than</option>
        </select>
        <label>Trigger</label>
      </div>
      <div>
      {triggerCiao.bind}
      </div>

      <div class="input-field col s12">
        <select id="functionSelect" onchange={(e: Event) => currentFunction.value = g.$("#functionSelect").`val`().asInstanceOf[String]}>
          <option value="On">On</option>
          <option value="Off">Off</option>
          <option value="Inc">Increase</option>
          <option value="Dec">Decrease</option>
          <option value="Set">Set</option>
        </select>
        <label>Function</label>
      </div>
      <div>{if(currentFunction.bind == "Inc" || currentFunction.bind == "Dec" || currentFunction.bind == "Set" )
        <input placeholder="Function Value" id="functionValue" oninput={(_: Event) => currentFunctionValue.value = functionValue.value.toInt}></input>
      else <!-- -->}</div>
      <a class="btn-large waves-effect waves-light blue" onclick={(e: Event) => confirm() }>
        Confirm
      </a> }

    g.$(g.document).ready(() => g.$("select").material_select())

    res
  }

  @dom def render = {
    <div>
      <ul class="collection"> {
        for {
          m <- c(messages.bind)
        } yield message(m).bind
      }
      </ul>
      {addButton.bind}
      {if(showAddMessage.bind) addMessage.bind else <div></div><div></div>}
    </div>
  }
}
