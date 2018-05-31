//package myhome.pages
//
//import com.thoughtworks.binding.{FutureBinding, dom}
//import myhome.FutureBindingUtils._
//import myhome.Main
//import myhome.components.Components._
//import myhome._
//
//import scala.concurrent.Future
//import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
//
//trait Rooms {
//  private val roomsFuture = FutureBinding(
//    Future.successful(
//      Seq(
////        Room(Room.Name("Cucina"), Seq(Device(Device.Id(1)))),
////        Room(Room.Name("Bagno"),
////             Seq(
////               Device(Device.Id(2)),
////               Device(Device.Id(5)),
////               Device(Device.Id(6)),
////               Device(Device.Id(7))
////             )),
////        Room(Room.Name("Stanza da letto"), Seq(Device(Device.Id(3)))),
////        Room(Room.Name("Magazzino"), Seq(Device(Device.Id(4))))
//      )
//    )
//  )
//
//  @dom
//  private def room(room: Room) = {
//    <li class="collection-item"> <!-- onclick={openRoom(room)} -->
//      {room.name}
//      <span class="badge">{room.devices.length.toString} devices</span>
//    </li>
//  }
//
//  @dom
//  def addRoom = {
//    <!-- Modal Trigger -->
//      <a class="waves-effect waves-light btn modal-trigger" href="#modal1">Modal</a>
//
//    <!-- Modal Structure -->
//      <div id="modal1" class="modal">
//        <div class="modal-content">
//          <h4>Modal Header</h4>
//          <p>A bunch of text</p>
//        </div>
//        <div class="modal-footer">
//          <a href="#!" class="modal-action modal-close waves-effect waves-green btn-flat">Agree</a>
//        </div>
//      </div>
//  }
//
//  @dom
//  def rooms = {
//    <div>
//      <ul class="collection">
//        {
//        val f = futureBindingToBindingSeq(roomsFuture, Seq.empty[Room])
//        for (r <- f.bind) yield room(r).bind
//        }
//      </ul>
//
//      <div class="fixed-action-btn">
//        <a class="btn-floating btn-large waves-effect waves-light red modal-trigger" href="#modal1">
//          <i class="large material-icons">add</i>
//        </a>
//      </div>
//      <!-- Modal Structure -->
//      <div id="modal1" class="modal">
//        <div class="modal-content">
//          <h4>Modal Header</h4>
//          <p>A bunch of text</p>
//        </div>
//        <div class="modal-footer">
//          <a href="#!" class="modal-action modal-close waves-effect waves-green btn-flat">Agree</a>
//        </div>
//      </div>
//    </div>
//
//
//  }
//}
