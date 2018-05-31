package myhome.components

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.dom
import org.scalajs.dom.Event

trait Login {
  private val loginVar    = Var("")
  private val passwordVar = Var("")

  @dom
  private val usernameInput = <input id="idUsername"
                                     type="text"
                                     class="validate"
                                     oninput={_: Event => loginVar.value = idUsername.value} />

  @dom
  private val passwordInput = <input id="idPassword"
                                     type="password"
                                     class="validate"
                                     oninput={_: Event => passwordVar.value = idPassword.value} />

  @dom
  def login = {
    <div class="row">
      <form class="col s12 l6">
        <div class="row">
          <div class="input-field col s12">
            {usernameInput.bind}
            <label for="confermaPassword">Login</label>
          </div>
          <div class="input-field col s12">
            {passwordInput.bind}
            <label for="password">Password</label>
          </div>
        </div>
      </form>
    </div>
  }
}
