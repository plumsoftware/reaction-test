package presentation

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.viewmodel.viewModel
import presentation.home.HomePage
import presentation.home.store.Output
import presentation.home.viewmodel.HomeViewModel
import presentation.extension.route.DesktopRouting
import presentation.testmenu.TestMenu
import presentation.testmenu.viewmodel.TestMenuViewModel
import presentation.theme.AppTheme

fun main() = run {
    application {
        val windowState = rememberWindowState()

        Window(
            onCloseRequest = ::exitApplication,
            icon = BitmapPainter(useResource("test_icon.png", ::loadImageBitmap)),
            title = "Тест на рекацию",
            state = windowState
        ) {
            AppTheme(useDarkTheme = false) {
                PreComposeApp {

                    val navigator = rememberNavigator()
                    val homeViewModel = viewModel(modelClass = HomeViewModel::class) {
                        HomeViewModel(
                            output = { output ->
                                when (output) {
                                    Output.AboutProgramButtonClicked -> {

                                    }

                                    Output.SettingsButtonClicked -> {

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
                                    presentation.testmenu.store.Output.Test1Clicked -> {

                                    }

                                    presentation.testmenu.store.Output.BackButtonClicked -> {
                                        navigator.popBackStack()
                                    }
                                }
                            }
                        )
                    }

                    NavHost(
                        navigator = navigator,
                        navTransition = NavTransition(),
                        initialRoute = DesktopRouting.home,
                    ) {
                        scene(
                            route = DesktopRouting.home,
                            navTransition = NavTransition(),
                        ) {
                            HomePage(homeViewModel::onEvent)
                        }

                        scene(
                            route = DesktopRouting.testmenu,
                            navTransition = NavTransition()
                        ) {
                            TestMenu(testMenuViewModel::onEvent)
                        }
                    }
                }
            }
        }
    }
}
