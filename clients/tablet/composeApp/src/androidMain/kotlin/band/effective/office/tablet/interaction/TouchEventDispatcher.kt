package band.effective.office.tablet.interaction

import android.view.MotionEvent
import android.view.Window
import band.effective.office.tablet.core.domain.orchestrator.EventOrchestrator
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Dispatches touch events to the EventOrchestrator by intercepting Window.Callback
 */
class TouchEventDispatcher(
    private val eventOrchestrator: EventOrchestrator
) {
    // The original window callback
    private var originalCallback: Window.Callback? = null
    
    // Install the touch event dispatcher on a window
    fun install(window: Window) {
        // Save the original callback
        originalCallback = window.callback
        
        // Set our custom callback that wraps the original
        window.callback = object : Window.Callback by originalCallback!! {
            override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                // Handle the touch event based on its action
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // User started touching the screen
                        eventOrchestrator.startUserInteraction()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        // User stopped touching the screen
                        eventOrchestrator.endUserInteraction()
                    }
                }
                
                // Pass the event to the original callback
                return originalCallback?.dispatchTouchEvent(event) ?: false
            }
        }
    }
    
    // Remove the touch event dispatcher
    fun uninstall(window: Window) {
        // Restore the original callback
        originalCallback?.let {
            window.callback = it
            originalCallback = null
        }
    }
}