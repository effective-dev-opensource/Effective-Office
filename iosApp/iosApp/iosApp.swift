import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate

    init() {
        Initializers().doInit()
    }

    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}
