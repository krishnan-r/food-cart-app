package appserver.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.logging.Logger;

public class HttpVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger(HttpVerticle.class.getName());
    JDBCClient client;

    private void failureHandler(RoutingContext handler) {
        HttpServerResponse response = handler.response();
        response.putHeader("content-type", "application/json; charset=utf-8");
        JsonObject result = new JsonObject();
        result.put("status", "error");
        if (handler.failure() == null)
            result.put("message", "Error 404 invalid endpoint");
        else
            result.put("message", handler.failure().toString());
        response.end(Json.encode(result));
    }

    protected void initializeDatabase() {
        JsonObject config = new JsonObject()
                .put("url", "jdbc:mysql://localhost:3306/foodcart")
                .put("driver_class", "com.mysql.cj.jdbc.Driver")
                .put("user", "root")
                .put("password", "example");
        client = JDBCClient.createShared(vertx, config);
    }

    private void loginHandler(RoutingContext context) {
        logger.info("loginHandler request received");
        JsonObject params = context.getBodyAsJson();
        if (!params.containsKey("username") || !params.containsKey("password")) {
            context.fail(1);
        } else {
            //Authentication

            client.getConnection(res1 -> {
                if (res1.succeeded()) {
                    SQLConnection conn = res1.result();
                    logger.info(" Database Connected! ");
                    String query = "select password from customer_details where employee_id=?";
                    String username = params.getValue("username").toString();
                    String password = params.getValue("password").toString();
                    JsonArray parameters = new JsonArray().add(username);

                    conn.queryWithParams(query, parameters, res -> {
                        if (res.succeeded()) {
                            JsonArray actualPassword = res.result().getResults().get(0);
                            if (password.equals(actualPassword.toString())) {
                                HttpServerResponse response = context.response();
                                response.putHeader("content-type", "application/json; charset=utf-8");
                                JsonObject js = new JsonObject();
                                js.put("logged_in", true);
                                js.put("token", "ABCD"); //TODO Use some token mechanism for authenticating subsequent requests..
                                String json = Json.encode(js);
                                response.end(json);

                            } else {
                                logger.info("Failed to authenticate");
                            }
                        }
                    });
                }

            });
        }
    }

    private void listItems(RoutingContext context) {
        // TODO Query Database Here
        logger.info("list items request received");
        HttpServerResponse response = context.response();
        response.putHeader("content-type", "application/json; charset=utf-8");
        JsonArray jsonResult = new JsonArray();
        JsonObject item = new JsonObject();

        // Sending some dummy data (TODO)
        item.put("name", "Veg Italian Pizza");
        item.put("id", "12345");
        item.put("image_url", "https://www.dominos.co.in/theme2/front/images/menu-images/my-vegpizza.jpg");
        item.put("rating", 3.5);
        item.put("description", "Tasty delicious italian pizza with cheese and toppings baked to perfection.");
        item.put("rating_count", 250);
        item.put("price", 250);


        for (int i = 0; i < 10; i++) {
            jsonResult.add(item);
        }

        String json = Json.encode(jsonResult);
        response.end(json);
    }


    private Future<Void> startHttpServer() {
        Future<Void> future = Future.future();

        HttpServer server = vertx.createHttpServer();
        initializeDatabase();
        Router router = Router.router(vertx);
        router.route().failureHandler(this::failureHandler);
        router.route().handler(BodyHandler.create());
        router.post("/api/login").handler(this::loginHandler);
        router.get("/api/list").handler(this::listItems);
        router.route().last().handler(this::failureHandler);


        server.requestHandler(router);
        server.listen(config().getInteger("port", 80), ar -> {
            if (ar.succeeded()) {
                logger.info("HttpVerticle initialization completed.");
                future.complete();
            } else {
                future.fail(ar.cause());
            }
        });
        return future;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        startHttpServer().setHandler(startFuture);
    }

    public static void main(String[] args) {
        logger.info("Starting FoodApp Vert.x Server");
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new HttpVerticle());
    }
}
