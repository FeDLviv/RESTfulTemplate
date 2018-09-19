package net.omisoft.rest.service.fcm;

import net.omisoft.rest.model.projection.FCMTokenProjection;

import java.util.Set;

public interface FCMService {

    void send(Set<FCMTokenProjection> tokens, String title, String body, FCMType type);

    void sendAsync(Set<FCMTokenProjection> tokens, String title, String body, FCMType type);

}