package band.effective.office.tablet.core.ui.inactivity

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * ActivityLifecycleCallbacks implementation that hooks into all activities
 * and sets up the Window.Callback wrapper to track user inactivity.
 */
class InactivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // Get the original Window.Callback
        val originalCallback = activity.window.callback

        // Set our custom Window.Callback wrapper
        activity.window.callback = InactivityWindowCallback(originalCallback)
    }

    override fun onActivityStarted(activity: Activity) {
        // No implementation needed
    }

    override fun onActivityResumed(activity: Activity) {
        // Reset the inactivity timer when an activity is resumed
        InactivityManager.resetTimer()
    }

    override fun onActivityPaused(activity: Activity) {
        // No implementation needed
    }

    override fun onActivityStopped(activity: Activity) {
        // No implementation needed
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // No implementation needed
    }

    override fun onActivityDestroyed(activity: Activity) {
        // No implementation needed
    }

    companion object {
        /**
         * Registers the InactivityLifecycleCallbacks with the Application
         * and starts tracking user inactivity.
         *
         * @param application The Application instance.
         * @param timeoutMs The inactivity timeout in milliseconds. Default is 1 minute.
         * @param callback The callback to execute when inactivity is detected.
         */
        fun initialize(
            application: Application,
            timeoutMs: Long = InactivityManager.DEFAULT_INACTIVITY_TIMEOUT_MS,
            callback: (() -> Unit)? = null,
        ) {
            // Register the ActivityLifecycleCallbacks
            application.registerActivityLifecycleCallbacks(InactivityLifecycleCallbacks())

            // Start tracking inactivity
            InactivityManager.startTracking(timeoutMs) {
                // Execute the original callback
                callback?.invoke()
            }
        }
    }
}
