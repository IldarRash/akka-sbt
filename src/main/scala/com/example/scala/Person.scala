package com.example.scala

class Person(val name: String, val age: Int) {
  override def toString: String = "Name " + name + ", Age" + age
}

object Person {
  val MAX_AGE = 256;
  val MAX_NAME_LENGTH = 1024

  def apply(name: String, age: Int) = new Person(name, age)

}

object Demo2 extends App {
  val person0 = new Person("Mike", 45)

  val person1 = Person("Mike", 45)

  println("person0 " + person0.toString)
  println("person1 " + person1.toString)
}