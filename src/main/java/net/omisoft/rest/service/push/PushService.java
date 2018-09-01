package net.omisoft.rest.service.push;

import net.omisoft.rest.dto.fcm.FCMTokenCreateDto;
import net.omisoft.rest.model.UserEntity;

public interface PushService {

    void saveToken(FCMTokenCreateDto data, UserEntity currentUser);

}