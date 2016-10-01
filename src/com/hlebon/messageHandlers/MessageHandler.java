package com.hlebon.messageHandlers;

import AIO.message.MessageWrapper;

public interface MessageHandler {
    void handle(MessageWrapper messageWrapper);
}
