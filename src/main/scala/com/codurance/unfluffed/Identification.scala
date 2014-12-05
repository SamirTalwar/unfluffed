package com.codurance.unfluffed

import org.cometd.bayeux.server.{BayeuxServer, ServerMessage, ServerSession}
import org.cometd.server.AbstractService

import scala.collection.JavaConverters._

class Identification(bayeux: BayeuxServer) extends AbstractService(bayeux, "hello") {
  addService("/framework/identification", "process")

  def process(remote: ServerSession, message: ServerMessage) {
    val response = Map("id" -> remote.getId).asJava
    remote.deliver(getServerSession, "/framework/identification", response)
  }
}

object Identification {
  def initialize(bayeux: BayeuxServer) {
    new Identification(bayeux)
  }
}
