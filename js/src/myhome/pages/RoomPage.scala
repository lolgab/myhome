//package myhome.pages
//
//import com.thoughtworks.binding.Binding.Constants
//import com.thoughtworks.binding.dom
//import myhome.Room
//
//trait RoomPage {
//  @dom
//  def roomPage(room: Room) = {
//    <ul class="collection">
//      <li class="collection-header"><h3>Devices</h3></li>
//      {
//      for(d <- Constants(room.devices: _*)) yield
//        <li class="collection-item">
//          {d.id.toString}
//        </li>
//      }
//    </ul>
//  }
//}
