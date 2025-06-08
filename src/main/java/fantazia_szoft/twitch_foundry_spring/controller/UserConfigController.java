package fantazia_szoft.twitch_foundry_spring.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fantazia_szoft.twitch_foundry_spring.dto.PlayerStatDTO;
import fantazia_szoft.twitch_foundry_spring.dto.RollDTO;
import fantazia_szoft.twitch_foundry_spring.dto.SheethtmlDTO;
import fantazia_szoft.twitch_foundry_spring.dto.UserConfigDTO;
import fantazia_szoft.twitch_foundry_spring.model.Player;
import fantazia_szoft.twitch_foundry_spring.model.Redemptions;
import fantazia_szoft.twitch_foundry_spring.model.UserConfig;
import fantazia_szoft.twitch_foundry_spring.repository.RedemptionsRepository;
import fantazia_szoft.twitch_foundry_spring.repository.UserConfigRepository;
import fantazia_szoft.twitch_foundry_spring.service.TwitchService;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://127.0.0.1:8082", "https://zed4vietrc5tn65xtn8eg5y01hr14k.ext-twitch.tv"})
public class UserConfigController {

    private final UserConfigRepository repository;
    private final RedemptionsRepository redemptionsRepository;
    private final TwitchService twitchService;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private String client_id = ""; 
    
    public UserConfigController(UserConfigRepository repository, TwitchService twitchService, RedemptionsRepository redemptionsRepository) {
        this.twitchService = twitchService;
        this.repository = repository;
        this.redemptionsRepository = redemptionsRepository;
    }
    
    @PostMapping("/config")
    public ResponseEntity registerConfig( @RequestBody UserConfigDTO dto,
            @RequestHeader("Authorization") String authHeader) {
//        // Delete old config if exists
    System.out.println("Érkező adatok: "+ dto.toString());
    	 String token = authHeader.replace("Bearer ", "");
    	 String twitchUserId = twitchService.getTwitchUserIdFromToken(token);
	    String twitchChanel = twitchService.getTwitchChannelFromToken(token); 
	    
	    repository.findByTwitchuserId(twitchUserId)
        .ifPresent(repository::delete);
        // Convert DTO to Entity and enrich
        UserConfig config = new UserConfig();
        //config.setTwitchToken(token);
        config.setTwitchuserId(twitchUserId);
        config.setTwitchChannelId(twitchChanel);
        config.setFoundryApiKey(dto.getFoundryApiKey());
        config.setPlayer1Name(dto.getPlayer1Name());
        config.setPlayer2Name(dto.getPlayer2Name());
        
        config.setPlayer3Name(dto.getPlayer3Name());
        config.setPlayer4Name(dto.getPlayer4Name());
        config.setPlayer5Name(dto.getPlayer5Name());
        config.setPlayer6Name(dto.getPlayer6Name());
        config.setTwitchToken(dto.getTwitchToken());
        System.out.println(config.toString());
        repository.save(config);
        
        try {
            subscribe(config.getTwitchuserId()); // <-- this line is critical
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to subscribe: " + e.getMessage());
        }

        return ResponseEntity.ok("Configuration saved and subscribed to Twitch redemptions.");
    }
    
    @GetMapping("/login")
    public void redirectToTwitch(HttpServletResponse response) throws IOException {
        String clientId = "d32fh16qw5qfk2giw6if0eg5tal34t";
        String redirectUri = "https://grim-garnet-benji1012-c136b1f8.koyeb.app/api/callback";
        String scope = "channel:read:redemptions";
        String url = "https://id.twitch.tv/oauth2/authorize?client_id=" + clientId +
                     "&redirect_uri=" + redirectUri +
                     "&response_type=code&scope=" + scope;
        response.sendRedirect(url);
    }
    
    @GetMapping("/user")
    public ResponseEntity<String> getUser(@RequestParam String accessToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.twitch.tv/helix/users"))
            .header("Authorization", "Bearer " + accessToken)
            .header("Client-Id", "d32fh16qw5qfk2giw6if0eg5tal34t")
            .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return ResponseEntity.ok(response.body());
    }
    
    @GetMapping("/callback")
    public ResponseEntity<String> getToken(@RequestParam String code) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        String clientId = "d32fh16qw5qfk2giw6if0eg5tal34t";
        String clientSecret = "eg111oi7qtrwnkbvzxmaizgl9l01fq";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://id.twitch.tv/oauth2/token" +
                    "?client_id=" + clientId +
                    "&client_secret=" + clientSecret +
                    "&code=" + code +
                    "&grant_type=authorization_code" +
                    "&redirect_uri=https://grim-garnet-benji1012-c136b1f8.koyeb.app/api/callback"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .header("Content-Type", "application/x-www-form-urlencoded")
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
     // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.body());
        String accessToken = jsonNode.get("access_token").asText();

        return ResponseEntity.ok("copy this into the Access Token field in the configuration: " +accessToken);
    }
    
    @GetMapping("/refresh")
    public ResponseEntity<?> refreshPlayerStats( @RequestHeader("Authorization") String authHeader) {
        try {
        	 String token = authHeader.replace("Bearer ", "");
            // Extract channel from JWT token
            String twitchChannel = twitchService.getTwitchChannelFromToken(token);

            // Find config for that channel
            Optional<UserConfig> configOpt = repository.findBytwitchChannelId(twitchChannel);
            if (configOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body("No config found for this Twitch channel.");
            }
            
            UserConfig config = configOpt.get();
            
            FoundryApiClientController foundryClient = new FoundryApiClientController(config.getFoundryApiKey());


            List<String> playerNames = List.of(
                    config.getPlayer1Name(),
                    config.getPlayer2Name(),
                    config.getPlayer3Name(),
                    config.getPlayer4Name(),
                    config.getPlayer5Name(),
                    config.getPlayer6Name()
                );

                List<PlayerStatDTO> playerStats = new ArrayList<>();

                for (String name : playerNames) {
                    if (name != null && !name.isBlank()) {
                        Player player = new Player(name, foundryClient);
                        playerStats.add(new PlayerStatDTO(player));
                        setClient_id(player.getClient_id());
                    }
                }

                return ResponseEntity.ok(playerStats);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body("Error fetching player stats: " + e.getMessage());
            }
    }
    
//    @PostMapping("/subscribe")
//    public ResponseEntity<String> subscribe(@RequestParam String userId, @RequestParam String accessToken) throws IOException, InterruptedException {
//        String callback = "https://grim-garnet-benji1012-c136b1f8.koyeb.app/twitch/webhook";
//        String secret = "eg111oi7qtrwnkbvzxmaizgl9l01fq";
//        System.out.println("we are in the subsribe function");
//        String body = """
//            {
//              "type": "channel.channel_points_custom_reward_redemption.add",
//              "version": "1",
//              "condition": {
//                "broadcaster_user_id": "%s"
//              },
//              "transport": {
//                "method": "webhook",
//                "callback": "%s",
//                "secret": "%s"
//              }
//            }
//            """.formatted(userId, callback, secret);
//
//        HttpRequest request = HttpRequest.newBuilder()
//            .uri(URI.create("https://api.twitch.tv/helix/eventsub/subscriptions"))
//            .header("Client-Id", "d32fh16qw5qfk2giw6if0eg5tal34t")
//            .header("Authorization", "Bearer " + accessToken)
//            .header("Content-Type", "application/json")
//            .POST(HttpRequest.BodyPublishers.ofString(body))
//            .build();
//        
//    	System.out.println("requesgt: "+ request.toString());
//        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println("requesgt: "+ response.body());
//        return ResponseEntity.ok(response.body());
//    }
    
    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestParam String userId) throws IOException, InterruptedException {
        System.out.println("🔔 Starting EventSub subscription...");

        String clientId = "d32fh16qw5qfk2giw6if0eg5tal34t";
        String clientSecret = "eg111oi7qtrwnkbvzxmaizgl9l01fq";

        // 1. Get app access token
        HttpRequest tokenRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://id.twitch.tv/oauth2/token" +
                    "?client_id=" + clientId +
                    "&client_secret=" + clientSecret +
                    "&grant_type=client_credentials"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .header("Content-Type", "application/x-www-form-urlencoded")
            .build();

        HttpResponse<String> tokenResponse = HttpClient.newHttpClient()
            .send(tokenRequest, HttpResponse.BodyHandlers.ofString());

        if (tokenResponse.statusCode() != 200) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("❌ Failed to get app access token: " + tokenResponse.body());
        }

        String appAccessToken = new JSONObject(tokenResponse.body()).getString("access_token");

        // 2. Prepare EventSub subscription body
        String callback = "https://grim-garnet-benji1012-c136b1f8.koyeb.app/twitch/webhook";
        String secret = "eg111oi7qtrwnkbvzxmaizgl9l01fq";

        String body = """
            {
              "type": "channel.channel_points_custom_reward_redemption.add",
              "version": "1",
              "condition": {
                "broadcaster_user_id": "%s"
              },
              "transport": {
                "method": "webhook",
                "callback": "%s",
                "secret": "%s"
              }
            }
            """.formatted(userId, callback, secret);

        HttpRequest subscribeRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://api.twitch.tv/helix/eventsub/subscriptions"))
            .header("Client-Id", clientId)
            .header("Authorization", "Bearer " + appAccessToken)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> subscribeResponse = HttpClient.newHttpClient()
            .send(subscribeRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("📬 Subscribe response: " + subscribeResponse.body());

        return ResponseEntity.ok(subscribeResponse.body());
    }


    
//    @PostMapping("/roll")
//    public ResponseEntity<?> rollDice(@RequestHeader("Authorization") String authHeader, @RequestBody RollDTO dto) {
//        try {
//            String token = authHeader.replace("Bearer ", "");
//            System.out.println("🔐 Token received: " + token);
//            String twitchUserId = twitchService.getTwitchopaque_user_idFromToken(token);
//            String twitchChannel = "";
//            if(twitchUserId != null) {
//            	 twitchUserId = twitchUserId.trim();
//            }
//           
//            // 🔍 1. Check if redemption exists
//            Optional<Redemptions> redemptionOpt = redemptionsRepository.findByUserId(twitchUserId);
//            if (redemptionOpt.isEmpty()) {
//            	 System.out.println("Redemption NOT found for userId: " + twitchUserId );
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                                     .body("No redemption found. Please redeem the roll reward first.");
//            }
//
//            Redemptions redemption = redemptionOpt.get();
//            String userName = redemption.getUserName() != null ? redemption.getUserName() : "Twitch viewer";
//            twitchChannel = redemption.getChannelId();
//            // 🧹 2. Delete the used redemption
//            redemptionsRepository.deleteById(redemption.getId());
//
//            // 🧠 3. Fetch Foundry config
//            Optional<UserConfig> configOpt = repository.findBytwitchChannelId(twitchChannel);
//            if (configOpt.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                                     .body("No config found for this Twitch channel.");
//            }
//
//            UserConfig config = configOpt.get();
//
//            // 🧪 4. Build payload
//            JSONObject payload = new JSONObject();
//            payload.put("formula", dto.getFormula());
//            payload.put("flavor", userName);
//            payload.put("target", "");
//            payload.put("speaker", "");
//            payload.put("itemUuid", "");
//            payload.put("createChatMessage", true);
//            payload.put("whisper", new org.json.JSONArray());
//
//            System.out.println("Sending dice roll payload for user: " + userName + " | Formula: " + dto.getFormula());
//
//            // 📡 5. Send the request
//            HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://foundryvtt-rest-api-relay.fly.dev/roll?clientId=" + client_id))
//                .header("x-api-key", config.getFoundryApiKey())
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
//                .build();
//
//            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//            System.out.println("Foundry Response: " + response.body());
//
//            return ResponseEntity.ok("Rolled the dice");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                                 .body("Error during dice roll: " + e.getMessage());
//        }
//    }

    
    @PostMapping("/roll")
    public ResponseEntity<?> rollDice(@RequestHeader("Authorization") String authHeader, @RequestBody RollDTO dto) {
        try {
            String token = authHeader.replace("Bearer ", "");
            System.out.println("🔐 Token received: " + token);

            // Try to extract real user ID
            String twitchUserId = twitchService.getTwitchUserIdFromToken(token);
            String twitchChannel = twitchService.getTwitchChannelFromToken(token);
            if (twitchUserId == null || twitchUserId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("IDENTITY_NOT_SHARED");
            }
            
            if (twitchChannel == null || twitchChannel.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("IDENTITY_NOT_SHARED");
            }

            twitchUserId = twitchUserId.trim();

            // 🔍 1. Check if redemption exists for this user
//            Optional<Redemptions> redemptionOpt = redemptionsRepository.findByUserIdAndChannelId(twitchUserId, twitchChannel);
//            if (redemptionOpt.isEmpty()) {
//                System.out.println("❌ No redemption found for userId: " + twitchUserId);
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("You must redeem the dice roll reward on Twitch before rolling.");
//            }
            List<Redemptions> redemptions = redemptionsRepository.findByUserIdAndChannelId(twitchUserId, twitchChannel);

            if (redemptions.isEmpty()) {
                System.out.println("❌ No redemption found for userId: " + twitchUserId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You must redeem the dice roll reward on Twitch before rolling.");
            }
            
         // Use the first redemption only
            Redemptions redemption = redemptions.get(0);
            String userName = redemption.getUserName() != null ? redemption.getUserName() : "Twitch viewer";

            // Delete only the redemption we use (one row)
            redemptionsRepository.deleteById(redemption.getId());

//            // ✅ 2. Use redemption data
//            Redemptions redemption = redemptionOpt.get();
//            String userName = redemption.getUserName() != null ? redemption.getUserName() : "Twitch viewer";
////            String twitchChannel = redemption.getChannelId().trim();
//
//            // 🧹 Delete used redemption
//            redemptionsRepository.deleteById(redemption.getId());

            // 🔧 3. Load Foundry config
            Optional<UserConfig> configOpt = repository.findBytwitchChannelId(twitchChannel);
            if (configOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No Foundry config found for this Twitch channel.");
            }

            UserConfig config = configOpt.get();

            // 🎲 4. Build and send roll payload
            JSONObject payload = new JSONObject();
            payload.put("formula", dto.getFormula());
            payload.put("flavor", userName);
            payload.put("target", "");
            payload.put("speaker", "");
            payload.put("itemUuid", "");
            payload.put("createChatMessage", true);
            payload.put("whisper", new org.json.JSONArray());

            System.out.println("🎲 Rolling dice for: " + userName + " | Formula: " + dto.getFormula());

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://foundryvtt-rest-api-relay.fly.dev/roll?clientId=" + client_id))
                .header("x-api-key", config.getFoundryApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("📩 Foundry response: " + response.body());
            return ResponseEntity.ok("✅ Dice rolled successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("❌ Error during dice roll: " + e.getMessage());
        }
    }
    
    @GetMapping("/login2")
    public void redirectToTwitch2(HttpServletResponse response) throws IOException {
        String clientId = "zed4vietrc5tn65xtn8eg5y01hr14k";
        String redirectUri = "https://grim-garnet-benji1012-c136b1f8.koyeb.app/api/callback2";
        String scope = "openid user:read:email";
        String url = "https://id.twitch.tv/oauth2/authorize?client_id=" + clientId +
                     "&redirect_uri=" + redirectUri +
                     "&response_type=code&scope=" + scope;
        response.sendRedirect(url);
    }
    
    @GetMapping("/callback2")
    public ResponseEntity<String> giveAccessToUserId(@RequestParam String code) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        String clientId = "zed4vietrc5tn65xtn8eg5y01hr14k";
        String clientSecret = "eg111oi7qtrwnkbvzxmaizgl9l01fq";
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://id.twitch.tv/oauth2/token" +
                    "?client_id=" + clientId +
                    "&client_secret=" + clientSecret +
                    "&code=" + code +
                    "&grant_type=authorization_code" +
                    "&redirect_uri=https://grim-garnet-benji1012-c136b1f8.koyeb.app/api/callback2"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .header("Content-Type", "application/x-www-form-urlencoded")
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
     // Parse the JSON response
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.readTree(response.body());
//        String accessToken = jsonNode.get("access_token").asText();

        return ResponseEntity.ok("");
    }
    
    
    @GetMapping("/sheets")
	    public ResponseEntity<?> getCharachterSheets(@RequestHeader("Authorization") String authHeader) {
	        try {
	            String token = authHeader.replace("Bearer ", "");
	            System.out.println("🔐 Token received: " + token);

	            String twitchChannel = twitchService.getTwitchChannelFromToken(token);
	            if (twitchChannel == null || twitchChannel.trim().isEmpty()) {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body("IDENTITY_NOT_SHARED");
	            }

	            Optional<UserConfig> configOpt = repository.findBytwitchChannelId(twitchChannel);
	            if (configOpt.isEmpty()) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("No Foundry config found for this Twitch channel.");
	            }

	            UserConfig config = configOpt.get();
	            
	            FoundryApiClientController foundryClient = new FoundryApiClientController(config.getFoundryApiKey());


	            List<String> playerNames = List.of(
	                    config.getPlayer1Name(),
	                    config.getPlayer2Name(),
	                    config.getPlayer3Name(),
	                    config.getPlayer4Name(),
	                    config.getPlayer5Name(),
	                    config.getPlayer6Name()
	                );

	                List<PlayerStatDTO> playerStats = new ArrayList<>();
	                List<SheethtmlDTO> playersheets = new ArrayList();

	                for (String name : playerNames) {
	                    if (name != null && !name.isBlank()) {
	                        Player player = new Player(name, foundryClient);
	                        playerStats.add(new PlayerStatDTO(player));
	                        setClient_id(player.getClient_id());
	                        playersheets.add(new SheethtmlDTO(name, foundryClient.getSheetByUUid(player.getUuid())));}
	                }


	            return ResponseEntity.ok(playersheets);

	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("❌ Error during getting sheets: " + e.getMessage());
	        }
	    }



	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
    
}

