package com.example.scala

object Demo {
  def main(args: Array[String]): Unit = {
      print(UtilityObject.max(0, 1))
  }
}

object UtilityObject {
  val MAX_INT = Integer.MAX_VALUE

  def max(x: Int, y: Int) = x > y
}
