package com.codurance.unfluffed

object IO {
  val BUFFER_CAPACITY_IN_BYTES = 16 * 1024

  def managed[C <: AutoCloseable](closeable: C) = new Managed[C](closeable)

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
