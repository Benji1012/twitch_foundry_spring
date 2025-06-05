package fantazia_szoft.twitch_foundry_spring.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fantazia_szoft.twitch_foundry_spring.model.Redemptions;
import fantazia_szoft.twitch_foundry_spring.repository.RedemptionsRepository;

@RestController
@RequestMapping("/twitch")
public class TwitchWebhookController {

    @Autowired
    private RedemptionsRepository redemptionsRepository;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleTwitchWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Twitch-Eventsub-Message-Type", required = false) String messageType
    ) {
        try {
        	System.out.println("messageType: "+ messageType);
            // ✅ Handle webhook verification
            if ("webhook_callback_verification".equalsIgnoreCase(messageType)) {
                String challenge = (String) payload.get("challenge");
                return ResponseEntity.ok(challenge);
            }

            if ("notification".equalsIgnoreCase(messageType)) {
                Map<String, Object> event = (Map<String, Object>) payload.get("event");
                Map<String, Object> reward = (Map<String, Object>) event.get("reward");
                System.out.println("ezt kapom: "+ payload.toString());
                String userId = (String) event.get("user_id");
                String userName = (String) event.get("user_name");
                String channelId = (String) event.get("broadcaster_user_id");
                String rewardTitle = (String) reward.get("title");

                System.out.println("🔔 Redemption received: " + userName + " in channel " + channelId);

                if (reward != null && rewardTitle != null && "Inspiráció!".equalsIgnoreCase(rewardTitle)) {
	                Redemptions redemption = new Redemptions();
	                redemption.setUserId(userId);
	                redemption.setUserName(userName);
	                redemption.setChanelId(channelId);
	
	                redemptionsRepository.save(redemption);
                }

                return ResponseEntity.ok("Redemption saved");
            }

            return ResponseEntity.ok("Unhandled message type: " + messageType);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook: " + e.getMessage());
        }
    }
}

