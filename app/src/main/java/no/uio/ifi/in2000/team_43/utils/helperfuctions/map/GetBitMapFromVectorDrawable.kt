package no.uio.ifi.in2000.team_43.utils.helperfuctions.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.createBitmap

fun getBitmapFromVectorDrawable(context: Context, drawableResId: Int): Bitmap {
    val drawable: Drawable = AppCompatResources.getDrawable(context, drawableResId)
        ?: throw IllegalArgumentException("Drawable not found")

    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 24
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 24

    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}