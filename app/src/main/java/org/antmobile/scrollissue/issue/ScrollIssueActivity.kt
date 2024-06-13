package org.antmobile.scrollissue.issue

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.util.VelocityTrackerAddPointsFix
import org.antmobile.scrollissue.TestList
import org.antmobile.scrollissue.ui.theme.ComposeScrollIssueOnZebraDevicesTheme

class ScrollIssueActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VelocityTrackerAddPointsFix = true
            ComposeScrollIssueOnZebraDevicesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TestList(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, ScrollIssueActivity::class.java)
    }
}
