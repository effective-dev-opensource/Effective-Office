package band.effective.office.tablet.di

import org.koin.core.module.Module

/**
 * Supplies the platform's [band.effective.office.tablet.time.TimeReceiver]. Only the Android one
 * takes anything — a `Context` — and only the Android graph has one, which is the whole reason
 * this is an expect module rather than a `single { }` in [appModule].
 *
 * Same shape as `settingsStoreModule()` in `core:domain`.
 */
expect fun timeReceiverModule(): Module
