package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun GestureGuideOverlay(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))
                    )
                )
                .border(2.dp, Color(0xFF00E5FF), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "DOKUNMATİK KONTROLLER",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Ekranda hiçbir tuş yoktur. Tüm hareketler jestlerle yapılır!",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                GestureItem(
                    icon = "👈 👉",
                    title = "Sola / Sağa Kaydır",
                    description = "Parçayı yatay olarak istediğin konuma sürükle"
                )

                Spacer(modifier = Modifier.height(10.dp))

                GestureItem(
                    icon = "🔄",
                    title = "Ekrana Dokun (Tap)",
                    description = "Parçayı saat yönünde döndürür"
                )

                Spacer(modifier = Modifier.height(10.dp))

                GestureItem(
                    icon = "👇",
                    title = "Aşağıya Sürükle",
                    description = "Yavaş Düşüş (Soft Drop) yapar"
                )

                Spacer(modifier = Modifier.height(10.dp))

                GestureItem(
                    icon = "⚡⬇️",
                    title = "Hızlı Aşağı Flick",
                    description = "Anında dibe vurur (Hard Drop) ve ekran titrer"
                )

                Spacer(modifier = Modifier.height(10.dp))

                GestureItem(
                    icon = "👆 / ✌️",
                    title = "Yukarı Kaydır / Çift Dokun",
                    description = "Mevcut parçayı beklemeye alır (Hold)"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "ANLADIM, OYNA!",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun GestureItem(
    icon: String,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x1F1E293B))
            .border(1.dp, Color(0x2238BDF8), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x3300E5FF))
        ) {
            Text(text = icon, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}
