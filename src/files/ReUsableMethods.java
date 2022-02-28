package files;

import io.restassured.path.json.JsonPath;

public class ReUsableMethods {
	
	// Create JsonPath Object to parse string into JSON
	public static JsonPath stringToJson(String response) {
		var js = new JsonPath(response);
		return js;
	}

}
