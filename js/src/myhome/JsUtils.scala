package myhome

import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Vars}
import com.thoughtworks.binding.{Binding, dom}

import scala.util.Try

object JsUtils {
  def c[T](seq: collection.Seq[T]) = Constants(seq: _*)

  // some helpers for binding.scala
  @dom def futureBindingToBinding[T](futureBinding: Binding[Option[Try[T]]], emptyVal: T): Binding[T] = futureBinding.bind
    .map((y:Try[T]) => y.get)
    .getOrElse(emptyVal)

  @dom def futureBindingToBindingSeq[T](futureBindingSeq: Binding[Option[Try[Seq[T]]]], emptyVal: Seq[T]): Binding[BindingSeq[T]] = {
    val tmpSeq: Seq[T] = futureBindingSeq.bind
      .map((y: Try[Seq[T]]) => y.get)
      .getOrElse(emptyVal)
    val x: BindingSeq[T] = Vars(tmpSeq: _*)
    x
  }
}
