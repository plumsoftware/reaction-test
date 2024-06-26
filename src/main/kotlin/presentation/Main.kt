package presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.repository.SettingsRepositoryImpl
import data.tests.TrafficLight
import domain.model.ReactionTest
import domain.model.Settings
import domain.storage.SettingsStorage
import domain.usecase.settings.GetUserSettingsUseCase
import domain.usecase.settings.SaveUserSettingsUseCase
import kotlinx.coroutines.*
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.viewmodel.viewModel
import presentation.authorization.AuthorizationPage
import presentation.authorization.viewmodel.AuthorizationViewModel
import presentation.home.HomePage
import presentation.home.store.Output
import presentation.home.viewmodel.HomeViewModel
import presentation.extension.route.DesktopRouting
import presentation.settings.SettingsPage
import presentation.settings.viewmodel.SettingsViewModel
import presentation.testmenu.TestMenu
import presentation.testmenu.viewmodel.TestMenuViewModel
import presentation.theme.AppTheme
import kotlin.coroutines.CoroutineContext

fun main() = run {

    println("App started!")

    application {
        val windowState = rememberWindowState()
        val reactionTests: List<ReactionTest> = listOf(
            TrafficLight()
        )

//        region::Storage
        val settingsRepository = SettingsRepositoryImpl()
        val settingsStorage = SettingsStorage(
            getUserSettingsUseCase = GetUserSettingsUseCase(settingsRepository),
            saveUserSettingsUseCase = SaveUserSettingsUseCase(settingsRepository)
        )
//        endregion

//        region::Actions
        val supervisorJob = SupervisorJob()
        val coroutineContextIO: CoroutineContext = Dispatchers.IO + supervisorJob

        var settings = Settings()
        CoroutineScope(coroutineContextIO).launch {
            settings = settingsStorage.get()
        }
//        endregion

        Window(
            onCloseRequest = ::exitApplication,
            icon = BitmapPainter(useResource("test_icon.png", ::loadImageBitmap)),
            title = "Тест на рекацию",
            state = windowState
        ) {
            AppTheme(useDarkTheme = settings.isDarkTheme) {
                PreComposeApp {

                    val navigator = rememberNavigator()
                    val homeViewModel = viewModel(modelClass = HomeViewModel::class) {
                        HomeViewModel(
                            output = { output ->
                                when (output) {
                                    Output.AboutProgramButtonClicked -> {

                                    }

                                    Output.SettingsButtonClicked -> {
                                        navigator.navigate(route = DesktopRouting.settings)
                                    }

                                    Output.TestsButtonClicked -> {
                                        navigator.navigate(route = DesktopRouting.testmenu)
                                    }
                                }
                            }
                        )
                    }
                    val testMenuViewModel = viewModel(modelClass = TestMenuViewModel::class) {
                        TestMenuViewModel(
                            output = { output ->
                                when (output) {
                                    is presentation.testmenu.store.Output.TestClicked -> {
                                        navigator.navigate(route = output.route)
                                    }

                                    presentation.testmenu.store.Output.BackButtonClicked -> {
                                        navigator.popBackStack()
                                    }
                                }
                            }
                        )
                    }
                    val authorizationViewModel = viewModel(modelClass = AuthorizationViewModel::class) {
                        AuthorizationViewModel(
                            output = { output ->
                                when (output) {
                                    presentation.authorization.store.Output.BackButtonClicked -> {
                                        navigator.popBackStack()
                                    }
                                }
                            }
                        )
                    }
                    val settingsViewModel = viewModel(modelClass = SettingsViewModel::class) {
                        SettingsViewModel(
                            settingsStorage = settingsStorage,
                            coroutineContextIO = coroutineContextIO,
                            output = { output ->
                                when (output) {
                                    presentation.settings.store.Output.BackClicked -> navigator.popBackStack()
                                }
                            }
                        )
                    }

                    NavHost(
                        navigator = navigator,
                        navTransition = NavTransition(),
                        initialRoute = DesktopRouting.home,
                        modifier = Modifier.padding(all = 0.dp)
                    ) {
                        scene(route = DesktopRouting.home) {
                            println("Home page rendered")
                            HomePage(homeViewModel::onEvent)
                        }
                        scene(route = DesktopRouting.testmenu) {
                            println("Test menu page rendered")
                            TestMenu(testMenuViewModel::onEvent, reactionTests)
                        }
                        scene(route = DesktopRouting.auth) {
                            println("Authorization page rendered")
                            AuthorizationPage(authorizationViewModel::onEvent)
                        }
                        scene(route = DesktopRouting.trafficLight) {
                            println("TrafficLight page rendered")
                        }
                        scene(route = DesktopRouting.settings) {
                            println("Settings page rendered")
                            SettingsPage(settingsViewModel::onEvent, settingsViewModel.state)
                        }
                    }
                }
            }
        }
    }
}
