package org.antmobile.scrollissue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.antmobile.scrollissue.issue.ScrollIssueActivity
import org.antmobile.scrollissue.ui.theme.ComposeScrollIssueOnZebraDevicesTheme
import org.antmobile.scrollissue.workaround.WithWorkaroundActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeScrollIssueOnZebraDevicesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val context = LocalContext.current
                        Button(onClick = {
                            context.startActivity(ScrollIssueActivity.newIntent(context))
                        }) {
                            Text("With Scroll Issue")
                        }
                        Button(onClick = {
                            context.startActivity(WithWorkaroundActivity.newIntent(context))
                        }) {
                            Text("With Workaround")
                        }
                    }
                }
            }
        }
    }
}