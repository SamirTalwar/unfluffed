package com.codurance.unfluffed

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

object ApplicationConfiguration {
  def from(config: Config) = {
    def assetsIn(configPath: String) = {
      config.as[List[String]](configPath).map(Asset)
    }

    val processes = config.as[List[String]]("processes").map(Process)
    val assets = Assets(assetsIn("assets.css"), assetsIn("assets.js"))
    ApplicationConfiguration(processes, assets)
  }
}

case class ApplicationConfiguration(processes: List[Process], assets: Assets)

case class Process(name: String) {
  val path = s"/application/processes/$name.js"
}

case class Assets(css: List[Asset], js: List[Asset])

case class Asset(subpath: String) {
  val path = s"/application/assets/$subpath"
}
