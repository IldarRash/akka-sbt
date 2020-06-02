package com.example.akka.testDriven

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

case class Greeting(message: String)

class Greeter extends Actor with ActorLogging{
  override def receive = {
    case Greeting(message) =>
      log.info("Hello {}!", message)
  }
}

object Greeter02 {
  def props(listener: Option[ActorRef] = None) =
    Props(new Greeter02(listener))
}
class Greeter02(listener: Option[ActorRef])
  extends Actor with ActorLogging {
  def receive = {
    case Greeting(who) =>
      val message = "Hello " + who + "!"
      log.info(message)
      listener.foreach(_ ! message)
  }
}
