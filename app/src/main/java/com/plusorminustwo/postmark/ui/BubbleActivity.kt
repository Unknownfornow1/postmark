
package com.plusorminustwo.postmark.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.plusorminustwo.postmark.ui.theme.PostmarkTheme
import com.plusorminustwo.postmark.ui.thread.ThreadScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BubbleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val threadId = intent.getLongExtra(EXTRA_THREAD_ID, -1L)
intent.putExtra("threadId", threadId)
        if (threadId <= 0L) {
            finish()
            return
        }

        setContent {
            PostmarkTheme {
                ThreadScreen(
                    threadId = threadId,
                    onBack = { finish() }
                )
            }
        }
    }

    companion object {
        const val EXTRA_THREAD_ID =
            "com.plusorminustwo.postmark.extra.BUBBLE_THREAD_ID"
    }
}
