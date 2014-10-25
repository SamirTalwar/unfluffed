package com.codurance

import java.io.{InputStream, OutputStream}
import java.nio.ByteBuffer
import java.nio.channels.Channels.newChannel
import java.nio.channels.{ReadableByteChannel, WritableByteChannel}

object IO {
  val BUFFER_CAPACITY_IN_BYTES = 16 * 1024

  def managed[C <: AutoCloseable](closeable: C) = new Managed[C](closeable)

  def copyAndClose(inputStream: InputStream, outputStream: OutputStream) {
    copyAndClose(newChannel(inputStream), newChannel(outputStream))
  }

  def copyAndClose(readableChannel: ReadableByteChannel, writeableChannel: WritableByteChannel) {
    for (inputChannel <- IO.managed(readableChannel);
         outputChannel <- IO.managed(writeableChannel)) {
      val buffer = ByteBuffer.allocate(BUFFER_CAPACITY_IN_BYTES)
      Iterator.continually(inputChannel.read(buffer)).takeWhile(_ >= 0).filter(_ > 0).foreach { _ =>
        buffer.flip()
        outputChannel.write(buffer)
        buffer.compact()
      }
    }
  }

  class Managed[C <: AutoCloseable](closeable: C) extends Traversable[C] {
    override def foreach[U](f: (C) => U) {
      try {
        f(closeable)
      } finally {
        closeable.close()
      }
    }
  }
}
