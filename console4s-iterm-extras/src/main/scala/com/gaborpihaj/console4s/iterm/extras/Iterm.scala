package com.gaborpihaj.console4s.iterm.extras

import java.io.File
import java.nio.file.Files
import java.util.Base64

import cats.FlatMap
import cats.effect.Sync
import cats.instances.string._
import cats.syntax.eq._
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.gaborpihaj.console4s.Console
import com.gaborpihaj.console4s.TerminalControl.{bell, esc}

object Iterm {
  def printImage[F[_]: FlatMap: Console](base64Encoded: String): F[Unit] =
    Console[F].putStrLn(esc(s"]1337;File=inline=1:$base64Encoded$bell"))

  def printImage[F[_]: FlatMap: Console](raw: Array[Byte]): F[Unit] =
    printImage(Base64.getEncoder().encodeToString(raw))

  def printImage[F[_]: Sync: Console](file: File): F[Unit] =
    for {
      bytes <- Sync[F].delay(Files.readAllBytes(file.toPath()))
      _     <- printImage(bytes)
    } yield ()

  def isIterm: Boolean =
    sys.env.get("LC_TERMINAL").exists(_ === "iTerm2")
}
