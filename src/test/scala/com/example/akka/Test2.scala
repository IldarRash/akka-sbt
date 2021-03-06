package com.example.akka

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import com.example.akka.testDriven.FilteringActor
import org.scalatest.{MustMatchers, WordSpecLike}

class Test2 extends TestKit(ActorSystem("testSystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {


  "A types of sending actors" must {
    "filter actor" in {
      import FilteringActor._

      val props = FilteringActor.props(testActor, 5)
      val filter = system.actorOf(props, "filter-1")

      filter ! Event(1)
      filter ! Event(2)
      filter ! Event(3)
      filter ! Event(4)
      filter ! Event(5)
      filter ! Event(6)

      val  events = receiveWhile() {
        case Event(id) if id <= 5 => id
      }

      events must be(List(1,2,3,4,5))
      expectMsg(Event(6))


    }
    "filter out particular messages using expectNoMsg" in {
      import FilteringActor._
      val props = FilteringActor.props(testActor, 5)
      val filter = system.actorOf(props, "filter-2")
      filter ! Event(1)
      filter ! Event(2)
      expectMsg(Event(1))
      expectMsg(Event(2))
      filter ! Event(1)
      expectNoMsg
      filter ! Event(3)
      expectMsg(Event(3))
      filter ! Event(1)
      expectNoMsg
      filter ! Event(4)
      filter ! Event(5)
      filter ! Event(5)
      expectMsg(Event(4))
      expectMsg(Event(5))
      expectNoMsg()
    }
  }
}
