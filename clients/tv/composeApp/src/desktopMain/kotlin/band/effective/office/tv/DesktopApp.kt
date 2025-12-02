package band.effective.office.tv

import band.effective.office.tv.Res
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import band.effective.office.tv.di.KoinInitializer
import band.effective.office.tv.environment.Environment
import band.effective.office.tv.logger.LoggerInitializer
import band.effective.office.tv.root.RootComponent
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry

fun main() {
    val environment = Environment.fromBuildKonfig()
    LoggerInitializer().init(environment.isDebug)
    val koinInitializer = KoinInitializer()
    koinInitializer.init(environment)

    application {
        Window(
            onCloseRequest = {
                koinInitializer.stop()
                exitApplication()
            },
            title = stringResource(Res.string.app_title)
        ) {
            val rootComponent = remember {
                RootComponent(
                    componentContext = DefaultComponentContext(LifecycleRegistry()),
                )
            }

            App(rootComponent)
        }
    }
}

