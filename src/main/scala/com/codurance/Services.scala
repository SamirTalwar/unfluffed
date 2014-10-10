package com.codurance

import scala.collection.JavaConverters._

import org.cometd.bayeux.server.{BayeuxServer, ServerMessage, ServerSession}
import org.cometd.server.AbstractService

object Services {
  def initialize(bayeux: BayeuxServer) {
    new HelloService(bayeux)
    new ClientIdentificationService(bayeux)
  }
}

class HelloService(bayeux: BayeuxServer) extends AbstractService(bayeux, "hello") {
  addService("/say/hello", "process")

  def process(remote: ServerSession, message: ServerMessage) {
    val id = message.getDataAsMap.get("id").asInstanceOf[Long]
    val output = Map("greeting" -> s"Hello from client $id.").asJava
    getBayeux.createChannelIfAbsent("/hello").getReference.publish(getServerSession, output)
  }
}

class ClientIdentificationService(bayeux: BayeuxServer) extends AbstractService(bayeux, "clientId") {
  addService("/client/identification/request", "process")

  var count = 0

  def process(remote: ServerSession, message: ServerMessage) {
    count += 1
    val output = Map("clientId" -> count).asJava
    remote.deliver(getServerSession, "/client/identification/response", output)
  }
}
