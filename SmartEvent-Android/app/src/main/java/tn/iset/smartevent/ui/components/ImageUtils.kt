package tn.iset.smartevent.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.ByteArrayOutputStream
import tn.iset.smartevent.ui.theme.Navy900
import tn.iset.smartevent.ui.theme.White

// Lit une image choisie dans la galerie, la réduit (max 800 px) et la renvoie en base64 (JPEG).
// Ce texte est envoyé au backend et stocké dans la base de données.
fun uriToBase64(context: Context, uri: Uri, maxSize: Int = 800): String? {
    return try {
        val resolver = context.contentResolver

        // 1) lire seulement les dimensions
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (bounds.outWidth / sample > maxSize * 2 || bounds.outHeight / sample > maxSize * 2) sample *= 2

        // 2) décoder en réduit
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        var bmp = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) } ?: return null

        // 3) corriger la rotation (photos prises avec l'appareil)
        val orientation = resolver.openInputStream(uri)?.use {
            android.media.ExifInterface(it).getAttributeInt(
                android.media.ExifInterface.TAG_ORIENTATION,
                android.media.ExifInterface.ORIENTATION_NORMAL
            )
        } ?: android.media.ExifInterface.ORIENTATION_NORMAL
        val angle = when (orientation) {
            android.media.ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            android.media.ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            android.media.ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        if (angle != 0f) {
            val m = Matrix().apply { postRotate(angle) }
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
        }

        // 4) réduire à maxSize puis compresser en JPEG
        val scale = minOf(1f, maxSize.toFloat() / maxOf(bmp.width, bmp.height))
        if (scale < 1f) {
            bmp = Bitmap.createScaledBitmap(bmp, (bmp.width * scale).toInt(), (bmp.height * scale).toInt(), true)
        }
        val out = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 75, out)
        Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
    } catch (e: Exception) {
        null
    }
}

// Affiche une image base64 (ne dessine rien si data est vide ou invalide)
@Composable
fun Base64Image(data: String?, modifier: Modifier = Modifier) {
    if (data.isNullOrBlank()) return
    val bitmap = remember(data) {
        runCatching {
            val bytes = Base64.decode(data, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }.getOrNull()
    }
    if (bitmap != null) {
        Image(bitmap = bitmap, contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier)
    }
}

// Pastille ronde : photo de profil, sinon la première lettre du nom
@Composable
fun Avatar(name: String, photo: String?, size: Dp = 40.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Navy900),
        contentAlignment = Alignment.Center
    ) {
        Text(
            name.take(1).uppercase(),
            color = White, fontSize = (size.value * 0.4f).sp, fontWeight = FontWeight.Bold
        )
        Base64Image(photo, Modifier.size(size))
    }
}
