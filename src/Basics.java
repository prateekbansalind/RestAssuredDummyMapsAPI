import files.PayLoad;
import io.restassured.RestAssured;
import files.ReUsableMethods;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import pojo.AddPlace;
import pojo.LocationClass;
import pojo.TypesClass;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Basics {

	public static void main(String[] args) throws IOException {

		// Request Body
		var locationObj = new LocationClass(-38.383494, 33.427362);
		List<String> typeObj = new ArrayList<String>();
		typeObj.add("Shoe Park");
		typeObj.add("Children Park");
		var addPlaceObj = new AddPlace(locationObj, 50, "Frontline House", "(+91) 983 893 3937", "34/1, Riccarton Street, Riccarton"
				, typeObj, "http://google.com", "French-IN" );
		
		// validate if Add Place API is working as expected. 
		
				// Three principals on which Rest Assured test cases has been written.
				// given - all input details
				// when - submit the API - HTTP method, resource
				// then - validate the response
				
				// Before we go down to work on above principals, first we need to 
				// access/hit to/on the baseURI. 
		
		RestAssured.baseURI = "https://rahulshettyacademy.com";
		
		// Response can only be extracted as String from the server.
		String response = given().log().all().queryParam("key", "qaclick123").header("Content-Type","application/json")
		//.body(new String(Files.readAllBytes(Paths.get("C:\\Users\\prate\\IdeaProjects\\RestAssuredDummyMapsAPI\\src\\files\\AddPlace.json"))))
				.body(addPlaceObj)
		.when().post("/maps/api/place/add/json")
		.then()
		.assertThat().statusCode(200)
		.body("scope", equalTo("APP"))
		.header("Server", "Apache/2.4.18 (Ubuntu)")
		.extract().response().asString();
		
		// Response in string form
		System.out.println("Response from server after post: \n" + response);
		
		// Parsing JSON : need to use Java class JsonPath
		
		var postResponseJSON = ReUsableMethods.stringToJson(response);
		// "js" contains JSON response body
		String placeId = postResponseJSON.getString("place_id");
		System.out.println("The place id is : " + placeId);
		
		
		// Update Place with new address
		// given() - query/path parameters, headers, body (Json)
		// when() - HTTP method, resource
		// then() - assert status code, body response
		String newAddress = "134 Barrington Palace, Lincoln";
		
		given().log().all()
		.queryParam("key", "qaclick123").queryParam("place_id", placeId)
		.header("Content-Type", "application/json")
		.body("{\r\n"
				+ "\"place_id\":\""+placeId+"\",\r\n"
				+ "\"address\":\""+newAddress+"\",\r\n"
				+ "\"key\":\"qaclick123\"\r\n"
				+ "}")
		.when().put("/maps/api/place/update/json")
		.then()
		.log().all()
		.assertThat().statusCode(200).body("msg", equalTo("Address successfully updated"));
		
		// Get Place to validate if New Address is present in the response.
		String getResponse = given().log().all()
		.queryParam("key", "qaclick123").queryParam("place_id", placeId)
		.when().get("/maps/api/place/get/json")
		.then()
		.log().all()
		.assertThat().statusCode(200)
		.extract().response().asString();
		
		var getResponseJSON = ReUsableMethods.stringToJson(getResponse);
		String actualAddress = getResponseJSON.getString("address");
		System.out.println(actualAddress);
		System.out.println(newAddress);
		
		// TestNG Assertion method
		Assert.assertEquals(actualAddress, newAddress);
		
		// Delete the Place
		given().log().all()
		.queryParam("key", "qaclick123")
		.header("Content-Type", "application/json")
		.body("{\r\n"
				+ "    \"place_id\":\""+placeId+"\"\r\n"
				+ "}")
		.when().post("/maps/api/place/delete/json")
		.then()
		.log().all()
		.assertThat().statusCode(200).body("status", equalTo("OK"));	
	}
}
