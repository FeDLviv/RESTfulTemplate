package net.omisoft.rest.task;

import lombok.AllArgsConstructor;
import net.omisoft.rest.repository.AccessTokenRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class TokenTask {

    private final AccessTokenRepository accessTokenRepository;

    @CacheEvict(value = "tokens", allEntries = true)
    @Scheduled(cron = "${app.task.cronExpiredTokensRemove}")
    @Transactional
    public void removeExpiredTokens() {
        accessTokenRepository.removeAllExpiredTokens();
    }

}