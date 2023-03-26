package mx.manma.examples.controllers;

import io.vertx.ext.web.RoutingContext;
import java.util.stream.Stream;

public class MainController {
  public static void hello(RoutingContext ctx) {
    var data = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)
      .filter(i -> i % 3 == 0)
      .reduce(Integer::sum);
    ctx.response().setStatusCode(200).end("""
      <h1>Hello World</h1>
      <p>This is a simple example</p>
      <br>
      <hr>
      """);
  }
}
