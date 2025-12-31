package band.effective.office.tv.core.ui.di

import band.effective.office.tv.core.ui.image.createTvImageLoader
import coil3.ImageLoader
import coil3.PlatformContext
import org.koin.dsl.module

/**
 * Shared ImageLoader module for all TV features.
 * Use with parametersOf(PlatformContext) when resolving.
 */
val imageLoaderModule = module {
    single<ImageLoader> { (context: PlatformContext) ->
        createTvImageLoader(context)
    }
}

