package org.solq.spark.service

import akka.actor.ActorSystem
import akka.actor.Props

class RpcServer {
  val system = ActorSystem("helloSystem")
  system.actorOf(Props[org.solq.spark.actor.TestActor])
}