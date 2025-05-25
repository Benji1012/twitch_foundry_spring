package fantazia_szoft.twitch_foundry_spring.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fantazia_szoft.twitch_foundry_spring.dto.PlayerStatDTO;
import fantazia_szoft.twitch_foundry_spring.dto.RollDTO;
import fantazia_szoft.twitch_foundry_spring.dto.UserConfigDTO;
import fantazia_szoft.twitch_foundry_spring.model.Player;
import fantazia_szoft.twitch_foundry_spring.model.UserConfig;
import fantazia_szoft.twitch_foundry_spring.repository.UserConfigRepository;
import fantazia_szoft.twitch_foundry_spring.service.TwitchService;

import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles.Lookup;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://127.0.0.1:8082", "https://zed4vietrc5tn65xtn8eg5y01hr14k.ext-twitch.tv"})
public class UserConfigController {

    private final UserConfigRepository repository;
   
    private final TwitchService twitchService;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private String client_id = ""; 
    
    public UserConfigController(UserConfigRepository repository,TwitchService twitchService) {
        this.twitchService = twitchService;
        this.repository = repository;
    }

    @PostMapping("/config")
    public UserConfig registerConfig( @RequestBody UserConfigDTO dto,
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
        System.out.println(config.toString());
        return repository.save(config);
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
    
    @PostMapping("/roll")
    public ResponseEntity<?> rollDice( @RequestHeader("Authorization") String authHeader,  @RequestBody RollDTO dto) {
        try {
        	System.out.println("/roll is called. rolldto: "+dto.getFormula().toString());
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
        	
                 JSONObject payload = new JSONObject();
                 payload.put("formula", dto.getFormula());
                 payload.put("flavor", "Twitch viewer");
                 payload.put("target", "");
                 payload.put("speaker", "");
                 payload.put("itemUuid", "");
                 payload.put("createChatMessage", true);
                 payload.put("whisper", new org.json.JSONArray());
                 
               System.out.println("json: "+payload);

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://foundryvtt-rest-api-relay.fly.dev" + "/roll?clientId=" + client_id))
                    .header("x-api-key", config.getFoundryApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Dice roll result: " + response.body());
           

                return ResponseEntity.ok("Rolled the dice");

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body("Error fetching player stats: " + e.getMessage());
            }
    }

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	

    
}

