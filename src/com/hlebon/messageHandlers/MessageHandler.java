package com.hlebon.messageHandlers;

import com.hlebon.message.MessageWrapper;

public interface MessageHandler {
    void handle(MessageWrapper messageWrapper);
}
