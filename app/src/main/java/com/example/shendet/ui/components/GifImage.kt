package com.example.shendet.ui.components

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest

@Composable
fun GifImage(
    data: Any,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
    androidx.compose.foundation.Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context).data(data).build(),
            imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = modifier
    )
}
