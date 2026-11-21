
package com.plusorminustwo.postmark.service.smartreply

import android.content.Context
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.TextMessage
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartReplyEngine @Inject constructor(
    @ApplicationContext context: Context
) {
    private val client = SmartReply.getClient()
    private val moduleInstallClient = ModuleInstall.getClient(context)

    private val lock = Any()
    private var installationInProgress = false
    private val pendingRequests =
        mutableListOf<Pair<List<TextMessage>, (List<String>) -> Unit>>()

    fun installModel() {
        startModelInstallation()
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
        conversation: List<TextMessage>,
        onResult: (List<String>) -> Unit
    ) {
        isModelAvailable { isAvailable ->
            if (isAvailable) {
                requestSuggestions(conversation, onResult)
            } else {
                synchronized(lock) {
                    pendingRequests.add(conversation to onResult)
                }
                startModelInstallation()
            }
        }
    }

    private fun startModelInstallation() {
        synchronized(lock) {
            if (installationInProgress) return
            installationInProgress = true
        }

        val request = ModuleInstallRequest.Builder()
            .addApi(client)
            .build()

        moduleInstallClient.installModules(request)
            .addOnSuccessListener {
                isModelAvailable { available ->
                    finishInstallation(available)
                }
            }
            .addOnFailureListener {
                finishInstallation(false)
            }
    }

    private fun finishInstallation(available: Boolean) {
        val requests = synchronized(lock) {
            installationInProgress = false
            pendingRequests.toList().also {
                pendingRequests.clear()
            }
        }

        requests.forEach { (conversation, onResult) ->
            if (available) {
                requestSuggestions(conversation, onResult)
            } else {
                onResult(emptyList())
            }
        }
    }

    private fun requestSuggestions(
        conversation: List<TextMessage>,
        onResult: (List<String>) -> Unit
    ) {
        client.suggestReplies(conversation)
            .addOnSuccessListener { result ->
                onResult(
                    if (result.status ==
                        SmartReplySuggestionResult.STATUS_SUCCESS
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
