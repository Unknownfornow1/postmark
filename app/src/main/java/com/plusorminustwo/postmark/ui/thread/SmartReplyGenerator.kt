package com.plusorminustwo.postmark.ui.thread

import com.google.mlkit.nl.smartreply.TextMessage
import com.plusorminustwo.postmark.domain.model.Message

object SmartReplyGenerator {

    fun createConversation(messages: List<Message>): List<TextMessage> {
        return messages
            .filter { it.body.isNotBlank() }
            .takeLast(10)
            .map { message ->
                if (message.isSent) {
                    TextMessage.createForLocalUser(
                        message.body,
                        message.timestamp
                    )
                } else {
                    TextMessage.createForRemoteUser(
                        message.body,
                        message.timestamp,
                        message.address
                    )
                }
            }
    }
}
