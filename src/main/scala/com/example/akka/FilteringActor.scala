package com.example.akka

import akka.actor.{Actor, ActorRef, Props}

object FilteringActor {
  def props(receiver: ActorRef, bufferSize: Int) =
          Props(new FilteringActor(receiver, bufferSize))
  case class Event(id: Long)
}

class FilteringActor(nextActor: ActorRef, bufferSize: Int) extends Actor {
    import FilteringActor._

  var lastMessages = Vector[Event]()

  override def receive = {
    case msg: Event =>
      if(!lastMessages.contains(msg)) {
        lastMessages = lastMessages :+ msg
        nextActor ! msg
        if (lastMessages.size > bufferSize) {
          lastMessages = lastMessages.tail
        }
      }
  }
}
