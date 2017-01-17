package org.solq.spark

import org.solq.spark.service.RpcServer

import org.solq.spark.service.HttpServer

import org.solq.spark.service.SparkServer


object Main extends App {
  new RpcServer;
  new HttpServer().start();
}

