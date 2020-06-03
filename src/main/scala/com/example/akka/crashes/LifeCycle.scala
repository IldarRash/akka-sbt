package com.example.akka.crashes

import akka.actor.{Actor, ActorLogging}
import com.example.akka.crashes.LifeCycle.{ForceRestart, ForceRestartException}

object LifeCycle {

  object SampleMessage

  object ForceRestart

  private class ForceRestartException extends IllegalArgumentException("force restart")

}

class LifeCycle extends Actor with ActorLogging {

  log.info("Constructor")

  override def preStart(): Unit = {
    log.info("preStart")
  }



  override def postStop(): Unit = {
    log.info("postStop")
  }



  override def preRestart(reason: Throwable,
                          message: Option[Any]): Unit = {
    log.info(s"preRestart. Reason: $reason when handling message: $message")
    super.preRestart(reason, message)
  }



  override def postRestart(reason: Throwable): Unit = {
    log.info("postRestart")
    super.postRestart(reason)

  }


  def receive = {
    case ForceRestart =>
      throw new ForceRestartException
    case msg: AnyRef =>
      log.info(s"Received: '$msg'. Sending back")
      sender() ! msg
  }
}
