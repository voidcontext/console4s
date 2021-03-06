package com.gaborpihaj.console4s.linereader

import cats.Eval
import cats.data.{Chain, RWS, RWST}
import cats.instances.option._
import cats.instances.unit._
import cats.syntax.apply._
import cats.syntax.flatMap._
import com.gaborpihaj.console4s.AutoCompletion

private[linereader] case class LineReaderState[Repr](
  keys: Chain[ByteSeq],
  column: Int,
  input: String,
  selectedCompletion: Option[(Int, String, Repr)],
  completionResult: Option[Repr]
)

private[linereader] object LineReaderState {

  type StateUpdate[Repr, A] = RWS[Env[Repr], Unit, LineReaderState[Repr], A]
  object StateUpdate {
    def apply[Repr, A](f: (Env[Repr], LineReaderState[Repr]) => (LineReaderState[Repr], A)): StateUpdate[Repr, A] =
      RWST.apply((e, s) => Eval.now(f(e, s) match { case (s, r) => ((), s, r) }))

    def inspect[Repr, A](f: (Env[Repr], LineReaderState[Repr]) => A): StateUpdate[Repr, A] =
      RWST.apply((e, s) => Eval.now(((), s, f(e, s))))

    def modify[Repr](f: (Env[Repr], LineReaderState[Repr]) => LineReaderState[Repr]): StateUpdate[Repr, Unit] =
      ask[Repr] >>= { e => RWS.modify(s => f(e, s)) }

    def ask[Repr]: StateUpdate[Repr, Env[Repr]] =
      RWS.ask[Env[Repr], Unit, LineReaderState[Repr]]

    def now[Repr, A](a: => A): StateUpdate[Repr, A] =
      RWST.liftF(Eval.now(a))

    def pure[Repr, A](a: A): StateUpdate[Repr, A] =
      RWS.pure(a)

    def lift[Repr, A](f: (Env[Repr], LineReaderState[Repr]) => StateUpdate[Repr, A]): StateUpdate[Repr, A] =
      for {
        e <- ask[Repr]
        s <- RWS.get[Env[Repr], Unit, LineReaderState[Repr]]
        r <- f(e, s)
      } yield r
  }

  case class Env[Repr](
    currentRow: Int,
    prompt: String,
    filter: Chain[Int] => Boolean,
    readWhile: Chain[Int] => Boolean,
    autocompletion: Option[AutoCompletion[Repr]]
  )

  def empty[Repr]: LineReaderState[Repr] = apply(Chain.empty, 0, "", None, None)

  implicit class LineReaderStateOps[Repr](state: LineReaderState[Repr]) {
    def moveColumnBy(n: Int): LineReaderState[Repr] = state.copy(column = state.column + n)
    def prependKeys(keys: ByteSeq): LineReaderState[Repr] = state.copy(keys = Chain.one(keys) ++ state.keys)
    def withInput(input: String, env: Env[Repr], write: String => Unit): LineReaderState[Repr] = {
      (
        env.autocompletion,
        state.completionResult
      ).mapN { case (ac, _) => ac.config.onResultChange(None, write) }
      state.copy(input = input, completionResult = None)
    }
    def selectCompletion(index: Int, input: String, selected: Repr): LineReaderState[Repr] =
      state.copy(selectedCompletion = Option((index, input, selected)))
    def selectResult(result: Repr) = state.copy(completionResult = Option(result))
    def result: (String, Option[Repr]) = state.input -> state.completionResult
  }
}
