package com.gaborpihaj.console4s

import cats.data.Chain

package object linereader {
  private[linereader] type ByteSeq = Chain[Int]
}
