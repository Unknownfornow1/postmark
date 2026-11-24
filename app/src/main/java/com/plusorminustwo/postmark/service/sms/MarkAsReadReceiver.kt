package com.plusorminustwo.postmark.service.sms

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.plusorminustwo.postmark.PostmarkApplication
import com.plusorminustwo.postmark.data.repository.MessageRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles the "Mark as read" action on incoming SMS/MMS notifications.
 *
 * Tapping the action button triggers this receiver, which:
 *  1. Marks unread rows read in both [Telephony.Sms.CONTENT_URI] and
 *     [Telephony.Mms.CONTENT_URI], scoped to [EXTRA_THREAD_ID] when available (see
 *     [ConversationReadMarker]) — this covers MMS (group messages, media), which have
 *     no single stable sender address to filter on. Falls back to the address-scoped
 *     SMS-only update when no thread id was passed (older/edge-case notifications).
 *  2. Cancels the individual notification so it disappears from the shade.
 *  3. Cancels the group summary notification if no other SMS notifications remain.
 *
 * The receiver also marks the corresponding Room messages as read directly,
 * keeping the local database and Android's telephony provider in sync.
 *
 * Requires WRITE_SMS (already declared in AndroidManifest).
 */
@AndroidEntryPoint
class MarkAsReadReceiver : BroadcastReceiver() {

        @Inject
    lateinit var messageRepository: MessageRepository

    @Inject
    lateinit var incomingNotifier: IncomingNotifier

    override fun onReceive(context: Context, intent: Intent) {
        val address  = intent.getStringExtra(EXTRA_ADDRESS) ?: return
        val notifId  = intent.getIntExtra(EXTRA_NOTIF_ID, 0)
        val threadId = intent.getLongExtra(EXTRA_THREAD_ID, -1L)

        // ── Extend receiver lifetime ──────────────────────────────────────────────
        // goAsync() keeps the receiver alive past onReceive() so the ContentResolver
        // update (an I/O call) does not run on the main thread and risk an ANR.
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ── Mark messages as read in the telephony provider(s) ────────────
                // Each provider update is wrapped independently so a failure in one
                // (e.g. MMS write rejected) can't skip the other or the notification
                // cancel below.
                ConversationReadMarker.buildUpdates(threadId, address).forEach { update ->
                    try {
                        val uri = when (update.table) {
                            ConversationReadMarker.Table.SMS -> Telephony.Sms.CONTENT_URI
                            ConversationReadMarker.Table.MMS -> Telephony.Mms.CONTENT_URI
                        }
                        context.contentResolver.update(
                            uri,
                            ContentValues().apply { put(Telephony.Sms.READ, 1) }, // "read" — same column name in both providers
                            update.selection,
                            update.selectionArgs.toTypedArray()
                        )
                    } catch (e: Exception) {
                        // Best-effort: one provider failing must not prevent the other
                        // update or the notification dismissal in `finally` below.
                    }
                }
                                                if (threadId > 0L) {
                    messageRepository.markAllRead(threadId)
                }

                incomingNotifier.markConversationRead(notifId)
                                                } finally {
                            pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_ADDRESS   = "extra_mark_read_address"
        const val EXTRA_NOTIF_ID  = "extra_mark_read_notif_id"
        const val EXTRA_THREAD_ID = "extra_mark_read_thread_id"
    }
}
