package band.effective.office.tv

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.window.application
import band.effective.office.tv.di.KoinInitializer
import band.effective.office.tv.environment.Environment
import band.effective.office.tv.logger.LoggerInitializer
import band.effective.office.tv.root.RootComponent
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import band.effective.office.tv.core.ui.Res
import band.effective.office.tv.core.ui.app_title

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

