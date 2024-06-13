package org.antmobile.scrollissue

import android.util.Log
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import org.antmobile.scrollissue.ui.theme.ComposeScrollIssueOnZebraDevicesTheme

@Composable
fun TestList(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .padding(10.dp, 0.dp, 10.dp, 0.dp)
            .pointerInput(Unit) {
                coroutineScope {
                    awaitEachGesture {
                        while (isActive) {
                            awaitPointerEvent(pass = PointerEventPass.Initial).changes.forEach {
                                Log.d("TEST", "PointerEvent Initial: $it")
                            }
                        }
                    }
                }
            }
            .nestedScroll(
                object : NestedScrollConnection {
                    override suspend fun onPreFling(available: Velocity): Velocity {
                        Log.d("TEST", "onPreFling available: $available")
                        return super.onPreFling(available)
                    }

                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        Log.d("TEST", "onPreScroll available: $available - $source")
                        return super.onPreScroll(available, source)
                    }

                    override suspend fun onPostFling(
                        consumed: Velocity,
                        available: Velocity
                    ): Velocity {
                        Log.d("TEST", "onPostFling available: $available, consumed: $consumed")
                        return super.onPostFling(consumed, available)
                    }

                    override fun onPostScroll(
                        consumed: Offset,
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        Log.d(
                            "TEST",
                            "onPostScroll available: $available - $source, consumed: $consumed"
                        )
                        return super.onPostScroll(consumed, available, source)
                    }

                }),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(100) { item ->
            Button(
                onClick = { },
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
            ) {
                Text(
                    text = "no $item",
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Preview
@Composable
private fun TestListPreview() {
    ComposeScrollIssueOnZebraDevicesTheme {
        TestList()
    }
}