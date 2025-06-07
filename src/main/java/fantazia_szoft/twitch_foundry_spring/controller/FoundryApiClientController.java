package fantazia_szoft.twitch_foundry_spring.controller;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

public class FoundryApiClientController {
    private final HttpClient httpClient;
    private final String baseUrl;
    private final String apiKey;
    private final String clientId;

    public FoundryApiClientController( String apiKey) throws Exception {
        this.httpClient = HttpClient.newHttpClient();
        this.baseUrl = "https://foundryvtt-rest-api-relay.fly.dev";
        this.apiKey = apiKey;
		this.clientId = getClienId();
    }
    
    public synchronized String getClienId() throws Exception {

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/clients"))
          .header("x-api-key", apiKey)
          .header("Content-Type", "application/json")
          .GET()
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      System.out.println("Response: "+response.body());
   // Parse the response body
      JSONObject json = new JSONObject(response.body());
      JSONArray clients = json.getJSONArray("clients");

      if (clients.length() > 0) {
          JSONObject firstClient = clients.getJSONObject(0);
          String clientId = firstClient.getString("id");
          System.out.println("ClientId: " + clientId);
          return clientId;
      } else {
          throw new RuntimeException("No clients found!");
      }
  }

    public synchronized void rollDice(String formula, String viewerName) throws Exception {
         JSONObject payload = new JSONObject();
         payload.put("formula", formula);
         payload.put("flavor", viewerName);
         payload.put("target", "");
         payload.put("speaker", "");
         payload.put("itemUuid", "");
         payload.put("createChatMessage", true);
         payload.put("whisper", new org.json.JSONArray());
         
        System.out.println("json: "+payload);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/roll?clientId=" + clientId))
            .header("x-api-key", apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println("Dice roll result: " + response.body());
    }
    
    public String getPlayerIdByName(String playerName) {
    	

		String encodedName = URLEncoder.encode(playerName, StandardCharsets.UTF_8);
//		String encodedFilter = URLEncoder.encode("name:" + playerName + ",documentType:actor", StandardCharsets.UTF_8);

    	
    	 HttpRequest request = HttpRequest.newBuilder()
    	            .uri(URI.create(baseUrl + "/search?clientId=" + clientId+"&query="+playerName+"&filter=name:"+playerName+",documentType:actor"))
    	            .header("x-api-key", apiKey)
    	            .header("Content-Type", "application/json")
    	            .GET()
    	            .build();
    	 
    	 HttpResponse<String> response;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			 System.out.println("Search result: " + response.body());
			 // Parse the JSON response
		        JSONObject json = new JSONObject(response.body());
		        JSONArray results = json.getJSONArray("results");
		        if (results.length() > 0) {
		            JSONObject firstResult = results.getJSONObject(0);
		            return firstResult.getString("uuid"); // or getString("uuid")
		        } else {
		            return null; // No results found
		        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
        
    }
    
    public JSONObject getPlayerStastByUUid(String playerUuid) {
   	 HttpRequest request = HttpRequest.newBuilder()
   	            .uri(URI.create(baseUrl + "/get?clientId=" + clientId+"&uuid="+playerUuid))
   	            .header("x-api-key", apiKey)
   	            .header("Content-Type", "application/json")
   	            .GET()
   	            .build();
   	 
   	 HttpResponse<String> response;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			 System.out.println("Search result: " + response.body());
			 // Parse the JSON response
		        JSONObject json = new JSONObject(response.body());
		       
	            return json; 
		        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
       
   }
    
    public synchronized Integer getAc(String playerUuid) throws Exception {
       JSONObject payload = new JSONObject();
       payload.put("script", "const uuid = '"+playerUuid+"';"
       		+ " const document = await fromUuid(uuid);if (!document) return null;"
       		+ "const actor = document instanceof Actor ? document : document.actor;if (!actor) return null;const ac = actor.system.attributes.ac;return ac?.value ?? null;");
       
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(baseUrl + "/execute-js?clientId=" + clientId))
          .header("x-api-key", apiKey)
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      JSONObject json = new JSONObject(response.body());
      Integer ac = json.getInt("result");
     
      return ac;
  }
}
