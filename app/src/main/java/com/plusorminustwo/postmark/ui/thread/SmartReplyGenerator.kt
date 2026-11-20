
package com.plusorminustwo.postmark.ui.thread

object SmartReplyGenerator {

    fun generate(message: String): List<String> {
        val text = message.trim().lowercase()

        if (text.isBlank()) return emptyList()

        return when {
            text.matches(Regex("^(hi|hello|hey|hiya)[!.? ]*$")) ||
                text.startsWith("good morning") ||
                text.startsWith("good afternoon") ||
                text.startsWith("good evening") -> listOf(
                    "Hey! How are you?",
                    "Hello! How's your day going?",
                    "Hey, good to hear from you!"
                )

            text.contains("how are you") ||
                text.contains("how are things") ||
                text.contains("how have you been") -> listOf(
                    "I'm doing well, thanks!",
                    "I'm good. How about you?",
                    "Doing fine, how's your day going?"
                )

            text.contains("thank you") ||
                text.contains("thanks") -> listOf(
                    "You're welcome!",
                    "No problem at all!",
                    "Anytime!"
                )

            text.contains("sorry") ||
                text.contains("apolog") -> listOf(
                    "It's okay, don't worry.",
                    "I understand.",
                    "No worries."
                )

            text.contains("where are you") ||
                text.contains("are you coming") ||
                text.contains("when will you") -> listOf(
                    "I'm on my way.",
                    "I'll let you know soon.",
                    "Give me a little while."
                )

            text.contains("?") -> listOf(
                "Yes, I understand.",
                "I'll let you know.",
                "Let me think about it."
            )

            else -> listOf(
                "Okay, got it.",
                "Thanks for letting me know.",
                "Alright!"
            )
        }
    }
}
