package com.gaborpihaj.example

import java.io.File

import cats.effect.{ExitCode, IO, IOApp}
import cats.instances.string._
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.gaborpihaj.console4s.AutoCompletionConfig.Down
import com.gaborpihaj.console4s._
import com.gaborpihaj.console4s.iterm.extras.Iterm

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    Terminal[IO].use { terminal =>
      val lineReader = LineReader[IO](terminal)
      implicit val console = Console[IO](terminal, lineReader)

      val autocomplete: AutoCompletionSource[String] = str =>
        List(
          "foo",
          "bar",
          "baz",
          "foobar",
          "foobarbaz"
        ).filter(_.startsWith(str)).map(s => s -> s)

      implicit val acConfig: AutoCompletionConfig[String] = AutoCompletionConfig.defaultAutoCompletionConfig.copy(
        direction = Down,
        onResultChange = (maybeString, write) =>
          write(
            TerminalControl.savePos() +
              TerminalControl.up(1) +
              TerminalControl.back(999) +
              TerminalControl.clearLine() +
              s"Currently selected: $maybeString" +
              TerminalControl.restorePos()
          )
      )

      for {
        _    <- console.clearScreen()
        _    <- imageExample()
        _    <- console.clearScreen()
        _    <- console.putStrLn(TerminalControl.down())
        str  <- console.readLine("prompt > ", autocomplete)
        _    <- console.clearScreen()
        int  <- console.readInt("What's you favourite number? ")
        _    <- console.clearScreen()
        bool <- console.readBool("Are you sure? (y/n) ")
        _    <- console.clearScreen()
        _    <- console.putStrLn(s"Results: '$str', '$int', '$bool'")
      } yield ExitCode.Success
    }

  private def imageExample()(implicit c: Console[IO]): IO[Unit] =
    if (Iterm.isIterm)
      c.clearScreen() >>
        Iterm.printImage[IO](new File("examples/src/main/resources/file_example_PNG_500kb.png")) >>
        c.readLine("Press enter to continue...").void
    else IO.unit
}
