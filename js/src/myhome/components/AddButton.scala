package myhome.components

import com.thoughtworks.binding.dom
import org.scalajs.dom.MouseEvent

trait AddButton {
  @dom
  def addButton(onClick: => Unit) = {

    <div class="fixed-action-btn">
      <a class="btn-floating btn-large waves-effect waves-light red" onclick={(_: MouseEvent) => onClick}>
        <i class="large material-icons">add</i>
      </a>
      <!--
      <ul>
        <li><a class="btn-floating red"><i class="material-icons">insert_chart</i></a></li>
        <li><a class="btn-floating yellow darken-1"><i class="material-icons">format_quote</i></a></li>
        <li><a class="btn-floating green"><i class="material-icons">publish</i></a></li>
        <li><a class="btn-floating blue"><i class="material-icons">attach_file</i></a></li>
      </ul>
      -->
    </div>
  }

}
