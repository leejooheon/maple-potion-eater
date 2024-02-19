package com.jooheon.maple.potion.display

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.jooheon.maple.potion.health.HealthModel
import com.jooheon.maple.potion.setting.SettingModel

@Composable
fun DisplayScreen(
    screenState: DisplayModel,
    healthState: HealthModel,
    settingState: SettingModel,
) {
    Column(
        modifier = Modifier.padding(32.dp)
    ) {
        Row {
            Column(modifier = Modifier.weight(0.65f)) {
                Text(
                    color = Color.White,
                    style = MaterialTheme.typography.h5,
                    text = "Screen Size",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                )
                Box {
                    Image(
                        bitmap = screenState.screenImage,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.aspectRatio(16f / 9f)
                    )
                    Text(
                        color = Color.White,
                        style = MaterialTheme.typography.body2,
                        text = "${DisplayModel.screenRect}".removePrefix("java.awt.Rectangle"),
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.05f))

            Column(
                modifier = Modifier.weight(0.3f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Text(
                        color = Color.White,
                        style = MaterialTheme.typography.h5,
                        text = "Health Point",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Image(
                    bitmap = screenState.hpImage,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                )
                Text(
                    color = Color.Black,
                    style = MaterialTheme.typography.h5,
                    text = "${healthState.hpPoint} / ${settingState.fullHp}",
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Text(
                        color = Color.White,
                        style = MaterialTheme.typography.h5,
                        text = "Magic Point",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Image(
                    bitmap = screenState.mpImage,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                )

                Text(
                    color = Color.Black,
                    style = MaterialTheme.typography.h5,
                    text = "${healthState.mpPoint} / ${settingState.fullMp}",
                )
            }
        }
    }
}

@Preview
@Composable
private fun ScreenDisplayPreview() {
    MaterialTheme {
        DisplayScreen(
            screenState = DisplayModel.default,
            healthState = HealthModel.default,
            settingState = SettingModel.default,
        )
    }
}