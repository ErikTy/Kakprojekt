package krusty;

import static spark.Spark.*;

public class RESTServer {
	public static int PORT = 8888; 
	
	private Database db;
	
	public void startServer() {
		db = new Database();
		db.connect();

		port(PORT);
		
		enableCORS();
		
		initRoutes();
	}

	private void initRoutes() {
		get("/customers", (req, res) -> db.getCustomers(req, res));
		get("/raw-materials", (req, res) -> db.getRawMaterials(req, res));
		get("/cookies", (req, res) -> db.getCookies(req, res));
		get("/recipes", (req, res) -> db.getRecipes(req, res));
		get("/pallets", (req, res) -> db.getPallets(req, res));
		
		post("/reset", (req, res) -> db.reset(req, res));
		post("/pallets", (req, res) -> db.createPallet(req, res));
	}
	
	public void stopServer() {
		stop();
	}
	
	/**
	 * Setup CORS, see:
	 * - https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
	 * - http://sparkjava.com/tutorials/cors
	 */
	private void enableCORS() {
	    options("/*", (request, response) -> {
	        String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
	        if (accessControlRequestHeaders != null) {
	            response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
	        }
	        String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
	        if (accessControlRequestMethod != null) {
	            response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
	        }
	        return "OK";
	    });

	    before((request, response) -> {
	        response.header("Access-Control-Allow-Origin", "*");
	        response.header("Access-Control-Allow-Headers", "Content-Type, Accept");
	        response.type("application/json");
	    });
	}

	public static void main(String[] args) throws InterruptedException {
		new RESTServer().startServer();
	}
}