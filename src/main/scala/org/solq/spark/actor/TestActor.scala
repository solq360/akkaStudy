package org.solq.spark.actor

import akka.actor.Actor

class TestActor extends Actor {
  def receive = {
    case "hello" => println("hello back at you")
    case _       => println("huh?")
  }
}