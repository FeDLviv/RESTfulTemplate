package net.omisoft.rest.service.push;

import lombok.AllArgsConstructor;
import net.omisoft.rest.converter.FCMTokenMapper;
import net.omisoft.rest.dto.fcm.FCMTokenCreateDto;
import net.omisoft.rest.model.FCMTokenEntity;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.model.base.OS;
import net.omisoft.rest.repository.FCMTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PushServiceImpl implements PushService {

    private final FCMTokenRepository fcmTokenRepository;
    private final FCMTokenMapper fcmTokenMapper;

    @Override
    @Transactional
    public void saveToken(FCMTokenCreateDto data, UserEntity currentUser) {
        FCMTokenEntity entity = fcmTokenRepository.findByDeviceAndOsAndUser(data.getDevice(), OS.valueOf(data.getOs()), currentUser)
                .orElse(null);
        if (entity != null) {
            fcmTokenMapper.update(data, entity);
        } else {
            entity = fcmTokenMapper.convert(data);
            entity.setUser(currentUser);
        }
        fcmTokenRepository.save(entity);
    }

}