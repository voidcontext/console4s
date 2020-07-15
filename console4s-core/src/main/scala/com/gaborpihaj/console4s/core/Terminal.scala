package com.gaborpihaj.console4s.core

trait Terminal {
  def writer(): Terminal.Writer
  def reader(): Terminal.Reader
  def flush(): Unit
  def getCursorPosition(): (Terminal.Row, Terminal.Column)
  def getHeight(): Int
}

object Terminal {
  type Row = Int
  type Column = Int
  trait Writer {
    def write(s: String): Unit
  }

  trait Reader {
    def readchar(): Int
  }
}
