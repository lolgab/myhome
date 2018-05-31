package myhome

trait Api {
  def devices(): Seq[Device]

  def set(device: IpAddress, port: Id, value: Int): Unit

  def messages(): Seq[Message]

  def addMessage(message: Message): Unit

  def removeMessage(message: Message): Unit
}
