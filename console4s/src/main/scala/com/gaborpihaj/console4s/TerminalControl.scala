package com.gaborpihaj.console4s

object TerminalControl {
  val bell = '\u0007'
  val esc = '\u001b'

  def up(n: Int = 1): String = csi(s"${n}A")

  def down(n: Int = 1) = csi(s"${n}B")

  def forward(n: Int = 1) = csi(s"${n}C")

  def back(n: Int = 1) = csi(s"${n}D")

  def clearScreen() = csi("2J")

  def clearLine() = csi("0K")

  def savePos() = esc("7")

  def restorePos() = esc("8")

  def sgrReset() = csi("0m")

  def bold() = csi("1m")

  @SuppressWarnings(Array("DisableSyntax.throw"))
  def move(row: Int, column: Int) =
    if (row < 0 || column < 0) throw new Exception(s"Row and column cannot be less than 0: ($row, $column) ")
    else csi(s"${row};${column}H")

  def csi(s: String): String = esc(s"[$s")

  def esc(s: String): String = s"$esc$s"
}
