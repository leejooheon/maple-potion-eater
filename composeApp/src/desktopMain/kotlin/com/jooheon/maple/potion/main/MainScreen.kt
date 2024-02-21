package com.jooheon.maple.potion.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Button
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.jooheon.maple.potion.automatic.PotionEater
import com.jooheon.maple.potion.display.DisplayProvider
import com.jooheon.maple.potion.display.DisplayScreen
import com.jooheon.maple.potion.health.HealthModel
import com.jooheon.maple.potion.health.HealthProvider
import com.jooheon.maple.potion.setting.SettingEvent
import com.jooheon.maple.potion.setting.SettingProvider
import com.jooheon.maple.potion.setting.SettingScreen
import kotlinx.coroutines.launch
import java.awt.Robot
import java.awt.Window

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(window: Window) {
    val pagerState = rememberPagerState(
        initialPage = TabNavigation.InitialPage,
        pageCount = { TabNavigation.PageNumber }
    )
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Tabs(pagerState)
        TabsContent(window, pagerState)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Tabs(pagerState: PagerState) {
    val tabs = TabNavigation.Tabs
    val scope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.White,
        contentColor = Color.White,
        tabs = {
            tabs.forEachIndexed { index, _ ->
                Tab(
                    text = {
                        Text(
                            text = tabs[index].title,
                            fontSize = 16.sp,
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Light,
                            color = Color.Black,
                            textAlign = TextAlign.Left
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TabsContent(
    window: Window,
    pagerState: PagerState,
) {
    val robot = remember { Robot() }
    val scope = rememberCoroutineScope()

    val settingProvider = remember { SettingProvider() }
    val displayProvider = remember { DisplayProvider(robot) }
    val healthProvider = remember {
        HealthProvider(
            scope = scope,
            displayState = displayProvider.model,
            settingState = settingProvider.model
        )
    }
    remember {
        PotionEater(
            robot = robot,
            scope = scope,
            healthState = healthProvider.model,
            settingState = settingProvider.model
        )
    }

    var start by remember { mutableStateOf(false) }
    LaunchedEffect(start) {
        if(start) displayProvider.start()
        else displayProvider.stop()
    }

    val settingState by settingProvider.model.collectAsState()
    val screenState by displayProvider.model.collectAsState()
    val healthState by healthProvider.model.collectAsState(HealthModel.default)

    HorizontalPager(pagerState) { page ->
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (page) {
                TabNavigation.Display.index -> {
                    DisplayScreen(
                        screenState = screenState,
                        healthState = healthState,
                        settingState = settingState,
                    )
                    Button(onClick = { start = !start }) {
                        Text(
                            text = if (start) "Stop" else "Start"
                        )
                    }
                }
                TabNavigation.Setting.index -> {
                    SettingScreen(
                        window = window,
                        state = settingState,
                        onSaveClicked = {
                            settingProvider.dispatch(SettingEvent.OnSaveClick(it))
                        }
                    )
                }
            }
        }
    }
}