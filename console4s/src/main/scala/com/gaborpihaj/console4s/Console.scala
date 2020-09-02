package com.gaborpihaj.console4s

import cats.Show
import cats.effect.Sync
import cats.kernel.Eq
import cats.syntax.apply._
import com.gaborpihaj.console4s.AutoCompletion

trait Console[F[_]] {
  def putStr(text: String): F[Unit]
  def putStrLn(): F[Unit]
  def putStrLn(text: String): F[Unit]
  def readLine(prompt: String): F[String]
  def readLine[Repr: Show: Eq](prompt: String, autocompletion: AutoCompletion[Repr]): F[(String, Option[Repr])]
  def readInt(prompt: String): F[Int]
  def readBool(prompt: String): F[Boolean]

  def clearScreen(): F[Unit]
  def moveToLastLine(): F[Unit]
}

object Console {
  def apply[F[_]: Sync](terminal: Terminal, lineReader: LineReader[F]): Console[F] =
    new Console[F] {
      def putStr(text: String): F[Unit] =
        write(text)

      def putStrLn(): F[Unit] =
        putStrLn("")

      def putStrLn(text: String): F[Unit] =
        write(s"$text\n")

      def readLine(prompt: String): F[String] =
        lineReader.readLine(prompt)

      def readLine[Repr: Show: Eq](prompt: String, autocompletion: AutoCompletion[Repr]): F[(String, Option[Repr])] =
        lineReader.readLine(prompt, autocompletion)

      def readInt(prompt: String): F[Int] =
        lineReader.readInt(prompt)

      def readBool(prompt: String): F[Boolean] =
        lineReader.readBool(prompt)

      def clearScreen(): F[Unit] =
        write(TerminalControl.clearScreen()) *> write(TerminalControl.move(1, 1))

      def moveToLastLine(): F[Unit] =
        write(TerminalControl.move(terminal.getHeight() - 1, 1))

      private def write(text: String): F[Unit] =
        Sync[F].delay(terminal.writer().write(text))
    }

  def apply[F[_]](implicit ev: Console[F]) = ev
}
