package myhome

object Requests {
  def setSwitch(device: IpAddress, port: Id, value: Boolean): String = s"setSwitch/$device/$port/$value"
  def setRegulation(device: IpAddress, port: Id, value: Byte): String = s"setRegulation/$device/$port/$value"
}
