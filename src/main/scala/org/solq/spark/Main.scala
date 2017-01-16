package org.solq.spark


object Main extends App {
  new SparkServer;
  new RpcServer;
  new HttpServer().start();
}

