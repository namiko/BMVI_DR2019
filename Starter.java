package dr;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Starter {
	public static JSONObject lookup;
	public static JSONObject withAccidents;
	public static JSONObject finalEnriched;

	public static void main(String[] args) throws IOException {
		//letzterSchritt
		bufferAccidents();
		addSecurityIndex();
		
		//2. Schritt
//		bufferRadmesser();
//		readFileAccidents();
		
		//erster Schritt
//		readFileRadmesser();

	}
	
	private static void addSecurityIndex() {
		JSONArray savedFeatures = (JSONArray) withAccidents.get("features");
        JSONArray bufferedFeatures = new JSONArray();
        @SuppressWarnings("unchecked")
		Iterator<JSONObject> iter = savedFeatures.iterator();
        while (iter.hasNext()) {
        	JSONObject x = iter.next();
        	JSONObject props = (JSONObject) x.get("properties");
        	long acc = (long) props.get("ACCIDENT_RATING");
        	long dis = Long.parseLong((String) props.get("DISTANCE_RATING"));
        	long overAllRating = acc + dis;
        	props.put("OVERALL_RATING", overAllRating);
        	bufferedFeatures.add(x);
        };

        withAccidents.put("features", bufferedFeatures);
        
        writeFileWithoutUTF(withAccidents, "C:\\Users\\nherrma1\\Downloads\\datarun\\gesamt2.geo.json");
		
	}

	private static void bufferRadmesser() {
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader("C:\\Users\\nherrma1\\Downloads\\datarun\\detailnetz_ueberholvorgaenge_bearbeitet.geo.json"))    
        {
            //Read JSON file          
            lookup = (JSONObject) jsonParser.parse(reader);          
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void bufferEnriched() {
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader("C:\\Users\\nherrma1\\Downloads\\datarun\\detailnetz_ueberholvorgaenge_unfaelle_happy.geo.json"))    
        {
            //Read JSON file          
            finalEnriched = (JSONObject) jsonParser.parse(reader);          
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void bufferAccidents() {
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader("C:\\Users\\nherrma1\\Downloads\\datarun\\detailnetz_ueberholvorgaenge_unfaelle.geo.json"))    
        {
            //Read JSON file          
            withAccidents = (JSONObject) jsonParser.parse(reader);          
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void readFileAccidents() {
		JSONParser jsonParser = new JSONParser();
		try (BufferedReader reader  = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\nherrma1\\Downloads\\datarun\\accidents_streets_2017.geojson"), "UTF-8")))      
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            
            JSONObject file = (JSONObject) obj;
            JSONArray features = (JSONArray) file.get("features");
            @SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = features.iterator();
            while (iterator.hasNext()) {
            	JSONObject x = iterator.next();
            	JSONObject props = (JSONObject) x.get("properties");
            	String streetName = (String) props.get("name");
            	long accidentCount = (long) props.get("count");
            	enrich(streetName, accidentCount);
            };
            
            JSONArray savedFeatures = (JSONArray) lookup.get("features");
            JSONArray bufferedFeatures = new JSONArray();
            @SuppressWarnings("unchecked")
			Iterator<JSONObject> iter = savedFeatures.iterator();
            while (iter.hasNext()) {
            	JSONObject x = iter.next();
            	JSONObject props = (JSONObject) x.get("properties");
            	if(!props.containsKey("ACCIDENT_COUNT")) {
            		props.put("ACCIDENT_COUNT", 0);
            		props.put("ACCIDENT_RATING", 3);
            	}
            	x.put("properties", props);
            	bufferedFeatures.add(x);
            };

            lookup.put("features", bufferedFeatures);
            
            writeFile(lookup, "C:\\Users\\nherrma1\\Downloads\\datarun\\detailnetz_ueberholvorgaenge_unfaelle.geo.json");
            
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void enrich(String streetName, long accidentCount) {
		JSONArray features = (JSONArray) lookup.get("features");
		Iterator<JSONObject> iterator = features.iterator();
		JSONArray newFeatureNode = new JSONArray(); 
		 while (iterator.hasNext()) {
			 JSONObject x = iterator.next();
			 JSONObject props = (JSONObject) x.get("properties");
			 String originStreet = (String) props.get("STR_NAME");
			 if(originStreet.equals(streetName)) {
				 props.put("ACCIDENT_COUNT", accidentCount);
				 if(accidentCount<21) {
					 props.put("ACCIDENT_RATING", 3);
				 }else if(accidentCount<41) {
					 props.put("ACCIDENT_RATING", 2);
				 }else if(accidentCount<61) {
					 props.put("ACCIDENT_RATING", 1);
				 }else {
					 props.put("ACCIDENT_RATING", 0);
				 }
				 x.put("properties", props);
			 }
			 
			 newFeatureNode.add(x);
			 
		 }
            lookup.put("features", newFeatureNode);
//            System.out.println(output);
            
		
	}

	public static  void readFileRadmesser() {
		JSONParser jsonParser = new JSONParser();
		try (BufferedReader reader  = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\nherrma1\\Downloads\\datarun\\detailnetz_ueberholvorgaenge.geo.json"), "UTF-8")))      
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject features = (JSONObject) obj;
            JSONArray featureNode = (JSONArray) features.get("features");
            JSONArray newFeatureNode = new JSONArray();
            @SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = featureNode.iterator();
            while (iterator.hasNext()) {
            	JSONObject x = iterator.next();
            	Object y = x.get("stats");
            	Object z = ((JSONObject) y).get("mean_dist_left");
            	String ztext = z.toString();
            	double zVal = Double.parseDouble(ztext);
            	Object eventCount = ((JSONObject) y).get("event_count");
            	long eC = (long) eventCount;
            	String rating = "danger";
            	if(eC == 0) {
            		rating = ("3");
            	}else {
            		if(zVal > 200) {
            			rating = ("3");
            		}else if(zVal > 150) {
            			rating = ("2");
            		}else if(zVal > 50) {
            			rating = ("1");
            		}
            		else {
            			rating = ("0");
            		}
            	}
            	JSONObject props = (JSONObject) x.get("properties");
            	props.put("DISTANCE_RATING", rating);
            	props.put("AVERAGE", zVal);
            	x.put("properties", props);
            	x.remove("stats");
            	
            	newFeatureNode.add(x);
           
            }
            JSONObject output = new JSONObject();
            output.put("type", "FeatureCollection");
            output.put("name", "Radmesser Daten");
            output.put("features", newFeatureNode);
//            System.out.println(output);
            writeFile(output, "C:\\Users\\nherrma1\\Downloads\\datarun\\detailnetz_ueberholvorgaenge_bearbeitet.geo.json");
             
            //Iterate over employee array
//            employeeList.forEach( emp -> parseEmployeeObject( (JSONObject) emp ) );
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
	}
	
	 public static void writeFile(JSONObject obj, String fileName) {

		 

//	        try (FileWriter file = new FileWriter(fileName)) {
	        try (Writer file = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
	            file.write(obj.toJSONString());
	            file.flush();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }

//	        System.out.print(obj);

	    }
	 
	 public static void writeFileWithoutUTF(JSONObject obj, String fileName) {

		 

	        try (FileWriter file = new FileWriter(fileName)) {
//	        try (Writer file = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
	            file.write(obj.toJSONString());
	            file.flush();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }

//	        System.out.print(obj);

	    }
	
}
