package com.example.akka

import akka.actor.{ActorSystem, Props, UnhandledMessage}
import akka.testkit.{CallingThreadDispatcher, EventFilter, TestKit}
import com.example.akka.Test3._
import com.example.akka.testDriven.{Greeter, Greeter02, Greeting}
import com.typesafe.config.ConfigFactory
import org.scalatest.{MustMatchers, WordSpecLike}

class Test3 extends TestKit(testSystem)
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {

  "The Greeter" must {
    "say Hello World!" in {
      val dispatchId = CallingThreadDispatcher.Id
      val props = Props[Greeter].withDispatcher(dispatchId)
      val greeter = system.actorOf(props)
      EventFilter.info(message = "Hello World!",
        occurrences = 1).intercept {
        greeter ! Greeting("World")
      }
    }
    "say Hello World! when a Greeting(World) is sent to it" in {
      val props = Greeter02.props(Some(testActor))
      val greeter = system.actorOf(props, "greeter02-1")
      greeter ! Greeting("World")
      expectMsg("Hello World!")
    }
    "say something else and see what happens" in {
      val props = Greeter02.props(Some(testActor))
      val greeter = system.actorOf(props, "greeter02-2")
      system.eventStream.subscribe(testActor, classOf[UnhandledMessage])
      greeter ! "World"
      expectMsg(UnhandledMessage("World", system.deadLetters, greeter))
    }
  }

}


object Test3 {
  val testSystem = {
    val config = ConfigFactory.parseString(
      """
      akka.loggers = [akka.testkit.TestEventListener]
      """)
    ActorSystem("testsystem", config)
  }
}
