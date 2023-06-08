package inforAutomator;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerationException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

//import org.codehaus.jackson.annotate.JsonIgnoreProperties;
//import org.codehaus.jackson.map.JsonMappingException;
//import org.codehaus.jackson.map.ObjectMapper;


public class JavaJSONMapper {
	final static Logger log = Logger.getLogger(JavaJSONMapper.class);

	public static String JavaToJSON(Object object)
	{
		ObjectMapper mapper = new ObjectMapper();
		String json;
		try
		{
			json = mapper.writeValueAsString(object);
		}
		catch(Exception e) 
		{
			log.error("Error jasonizing the object (" + e.getMessage() + ")", e);
			e.printStackTrace();
			return "{}";
		}
		return json;
	}
	
	public static JsonObject StringToJSON(String jsonString)
	{
		return StringToJSON(jsonString, false); 			
	}
	
	public static JsonArray StringToJSONArray(String jsonString, boolean removeNonAsciiChar)
	{
		if (removeNonAsciiChar)
		{
			log.trace("Required to replace non printable chars from '" + jsonString + "'");
			jsonString = jsonString.replaceAll("[^{\\x20-\\x7E}]", "");
		}
		log.trace("Converting '" + jsonString + "' to JSONObject");
		JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
		JsonArray jObj = jsonReader.readArray();
		jsonReader.close();
		return jObj; 			
	}

	public static JsonObject StringToJSON(String jsonString, boolean removeNonAsciiChar)
	{
		if (removeNonAsciiChar)
		{
			log.trace("Required to replace non printable chars from '" + jsonString + "'");
			jsonString = jsonString.replaceAll("[^{\\x20-\\x7E}]", "");
		}
		log.trace("Converting '" + jsonString + "' to JSONObject");
		JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
		JsonObject jObj = jsonReader.readObject();
		jsonReader.close();
		return jObj; 			
	}
	
	public static Object JSONToJava(JsonObject jsonIn, Class<?> objClass)
	{
		Object object = null;
		ObjectMapper mapper = new ObjectMapper();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		mapper.setDateFormat(df);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try
		{
			//	    	  PrintWriter out = new PrintWriter("./tmpjson");
			//	    	  out.println(jsonIn.toString());
			//	    	  out.close();
			//		         object =  mapper.readValue(new File("./tmpjson"), objectClass);
			object =  mapper.readValue(jsonIn.toString(), objClass);
		}
		catch (JsonGenerationException e)
		{
			log.error("Exception " + e.getMessage(), e);
			e.printStackTrace();
		} catch (IOException e)
		{
			log.error("Exception " + e.getMessage(), e);
			e.printStackTrace();
		}
		return(object);
	}
	
	public static ArrayList<?> JSONArrayToJava(JsonArray jsonIn, Class<?> objClass)
	{
		ArrayList<Object> objectArray = new ArrayList<Object>();
		for (JsonValue object : jsonIn) {
			objectArray.add(JSONToJava((JsonObject) object, objClass));
		}
		return objectArray;
		
	}

	public static Object[] JSONArrayToJavaArray(JsonArray jsonIn, Class<?> objClass)
	{
		Object[] objectArray = new Object[jsonIn.size()];
		for (int i = 0; i < jsonIn.size(); i++) {
			objectArray[i] = JSONToJava((JsonObject) jsonIn.get(i), objClass);
		}
		return objectArray;
		
	}
}
