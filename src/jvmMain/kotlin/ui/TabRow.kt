package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun TabRow(texts: Map<Int, String>, currentTab: Int, scrollState: ScrollState, onTabChange: (Int) -> Unit) {
    if (texts.size > 1) {
        Box(modifier = Modifier.fillMaxWidth()) {

            Box(
                modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState)
            ) {
                Row {
                    // default padding values
                    for (item in texts.toSortedMap()) {
                        Button(
                            onClick = { onTabChange(item.key) },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp, start = 2.dp, end = 2.dp).height(24.dp),
                            contentPadding = PaddingValues(4.dp),
                        ) {
                            Text(
                                text = "" + item.key,
                                fontWeight = if (item.key == currentTab) FontWeight.ExtraBold else FontWeight.Normal,
                                textDecoration = if (item.key == currentTab) TextDecoration.Underline else TextDecoration.None,
                            )
                        }
                        if (item.key < texts.size) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(end = 3.dp),
                adapter = rememberScrollbarAdapter(scrollState),
                style = ScrollbarStyle(
                    thickness = 0.dp,
                    minimalHeight = 0.dp,
                    unhoverColor = androidx.compose.ui.graphics.Color.Transparent,
                    hoverColor = androidx.compose.ui.graphics.Color.Transparent,
                    hoverDurationMillis = 0,
                    shape = RectangleShape
                )
            )
        }
    }
}