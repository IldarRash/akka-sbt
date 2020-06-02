package com.example.akka

import akka.actor.{Actor, ActorRef, Props}

object SendingActor {
  def props(receiver: ActorRef) = Props(new SendingActor(receiver))
  case class Event(id: Long)
  case class SortEvents(unbolt: Vector[Event])
  case class SortedEvents(sorted: Vector[Event])
}

class SendingActor(receiver: ActorRef) extends Actor{
  import SendingActor._

  override def receive = {
    case SortEvents(unbolt) =>
      receiver ! SortedEvents(unbolt.sortBy(_.id))
  }

}
