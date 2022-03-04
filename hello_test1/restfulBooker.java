package hello_test1;

import org.json.simple.JSONObject;
import org.testng.annotations.Test;
import com.shaft.api.RestActions;
import com.shaft.api.RestActions.RequestType;
import com.shaft.driver.DriverFactory;
import com.shaft.validation.Assertions;
import com.shaft.validation.Verifications;

import io.restassured.http.ContentType;
import io.restassured.response.Response;



public class restfulBooker {
  @Test
  public void getbookingIDs() {
	  RestActions apiObject = DriverFactory.getAPIDriver("https://restful-booker.herokuapp.com/");
	  
	  apiObject.buildNewRequest("booking", RequestType.GET).performRequest();
	  
  }
  
  @Test
  public void getbooking() {
	  RestActions apiObject = DriverFactory.getAPIDriver("https://restful-booker.herokuapp.com/");
	  
	  apiObject.buildNewRequest("booking/" + "5", RequestType.GET).performRequest()
	  ;
  
  
}
  
  
  @SuppressWarnings({ "unchecked", "deprecation", "removal" })
@Test
  public void createbooking() {
	  RestActions apiObject = DriverFactory.getAPIDriver("https://restful-booker.herokuapp.com/");
	  JSONObject createbookingbody =  new JSONObject();
	    createbookingbody.put("firstname", "ALI");
	    createbookingbody.put("lastname", "ZEIDAN");
	    createbookingbody.put("totalprice", 2000 );
	    createbookingbody.put("depositpaid", true);
	    JSONObject bookingdates =  new JSONObject();
	    bookingdates.put("checkin","2022-01-01");
	    bookingdates.put("checkout","2022-03-03");
	    createbookingbody.put("bookingdates", bookingdates);
	    createbookingbody.put("additionalneeds", "cocacola");
	     
	  Response createbookingRes =  apiObject.buildNewRequest("booking", RequestType.POST)
	  .setContentType(ContentType.JSON)
	  .setRequestBody(createbookingbody)
	  .performRequest();
	  
	 String BookingId=  RestActions.getResponseJSONValue(createbookingRes, "bookingid");
	  
	   
	 Response getbookingRes = apiObject.buildNewRequest("booking/" + BookingId , RequestType.GET).performRequest();
	  String fristname =RestActions.getResponseJSONValue(getbookingRes, "firstname");
	  String lastname =RestActions.getResponseJSONValue(getbookingRes, "lastname");
	  String checkin =RestActions.getResponseJSONValue(getbookingRes, "bookingdates.checkin");
	  String checkout =RestActions.getResponseJSONValue(getbookingRes, "bookingdates.checkout");
	 
	  Verifications.verifyEquals("ALI", fristname);
	  Verifications.verifyEquals("ZEIDAN", lastname); 
	  Verifications.verifyEquals("2022-01-01", checkin);
	  Verifications.verifyEquals("2022-03-03", checkout);
	Assertions.assertJSONFileContent(getbookingRes, 
			System.getProperty("jsonFolderPath")+ "Booking.json");
		  


  }

  @SuppressWarnings("unchecked")
  @Test(dependsOnMethods = {"createbooking"})
public void deletebooking() {
	  RestActions apiObject = DriverFactory.getAPIDriver("https://restful-booker.herokuapp.com/");
	  
	 JSONObject authantication =  new JSONObject();
	  authantication.put("username","admin");
	  authantication.put("password","password123");
	  
	 Response createtoken = apiObject.buildNewRequest("auth", RequestType.POST)
	  .setContentType(ContentType.JSON)
	  .setRequestBody(apiObject)
	  .setRequestBody(authantication)
	  .performRequest()
	  ; 
	 String token = RestActions.getResponseJSONValue(createtoken, "token");
	 Response getbookingId = apiObject.buildNewRequest("booking", RequestType.GET)
	  .setUrlArguments("firstname=ALI&lastname=ZEIDAN")
	  .performRequest();
	 
	 String bookingId = RestActions.getResponseJSONValue(getbookingId,"bookingid[0]");
	  // Delete  Request 
	  apiObject.buildNewRequest("booking/"+ bookingId, RequestType.DELETE)
	  .setTargetStatusCode(201)
	  .setContentType(ContentType.JSON)
	  .addHeader("Cookie", "token="+ token)
	  .performRequest()
	  ;
  
  
  }
}