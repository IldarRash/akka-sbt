package com.example.akka

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{MustMatchers, WordSpec, WordSpecLike}

import scala.util.Random

class Test1 extends TestKit(ActorSystem("testSystem"))
                    with WordSpecLike
                    with MustMatchers
                    with StopSystemAfterAll {
    "A Silent Actor" must {
      "change state whe it recives a message. single thread" in {
        import SilentActor._

        val silentActor = TestActorRef[SilentActor]
        silentActor ! SilentMessage("whisper")
        silentActor.underlyingActor.state must(contain("whisper"))
      }
      "change state when it receives a message, multi thread" in {
        import SilentActor._

        val silentActor = system.actorOf(Props[SilentActor], "s3")
        silentActor ! SilentMessage("1")
        silentActor ! SilentMessage("2")
        silentActor ! GetState(testActor)
        expectMsg(Vector("1", "2"))
      }
    }

    "A Sending actor" must {
      "send a message to another actor" in {
        import SendingActor._
        val props = SendingActor.props(testActor)
        val sendingActor = system.actorOf(props, "sendingActor")

        val size = 1000
        val maxInclusive = 100000

        def randomEvents() = (0 until size).map{ _ =>
          Event(Random.nextInt(maxInclusive))
        }.toVector

        val unsorted = randomEvents()
        val sortEvents = SortEvents(unsorted)
        sendingActor ! sortEvents

        expectMsgPF() {
          case SortedEvents(events) =>
            events.size must be(size)
            unsorted.sortBy(_.id) must be(events)
        }
      }
    }
}
