package com.gaborpihaj.console4s

import cats.effect.{Resource, Sync}
import com.gaborpihaj.console4s.core
import org.jline.keymap.BindingReader
import org.jline.terminal.TerminalBuilder

object Terminal {
  type Row = core.Terminal.Row
  type Column = core.Terminal.Column
  type Writer = core.Terminal.Writer
  type Reader = core.Terminal.Reader

  def apply[F[_]: Sync]: Resource[F, Terminal] =
    Resource
      .make(
        Sync[F].delay {
          val terminal = TerminalBuilder
            .builder()
            .system(true)
            .jansi(true)
            .build()
          terminal.enterRawMode()
          terminal.echo(false)
          terminal
        }
      )(terminal =>
        Sync[F].delay {
          terminal.flush()
          terminal.close()
        }
      )
      .map { underlying =>
        new Terminal {

          def writer(): Writer = _writer

          def reader(): Reader = _reader

          def flush(): Unit = underlying.flush()

          def getCursorPosition(): (Int, Int) = {
            val pos = underlying.getCursorPosition(_ => ())

            (pos.getY() + 1, pos.getX() + 1)
          }

          def getHeight(): Int = underlying.getHeight()

          private val _writer = new Writer {
            def write(s: String): Unit = underlying.writer().write(s)
          }

          private val _reader = new Reader {
            def readchar(): Int = bindingReader.readCharacter()
            private val bindingReader = new BindingReader(underlying.reader())
          }
        }
      }
}
