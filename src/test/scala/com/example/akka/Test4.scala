package com.example.akka

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.example.akka.testDriven.EchoActor
import org.scalatest.{MustMatchers, WordSpecLike}

class Test4 extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with ImplicitSender
  with StopSystemAfterAll{
  "Reply wuth the same message it recieves" in {
    val echo = system.actorOf(Props[EchoActor], "echo2")
    echo ! "some message"
    expectMsg("some message")
  }
}
