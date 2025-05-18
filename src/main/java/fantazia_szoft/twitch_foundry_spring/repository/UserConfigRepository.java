package fantazia_szoft.twitch_foundry_spring.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fantazia_szoft.twitch_foundry_spring.model.UserConfig;

import java.util.Optional;

@Repository
public interface UserConfigRepository extends JpaRepository<UserConfig, Long> {
    Optional<UserConfig> findBytwitchChannelId(String twitchChannelId);
    Optional<UserConfig> findByTwitchuserId(String twitchuserId);

}
