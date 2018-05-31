package myhome.components

import com.thoughtworks.binding.dom
import myhome.{Main, Model, pages}
import org.scalajs.dom.raw.Event

trait BarraSuperiore {
  @dom
  def barraSuperiore = {
    <nav>
      <div class="nav-wrapper">
        <a class="brand-logo left">
          My Home
        </a>
        <ul id="nav-mobile" class="right">
          <li><a onclick={(e: Event) => Main.actualPage.value = pages.devices.View.render}>Devices</a></li>
          <li><a onclick={(e: Event) => Main.actualPage.value = pages.messages.View.render}>Messages</a></li>
        </ul>
      </div>
    </nav>
  }
}
