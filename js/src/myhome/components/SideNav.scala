package myhome.components

import com.thoughtworks.binding.dom

import scala.scalajs.js

trait SideNav {
  @dom
  def sideNav = {
    val res = <div>
      <ul id="slide-out" class="side-nav">
        <li>
          <div class="user-view">
            <div class="backgroud">
              <img src=""></img>
            </div>
          </div>
        </li>

        <li><a>Menu1</a></li>
        <li><a>Menu2</a></li>
        <li><a>Menu3</a></li>
      </ul>
    </div>
    res
  }
}
