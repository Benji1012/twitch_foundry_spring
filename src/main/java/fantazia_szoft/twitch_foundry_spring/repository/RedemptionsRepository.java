package fantazia_szoft.twitch_foundry_spring.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fantazia_szoft.twitch_foundry_spring.model.Redemptions;
import fantazia_szoft.twitch_foundry_spring.model.UserConfig;

import java.util.Optional;

@Repository
public interface RedemptionsRepository extends JpaRepository<Redemptions, Long> {

	  Optional<Redemptions> findByUserIdAndChanelId(String userId, String chanelId);

}
