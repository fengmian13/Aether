package com.anther.websocket.message;


import com.anther.entity.dto.MessageSendDto;
import org.springframework.stereotype.Component;

@Component("messageHandler")
public interface MessageHandler {

    void listenMessage();

    void sendMessage(MessageSendDto sendDto);
}
