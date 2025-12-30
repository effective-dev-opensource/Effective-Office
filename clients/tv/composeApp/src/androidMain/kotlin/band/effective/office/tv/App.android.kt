package band.effective.office.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import band.effective.office.tv.root.RootComponent
import com.arkivanov.decompose.defaultComponentContext

class AppActivity : ComponentActivity() {

    private lateinit var rootComponent: RootComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        rootComponent = RootComponent(componentContext = defaultComponentContext())

        setContent {
            App(rootComponent)
        }
    }
}


