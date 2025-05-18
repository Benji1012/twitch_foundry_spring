package fantazia_szoft.twitch_foundry_spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fantazia_szoft.twitch_foundry_spring.dto.PlayerStatDTO;
import fantazia_szoft.twitch_foundry_spring.dto.UserConfigDTO;
import fantazia_szoft.twitch_foundry_spring.model.Player;
import fantazia_szoft.twitch_foundry_spring.model.UserConfig;
import fantazia_szoft.twitch_foundry_spring.repository.UserConfigRepository;
import fantazia_szoft.twitch_foundry_spring.service.TwitchService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://127.0.0.1:8082")
public class UserConfigController {

    private final UserConfigRepository repository;
   
    private final TwitchService twitchService;
    
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
        config.settwitchChannelId(twitchChanel);
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
                    }
                }

                return ResponseEntity.ok(playerStats);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body("Error fetching player stats: " + e.getMessage());
            }
    }

    
}

