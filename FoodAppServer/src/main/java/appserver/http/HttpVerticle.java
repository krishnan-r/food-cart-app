package appserver.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
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
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger(HttpVerticle.class.getName());
    JDBCClient client;

    protected void initializeDatabase() {
        JsonObject config = new JsonObject()
                .put("url", "jdbc:mysql://localhost:3306/foodcart")
                .put("driver_class", "com.mysql.cj.jdbc.Driver")
                .put("user", "root")
                .put("password","");
        client = JDBCClient.createShared(vertx, config);
    }
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

    private static String toStringInHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }

    private static byte[] fromHexStringToBytes(String hex) throws NoSuchAlgorithmException
    {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    private String hashToCheck(String password,byte[] salt) throws  InvalidKeySpecException,NoSuchAlgorithmException {
        KeySpec specHashCheck = new PBEKeySpec(password.toCharArray(),salt, 65536,128);
        SecretKeyFactory factoryHashCheck = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hashCheck = factoryHashCheck.generateSecret(specHashCheck).getEncoded();
        return toStringInHex(hashCheck);
    }

    private ArrayList<String> hashToStore(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        ArrayList<String>  passwordSalt = new ArrayList<String>();
        passwordSalt.add(toStringInHex(hash));
        passwordSalt.add(toStringInHex(salt));
        return passwordSalt;
    }

    private void insertIntoDatabase(String name, String employee_id, String password,String mobile,String email,boolean verified) {

        client.getConnection(res1 -> {
            if (res1.succeeded()) {
                SQLConnection conn = res1.result();
                try {
                    ArrayList<String> passHash = hashToStore(password);
                    String hashedPassword = passHash.get(0);
                    String salt = passHash.get(1);
                    JsonArray parameters = new JsonArray()
                            .add(name)
                            .add(employee_id)
                            .add(hashedPassword)
                            .add(Integer.parseInt(mobile))
                            .add(email).add(verified).add(salt);
                    String query = "insert into  customer_details values(?,?,?,?,?,?,?) ";
                    conn.updateWithParams(query,parameters,res->{
                        if(res.succeeded()){
                            logger.log(Level.INFO,"User Succesfully signed up!");
                        }
                        else{
                            logger.log(Level.INFO,"Unable to register user : "+res.cause());
                        }
                    });

                }
                catch(Exception e){
                    logger.log(Level.INFO,"Couldn't connect to Database: "+e.getMessage());
                }


            }
        });
    }

    private void loginHandler(RoutingContext context) {
        logger.info("loginHandler request received");
        JsonObject params = context.getBodyAsJson();

        HttpServerResponse response = context.response();
        response.putHeader("content-type", "application/json; charset=utf-8");
        if (!params.containsKey("username") || !params.containsKey("password")) {
            response.end("Credentials not filled");
            context.fail(1);
        }
        else {
            String username = params.getValue("username").toString();
            String password = params.getValue("password").toString();
            //Authentication
          client.getConnection(res1 -> {
                if (res1.succeeded()) {
                        SQLConnection conn = res1.result();
                        logger.log(Level.INFO,"Successfully connected to the database");
                        String query = "select password,salt from customer_details where employee_id=?";
                        JsonArray parameters = new JsonArray().add(username);
                        JsonObject js = new JsonObject();
                        conn.queryWithParams(query,parameters,res->{
                            if(res.succeeded()){
                                JsonArray actualPassword = res.result().getResults().get(0);
                               String actualPasswordHash = actualPassword.getString(0);
                               String salt = actualPassword.getString(1);
                                 try {
                                    String givenHash = hashToCheck(password, fromHexStringToBytes(salt));
                                    if(actualPasswordHash.equals(givenHash)){
                                        logger.log(Level.INFO,"Authenticated !");
                                        js.put("message","Authenticated");
                                        js.put("logged_in", true);
                                        js.put("token", "ABCD");

                                        String json = Json.encode(js);
                                        response.end(json);
                                    }
                                    else{
                                        js.put("logged_in",false);
                                        js.put("message","Invalid Username or Password");
                                        js.put("token","null");
                                        String json = Json.encode(js);
                                        response.end(json);
                                       logger.log(Level.INFO,"Failed to authenticate");
                                    }
                                }
                                catch(Exception e){
                                    logger.log(Level.INFO,e.getMessage());
                                    js.put("message","Invalid Username or password");
                                    js.put("logged_in",false);
                                    js.put("token","null");
                                    String json = Json.encode(js);
                                    response.end(json);
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
        router.route("/api/login").handler(BodyHandler.create());
        router.post("/api/login").handler(this::loginHandler);
        router.get("/api/list").handler(this::listItems);
        router.route().last().handler(this::failureHandler);
//    insertIntoDatabase("User1","E1","user1Password","123456789","user1Email@gmail.com",false);

        server.requestHandler(router);
        server.listen(config().getInteger("port", 86), ar -> {
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
