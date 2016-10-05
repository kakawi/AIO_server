package com.hlebon.messageHandlers.server;

import com.hlebon.message.MessageWrapper;

public interface MessageHandlerServer {
    void handle(MessageWrapper messageWrapper);
}
