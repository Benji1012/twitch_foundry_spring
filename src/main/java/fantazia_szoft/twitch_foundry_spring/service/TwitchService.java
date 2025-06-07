package fantazia_szoft.twitch_foundry_spring.service;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class TwitchService {

//    private final WebClient webClient;
//
//    public TwitchService(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.baseUrl("https://id.twitch.tv").build();
//    }

//    public String getTwitchUsernameFromToken(String accessToken) {
//        return webClient.get()
//                .uri("/oauth2/validate")
//                .header(HttpHeaders.AUTHORIZATION, "OAuth " + accessToken)
//                .retrieve()
//                .bodyToMono(JsonNode.class)
//                .map(json -> json.get("login").asText())
//                .block(); // Blocking here for simplicity; can be reactive if needed
//    }
    
    public String getTwitchUserIdFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("opaque_user_id").asString(); // or use "preferred_username" if available
    }

    public String getTwitchChannelFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("channel_id").asString(); // or "channel_name" depending on Twitch config
    }
}
