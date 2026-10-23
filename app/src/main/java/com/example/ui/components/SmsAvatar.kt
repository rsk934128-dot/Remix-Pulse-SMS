package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CategoryOtpColor
import com.example.ui.theme.CategoryPromoColor
import com.example.ui.theme.CategoryTxColor

val AvatarPalette = listOf(
    Color(0xFF2563EB),
    Color(0xFF7C3AED),
    Color(0xFF059669),
    Color(0xFFD97706),
    Color(0xFFDC2626),
    Color(0xFF0284C7)
)

@Composable
fun SmsAvatar(
    nameOrAddress: String,
    category: String,
    colorSeed: Int = 0,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val bgColor = when (category) {
        "OTP" -> CategoryOtpColor
        "TRANSACTION" -> CategoryTxColor
        "PROMOTION" -> CategoryPromoColor
        else -> AvatarPalette[Math.abs(colorSeed) % AvatarPalette.size]
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        when (category) {
            "OTP" -> {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security OTP",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.5f)
                )
            }
            "TRANSACTION" -> {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = "Bank Transaction",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.5f)
                )
            }
            "PROMOTION" -> {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = "Promotional Offer",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.5f)
                )
            }
            else -> {
                val initial = nameOrAddress.trim().firstOrNull()?.uppercaseChar()
                if (initial != null && (initial.isLetter() || initial.isDigit())) {
                    Text(
                        text = initial.toString(),
                        color = Color.White,
                        fontSize = (size.value * 0.42f).sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Contact",
                        tint = Color.White,
                        modifier = Modifier.size(size * 0.5f)
                    )
                }
            }
        }
    }
}
