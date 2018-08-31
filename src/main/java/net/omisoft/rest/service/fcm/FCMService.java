package net.omisoft.rest.service.fcm;

import net.omisoft.rest.pojo.CustomFCMToken;

import java.util.Set;

public interface FCMService {

    void send(Set<CustomFCMToken> tokens, String title, String body, FCMType type);

    void sendAsync(Set<CustomFCMToken> tokens, String title, String body, FCMType type);

}