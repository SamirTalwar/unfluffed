package com.codurance.unfluffed

import java.nio.file.Path

import io.undertow.Handlers.resource
import io.undertow.predicate.Predicates._
import io.undertow.server.handlers.resource.{ClassPathResourceManager, FileResourceManager, ResourceHandler}

object StaticResources {
  val STATIC_RESOURCE_PATH = "com/codurance/unfluffed/static"

  val DIRECT_FILE_TRANSFER_LIMIT_IN_BYTES = 1024

  def applicationResources(applicationDirectory: Path): ResourceHandler = {
    resource(new FileResourceManager(
        applicationDirectory.toFile,
        DIRECT_FILE_TRANSFER_LIMIT_IN_BYTES))
      .setAllowed(not(path("application.conf")))
  }

  def frameworkResources: ResourceHandler = {
    resource(new ClassPathResourceManager(
        getClass.getClassLoader,
        STATIC_RESOURCE_PATH))
  }
}
