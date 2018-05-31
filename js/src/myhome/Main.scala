package myhome

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.dom
import myhome.components.Components
import org.scalajs.dom.document
import autowire._
import org.scalajs.dom.raw.Node

import scalajs.concurrent.JSExecutionContext.Implicits.queue

object Main {
  val actualPage = Var(pages.devices.View.render)
  @dom
  def render = {
    <div>
      {Components.barraSuperiore.bind}
      {Components.sideNav.bind}
      {actualPage.bind.bind}
    </div>
  }

  def main(args: Array[String]): Unit = {
    dom.render(document.getElementById("root"), render)
  }
}
