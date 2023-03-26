package mx.manma.examples.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.stream.Collectors;

public class SensorData extends AbstractVerticle {
  private final HashMap<String, Double> lastValues = new HashMap<>();
  private static final Logger logger = LoggerFactory.getLogger(SensorData.class);

  @Override
  public void start() throws Exception {
    logger.info("Starting sensor data verticle");
    EventBus bus = vertx.eventBus();
    bus.consumer("sensor.updates", this::update);
    bus.consumer("sensor.averages", this::average);
  }

  private void update(Message<JsonObject> message) {
    JsonObject json = message.body();
    lastValues.put(json.getString("id"), json.getDouble("temp"));
  }

  private void average(Message<JsonObject> message) {
    double avg = lastValues.values().stream().
      collect(Collectors.averagingDouble(Double::doubleValue));
    JsonObject json = new JsonObject().put("average", avg);
    message.reply(json);
  }
}
