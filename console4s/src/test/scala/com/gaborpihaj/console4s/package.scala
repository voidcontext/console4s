package com.gaborpihaj

import cats.kernel.Semigroup

package object console4s {
  def strToChars(s: String): List[Int] = s.toCharArray.map(_.toInt).toList

  def repeat[A](a: A, n: Int)(implicit S: Semigroup[A]): A =
    if (n <= 1) a
    else S.combine(a, repeat(a, n - 1))
}
