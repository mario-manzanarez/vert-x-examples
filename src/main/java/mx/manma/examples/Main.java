package mx.manma.examples;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import mx.manma.examples.verticles.HeatSensor;
import mx.manma.examples.verticles.Listener;
import mx.manma.examples.verticles.SensorData;

public class Main {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(HeatSensor.class.getCanonicalName(), new DeploymentOptions().setInstances(4));
    vertx.deployVerticle(Listener.class.getCanonicalName());
    vertx.deployVerticle(SensorData.class.getCanonicalName());
    vertx.deployVerticle(MainVerticle.class.getCanonicalName());

  }
}
