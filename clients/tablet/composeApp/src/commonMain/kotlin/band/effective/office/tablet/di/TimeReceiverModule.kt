package band.effective.office.tablet.di

import org.koin.core.module.Module

/**
 * Supplies the platform's [band.effective.office.tablet.time.TimeReceiver]. It is an expect module
 * because only the Android implementation takes a `Context`, and only the Android graph has one.
 */
expect fun timeReceiverModule(): Module
