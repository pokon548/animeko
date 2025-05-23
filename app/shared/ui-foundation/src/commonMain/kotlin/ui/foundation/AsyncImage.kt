/*
 * Copyright (C) 2024-2025 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license, which can be found at the following link.
 *
 * https://github.com/open-ani/ani/blob/main/LICENSE
 */

package me.him188.ani.app.ui.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil3.Image
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePainter
import coil3.memory.MemoryCache
import coil3.network.NetworkFetcher
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import kotlinx.coroutines.Dispatchers
import me.him188.ani.utils.ktor.ScopedHttpClient
import me.him188.ani.utils.platform.currentPlatform
import me.him188.ani.utils.platform.isDesktop
import me.him188.ani.utils.platform.isIos

val LocalImageLoader = androidx.compose.runtime.compositionLocalOf<ImageLoader> {
    error("No ImageLoader provided")
}

@Stable
inline val defaultFilterQuality get() = if (currentPlatform().isDesktop()) FilterQuality.High else FilterQuality.Low

@Composable
fun AsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = LocalImageLoader.current,
    error: Painter? = null,
    fallback: Painter? = error,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = defaultFilterQuality,
    clipToBounds: Boolean = true,
) {
    return coil3.compose.AsyncImage(
        model = model,
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = Modifier.then(modifier),
        placeholder = null,
        error = error,
        fallback = fallback,
        onLoading = onLoading,
        onSuccess = onSuccess,
        onError = onError,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        clipToBounds = clipToBounds,
    )
}


@Composable
fun AsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = LocalImageLoader.current,
    placeholder: Painter? = null,
    error: Painter? = null,
    fallback: Painter? = error,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = defaultFilterQuality,
    clipToBounds: Boolean = true,
) {
    return coil3.compose.AsyncImage(
        model = model,
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier,
        placeholder = placeholder,
        error = error,
        fallback = fallback,
        onLoading = onLoading,
        onSuccess = onSuccess,
        onError = onError,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        clipToBounds = clipToBounds,
    )
}

@Composable
fun AsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = LocalImageLoader.current,
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = defaultFilterQuality,
    clipToBounds: Boolean = true,
) {
    return coil3.compose.AsyncImage(
        model = model,
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier,
        transform = transform,
        onState = onState,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        clipToBounds = clipToBounds,
    )
}

@OptIn(ExperimentalCoilApi::class)
fun createDefaultImageLoader(
    context: PlatformContext,
    client: ScopedHttpClient,
    config: ImageLoader.Builder.() -> Unit = {}
): ImageLoader {
    return ImageLoader.Builder(context).apply {
        if (!currentPlatform().isIos()) {
            crossfade(true)
        }
//        diskCache(DiskCache.Builder().apply {
//            maxSizeBytes(100 * 1024 * 1024)
//        }.build())

        coroutineContext(Dispatchers.Default)

        diskCachePolicy(CachePolicy.ENABLED)
        memoryCachePolicy(CachePolicy.ENABLED)
        memoryCache {
            MemoryCache.Builder().apply {
                maxSizeBytes(10 * 1024 * 1024)
            }.build()
        }
        networkCachePolicy(CachePolicy.ENABLED)

        components {
            add(SvgDecoder.Factory())

            add(
                NetworkFetcher.Factory(
                    networkClient = {
                        ScopedHttpClientNetworkFetcher(client)
                    },
                ),
            )
        }

        config()
    }.build()
}

expect fun ImageBitmap.asCoilImage(): Image
