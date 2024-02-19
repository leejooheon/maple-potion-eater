package com.jooheon.maple.potion.setting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.onClick
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jooheon.maple.potion.util.toPercentRange
import kotlinx.coroutines.launch
import java.awt.Window
import java.awt.event.ActionListener
import java.lang.NullPointerException
import javax.swing.JFileChooser

@Composable
fun SettingScreen(
    window: Window,
    state: SettingModel,
    onSaveClicked: (SettingModel) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var filePath by remember { mutableStateOf(state.tesseractPath) }
    val fileChooser = remember { JFileChooser() }
    val listener = remember {
        ActionListener { actionEvent ->
            println("action: $actionEvent")
            when(actionEvent.actionCommand) {
                "ApproveSelection" -> {
                    try {
                        filePath = fileChooser.selectedFile.path
                    } catch (e: NullPointerException) { /** nothing **/ }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        fileChooser.apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            approveButtonText = "디렉토리를 선택하세요."
            addActionListener(listener)
        }
    }

    var fullHpValue by remember { mutableStateOf(state.fullHp.toString()) }
    var fullMpValue by remember { mutableStateOf(state.fullMp.toString()) }

    var hpEatPercentage by remember { mutableStateOf(state.hpEatPercentage.toString()) }
    var mpEatPercentage by remember { mutableStateOf(state.mpEatPercentage.toString()) }

    Column (
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    ) {
        FileChooserRow(
            title = "Tesseract tessdata path",
            initialValue = filePath,
            onClicked = {
                scope.launch {
                    fileChooser.showOpenDialog(window)
                }
            }
        )
        ContentRow(
            title = "Full HP",
            initialValue = fullHpValue,
            onValueChanged = {
                fullHpValue = it
            }
        )
        ContentRow(
            title = "Full MP",
            initialValue = fullMpValue,
            onValueChanged = {
                fullMpValue = it
            }
        )

        ContentRow(
            title = "HP Eat Percentage (0~100)",
            initialValue = hpEatPercentage,
            onValueChanged = {
                hpEatPercentage = it
            }
        )
        ContentRow(
            title = "MP Eat Percentage (0~100)",
            initialValue = mpEatPercentage,
            onValueChanged = {
                mpEatPercentage = it
            }
        )

        Button(
            modifier = Modifier.padding(16.dp),
            onClick = {
                val hp = fullHpValue.toIntOrNull() ?: return@Button
                val mp = fullMpValue.toIntOrNull() ?: return@Button

                val hpPercentage = hpEatPercentage.toPercentRange() ?: return@Button
                val mpPercentage = mpEatPercentage.toPercentRange() ?: return@Button

                val tesseractPath = filePath
                val newModel = state.copy(
                    fullHp = hp,
                    fullMp = mp,
                    hpEatPercentage = hpPercentage,
                    mpEatPercentage = mpPercentage,
                    tesseractPath = tesseractPath,
                )

                onSaveClicked.invoke(newModel)
            }
        ) {
            Text(text = "Save")
        }
    }
}

@Composable
private fun ContentRow(
    title: String,
    initialValue: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = Modifier.padding(8.dp))
    Row(
        modifier = modifier
            .background(Color.Black)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            color = Color.White,
            style = MaterialTheme.typography.h5,
            text = title,
        )
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            tint = Color.White,
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        BasicTextField(
            value = initialValue,
            onValueChange = onValueChanged,
            textStyle = MaterialTheme.typography.h5.copy(
                color = Color.White,
                fontWeight = FontWeight.Light
            ),
            modifier = Modifier
                .widthIn(min = 128.dp)
                .height(24.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileChooserRow(
    title: String,
    initialValue: String,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = Modifier.padding(16.dp))
    Row(
        modifier = modifier
            .background(Color.Black)
            .padding(16.dp)
            .onClick { onClicked.invoke() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            color = Color.White,
            style = MaterialTheme.typography.h5,
            text = title,
        )
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            tint = Color.White,
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        BasicTextField(
            value = initialValue,
            readOnly = true,
            onValueChange = { /** nothing **/ },
            textStyle = MaterialTheme.typography.body2.copy(
                color = Color.White,
                fontWeight = FontWeight.Light
            ),
            modifier = Modifier
                .widthIn(min = 128.dp)
                .height(24.dp)
                .onClick { onClicked.invoke() }
        )
    }
}