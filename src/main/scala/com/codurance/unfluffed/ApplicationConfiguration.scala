package com.codurance.unfluffed

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

object ApplicationConfiguration {
  def from(config: Config) = {
    val processes = config.as[List[String]]("processes").map(Process)
    ApplicationConfiguration(processes)
  }
}

case class ApplicationConfiguration(processes: List[Process])

case class Process(name: String) {
  val path = s"/application/processes/$name.js"
}
