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

    val title = config.getString("title")
    val components = config.as[List[String]]("components").map(Component)
    val assets = Assets(assetsIn("assets.css"), assetsIn("assets.js"))
    ApplicationConfiguration(title, components, assets)
  }
}

case class ApplicationConfiguration(title: String, components: List[Component], assets: Assets)

case class Component(name: String) {
  val uri: Uri = "application" / "components" / s"$name.js"
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
