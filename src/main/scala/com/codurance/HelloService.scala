package com.codurance

import scala.collection.JavaConverters._

import org.cometd.bayeux.server.{BayeuxServer, ServerMessage, ServerSession}
import org.cometd.server.AbstractService

class HelloService(bayeux: BayeuxServer) extends AbstractService(bayeux, "hello") {
  addService("/service/hello", "processHello")

  def processHello(remote: ServerSession, message: ServerMessage) {
    val name = message.getDataAsMap.get("name").asInstanceOf[String]
    val output = Map("greeting" -> ("Hello, " + name)).asJava
    remote.deliver(getServerSession, "/hello", output)
  }
}
