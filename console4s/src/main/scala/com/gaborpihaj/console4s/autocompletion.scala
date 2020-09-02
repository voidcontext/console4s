package com.gaborpihaj.console4s

import cats.Eq
import com.gaborpihaj.console4s.AutoCompletion._

case class AutoCompletion[Repr](
  source: AutoCompletionSource[Repr],
  config: AutoCompletionConfig[Repr]
)

object AutoCompletion {
  trait AutoCompletionSource[Repr] {
    def candidates(fragment: String): List[(String, Repr)]
  }

  case class AutoCompletionConfig[Repr](
    maxCandidates: Int,
    strict: Boolean,
    direction: Direction,
    onResultChange: (Option[Repr], String => Unit) => Unit
  )

  sealed trait Direction
  case object Up extends Direction
  case object Down extends Direction

  object Direction {
    implicit val eq: Eq[Direction] = Eq.fromUniversalEquals
  }

  def defaultAutoCompletionConfig[Repr]: AutoCompletionConfig[Repr] =
    AutoCompletionConfig(
      maxCandidates = 5,
      strict = false,
      direction = Up,
      onResultChange = (_, _) => ()
    )

}
