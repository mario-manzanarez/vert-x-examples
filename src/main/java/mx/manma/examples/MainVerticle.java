package mx.manma.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.TimeoutStream;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.ext.web.Router;
import mx.manma.examples.controllers.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpRequest;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOG.info("Starting verticle");
    vertx.createHttpServer()
      .requestHandler(this::handler)
      .listen(config().getInteger("port", 8080))
      .onFailure(fail -> LOG.error("Failed to start verticle", fail))
      .onSuccess(server -> LOG.info("Started verticle"));
  }

  private void handler(HttpServerRequest request) {
    if ("/".equals(request.path())) {
      request.response().sendFile("index.html");
    } else if ("/sse".equals(request.path())) {
      sse(request);
    } else
      request.response().setStatusCode(404).end();
  }

  private void sse(HttpServerRequest request) {
    HttpServerResponse response = request.response();
    response
      .putHeader("Content-Type", "text/event-stream")
      .putHeader("Cache-Control", "no-cache")
      .setChunked(true);

    MessageConsumer<JsonObject> consumer =
      vertx
        .eventBus()
        .consumer("sensor.updates");
    consumer.handler(msg -> {
      response.write("event: update\n");
      response.write("data:  " + msg.body().encode() + "\n\n");
    });
    TimeoutStream ticks = vertx.periodicStream(1_000);
    ticks.handler(id -> {
      vertx.eventBus().<JsonObject>request("sensor.averages", "", reply -> {
        if (reply.succeeded()) {
          response.write("event: average\n");
          response.write("data: " + reply.result().body().encode() + "\n\n");
        }
      });
    });
    response.endHandler(v -> {
      consumer.unregister();
      ticks.cancel();
    });
  }
}
