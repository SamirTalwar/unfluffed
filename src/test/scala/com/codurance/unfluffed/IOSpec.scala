package com.codurance.unfluffed

import java.nio.file.{Files, Path}

import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers}

import scala.collection.JavaConverters._

class IOSpec extends FunSuite with Matchers with BeforeAndAfterEach {
  private val FileContents = "This is a test."
  private var temporaryFile: Path = null

  override protected def beforeEach(): Unit = {
    temporaryFile = Files.createTempFile("test", ".txt")
    Files.write(temporaryFile, Seq(FileContents).asJava)
  }

  override protected def afterEach(): Unit = {
    Files.deleteIfExists(temporaryFile)
  }

  test("IO is managed inside a `for` comprehension") {
    var text: String = null
    for (input <- IO.managed(Files.newBufferedReader(temporaryFile))) {
      text = input.readLine()
    }
    text should be(FileContents)
  }
}
