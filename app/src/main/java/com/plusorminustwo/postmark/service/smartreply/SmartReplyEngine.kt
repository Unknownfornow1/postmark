package com.plusorminustwo.postmark.service.smartreply

import android.content.Context
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.nl.smartreply.SmartReply
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartReplyEngine @Inject constructor(
    @ApplicationContext context: Context
) {
    private val client = SmartReply.getClient()
    private val moduleInstallClient = ModuleInstall.getClient(context)

    fun installModel() {
        val request = ModuleInstallRequest.Builder()
            .addApi(client)
            .build()

        moduleInstallClient.installModules(request)
    }

    fun isModelAvailable(
        onResult: (Boolean) -> Unit
    ) {
        moduleInstallClient.areModulesAvailable(client)
            .addOnSuccessListener { response ->
                onResult(response.areModulesAvailable())
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun suggestReplies(
        conversation: List<com.google.mlkit.nl.smartreply.TextMessage>,
        onResult: (List<String>) -> Unit
    ) {
        client.suggestReplies(conversation)
            .addOnSuccessListener { result ->
                onResult(
                    if (result.status ==
                        com.google.mlkit.nl.smartreply.SmartReplySuggestionResult.STATUS_SUCCESS
                    ) {
                        result.suggestions.map { it.text }
                    } else {
                        emptyList()
                    }
                )
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
