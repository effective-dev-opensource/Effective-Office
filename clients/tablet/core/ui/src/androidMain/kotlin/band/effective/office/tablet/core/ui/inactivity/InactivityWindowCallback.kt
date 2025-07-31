package band.effective.office.tablet.core.ui.inactivity

import android.view.KeyEvent
import android.view.MotionEvent
import android.view.Window

/**
 * A Window.Callback wrapper that intercepts touch and key events to track user activity.
 * It preserves the original Window.Callback behavior by delegating to it after intercepting events.
 *
 * @param original The original Window.Callback to delegate to.
 */
class InactivityWindowCallback(private val original: Window.Callback) : Window.Callback by original {

    /**
     * Intercepts touch events and notifies the InactivityManager before delegating to the original callback.
     *
     * @param event The touch event.
     * @return The result of the original callback's dispatchTouchEvent method.
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        // Notify the InactivityManager that a user interaction has occurred
        InactivityManager.onUserInteraction()
        
        // Delegate to the original callback
        return original.dispatchTouchEvent(event)
    }

    /**
     * Intercepts key events and notifies the InactivityManager before delegating to the original callback.
     *
     * @param event The key event.
     * @return The result of the original callback's dispatchKeyEvent method.
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // Notify the InactivityManager that a user interaction has occurred
        InactivityManager.onUserInteraction()
        
        // Delegate to the original callback
        return original.dispatchKeyEvent(event)
    }
}