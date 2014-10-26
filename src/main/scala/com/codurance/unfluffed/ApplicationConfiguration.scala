package com.codurance.unfluffed

import com.netaporter.uri.Uri
import com.netaporter.uri.dsl._
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._

object ApplicationConfiguration {
  def from(config: Config) = {
    def assetsIn(configPath: String) = {
      config.as[Option[List[String]]](configPath).getOrElse(List()).map(Asset)
    }

    val processes = config.as[List[String]]("processes").map(Process)
    val assets = Assets(assetsIn("assets.css"), assetsIn("assets.js"))
    ApplicationConfiguration(processes, assets)
  }
}

case class ApplicationConfiguration(processes: List[Process], assets: Assets)

case class Process(name: String) {
  val uri: Uri = "application" / "processes" / s"$name.js"
}

case class Assets(css: List[Asset], js: List[Asset])

case class Asset(asset: String) {
  val uri: Uri = {
    val assetUri: Uri = Uri.parse(asset)
    if (assetUri.host.isDefined) {
      assetUri
    } else {
      "application" / "assets" / asset
    }
  }
}
