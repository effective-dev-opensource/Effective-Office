package band.effective.office.elevator.expects

import band.effective.office.elevator.ui.uiViewController
import com.seiko.imageloader.component.ComponentRegistryBuilder
import com.seiko.imageloader.component.setupDefaultComponents
import io.github.aakira.napier.Napier
import okio.Path
import okio.Path.Companion.toPath
import platform.UIKit.UIAlertController
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.Foundation.*
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual fun showToast(message: String) {
    Napier.e { message }
    val controller = UIAlertController(null, null)
    controller.message = message
    controller.showViewController(uiViewController, null)
}

actual fun generateVibration(milliseconds: Long) {
//    AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
}

actual fun makeCall(phoneNumber: String) {
    val url = NSURL(string = "tel:$phoneNumber")
    UIApplication.sharedApplication.openURL(url)
}

actual fun pickTelegram(telegramNick: String) {
    if (telegramNick.isBlank()) return

    val appUrl = NSURL(string = "tg://resolve?domain=$telegramNick")
    val webUrl = NSURL(string = "https://t.me/$telegramNick")
    val application = UIApplication.sharedApplication

    val urlToOpen = if (application.canOpenURL(appUrl)) {
        appUrl
    } else {
        webUrl
    }

    dispatch_async(dispatch_get_main_queue()) {
        application.openURL(
            url = urlToOpen,
            options = emptyMap<Any?, Any?>(),
            completionHandler = null
        )
    }
}

actual fun pickSBP(phoneNumber: String) {
    val url = NSURL(string = "tel:$phoneNumber")
    UIApplication.sharedApplication.openURL(url)
}

actual fun ComponentRegistryBuilder.setupDefaultComponents() = this.setupDefaultComponents()

actual fun getImageCacheDirectoryPath(): Path {
    val cacheDir = NSSearchPathForDirectoriesInDomains(
        NSCachesDirectory,
        NSUserDomainMask,
        true
    ).first() as String
    return ("$cacheDir/media").toPath()
}