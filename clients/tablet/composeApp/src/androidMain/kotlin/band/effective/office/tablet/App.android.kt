package band.effective.office.tablet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import band.effective.office.tablet.root.RootComponent
import com.arkivanov.decompose.defaultComponentContext

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val root = RootComponent(componentContext = defaultComponentContext())
        setContent { App(root) }
    }
}
