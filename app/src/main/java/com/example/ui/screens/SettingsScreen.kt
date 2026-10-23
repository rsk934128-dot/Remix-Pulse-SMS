package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.auth.GoogleSignInButton
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDefaultSms: Boolean,
    hasTelephonyPermissions: Boolean,
    language: String,
    currentUser: FirebaseUser?,
    isCloudSyncing: Boolean,
    onToggleLanguage: () -> Unit,
    onRequestDefaultSms: () -> Unit,
    onRequestPermissions: () -> Unit,
    onTriggerCloudSync: () -> Unit,
    onSignOut: () -> Unit,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "BN") "সেটিংস ও ক্লাউড সিঙ্ক" else "Settings & Cloud Sync",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Firebase Cloud Sync & Google Auth Card
            Text(
                text = if (language == "BN") "ফায়ারবেস ক্লাউড ব্যাকআপ ও গুগল অ্যাকাউন্ট" else "Firebase Cloud Backup & Google Account",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentUser != null) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (currentUser != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentUser.displayName ?: "Google User",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currentUser.email ?: "",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFDCFCE7))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Connected", fontSize = 11.sp, color = Color(0xFF166534), fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Firestore: ai-studio-android-pulsesms-d91f8c0e-d0eb-454c-a3c4-23804c092a8d (us-west1)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onTriggerCloudSync,
                                enabled = !isCloudSyncing,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("trigger_cloud_sync_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isCloudSyncing) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (language == "BN") "এখনই সিঙ্ক করুন" else "Sync Now", fontSize = 12.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = onSignOut,
                                modifier = Modifier.testTag("sign_out_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == "BN") "লগআউট" else "Sign Out", fontSize = 12.sp)
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == "BN") "ফায়ারবেস ক্লাউড ব্যাকআপ" else "Firebase Cloud Backup",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (language == "BN") "আপনার সকল এসএমএস ও চ্যাট ক্লাউডে নিরাপদে সেভ রাখতে গুগল সাইন-ইন করুন।"
                                    else "Sign in with Google to securely back up and sync messages across devices.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        GoogleSignInButton(
                            onAuthSuccess = onAuthSuccess,
                            onAuthError = onAuthError
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Default SMS status
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDefaultSms) Color(0xFFECFDF5) else Color(0xFFFEF3C7)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDefaultSms) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isDefaultSms) Color(0xFF059669) else Color(0xFFD97706)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isDefaultSms) {
                                if (language == "BN") "সিস্টেম ডিফল্ট এসএমএস অ্যাপ (সক্রিয়)" else "Default SMS App (Active)"
                            } else {
                                if (language == "BN") "ডিফল্ট এসএমএস হিসেবে সেট করা নেই" else "Not Default SMS App"
                            },
                            fontWeight = FontWeight.Bold,
                            color = if (isDefaultSms) Color(0xFF065F46) else Color(0xFF92400E)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (language == "BN") {
                            "Android 10+ রোল ম্যানেজারের মাধ্যমে ডিফল্ট এসএমএস অ্যাপ সেট করলে অ্যান্ড্রয়েডের সকল ইনকামিং ও আউটগোয়িং এসএমএস সরাসরি অ্যাক্সেস করা যায়।"
                        } else {
                            "Setting as Default SMS handler enables full incoming SMS interception, carrier messaging, and status reporting."
                        },
                        fontSize = 12.sp,
                        color = if (isDefaultSms) Color(0xFF047857) else Color(0xFF78350F)
                    )

                    if (!isDefaultSms) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onRequestDefaultSms,
                            modifier = Modifier.testTag("settings_set_default_sms_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (language == "BN") "এখনই ডিফল্ট করুন" else "Set as Default Now")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Permissions list
            Text(
                text = if (language == "BN") "অ্যান্ড্রয়েড পারমিশন স্টেটাস" else "Android Permissions Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            val permissionsList = listOf(
                Pair("SEND_SMS", if (language == "BN") "সিম কার্ড দিয়ে এসএমএস পাঠানো" else "Send SMS via Carrier SIM"),
                Pair("RECEIVE_SMS", if (language == "BN") "রিয়েল-টাইমে ইনকামিং এসএমএস রিসিভ করা" else "Intercept Live Incoming SMS"),
                Pair("READ_SMS", if (language == "BN") "ইনবক্স মেসেজ পড়ার অনুমতি" else "Read SMS Inbox"),
                Pair("POST_NOTIFICATIONS", if (language == "BN") "নতুন মেসেজের পুশ নোটিফিকেশন" else "Post SMS Notifications")
            )

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    permissionsList.forEachIndexed { index, (perm, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = perm, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(
                                imageVector = if (hasTelephonyPermissions) Icons.Default.CheckCircle else Icons.Default.Security,
                                contentDescription = null,
                                tint = if (hasTelephonyPermissions) Color(0xFF059669) else Color(0xFFF59E0B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        if (index < permissionsList.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }

                    if (!hasTelephonyPermissions) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onRequestPermissions,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_grant_permissions_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (language == "BN") "অনুমতিগুলোর জন্য অনুরোধ করুন" else "Request Missing Permissions")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Language Selector
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == "BN") "অ্যাপের ভাষা" else "App Language",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (language == "BN") "বর্তমান: বাংলা (Bangla)" else "Current: English",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onToggleLanguage,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (language == "BN") "Switch to English" else "বাংলায় পরিবর্তন")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Architecture details
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == "BN") "টেকনোলজি স্ট্যাক ও ফায়ারবেস ব্যাকএন্ড" else "Tech Stack & Firebase Backend",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Google Firebase Auth (Jetpack Credential Manager)\n• Cloud Firestore (us-west1 Enterprise Edition)\n• Jetpack Compose Material 3 UI\n• Kotlin Coroutines & Flow\n• Room Database (Offline-First Local Persistence)\n• Telephony API (SmsManager & BroadcastReceiver)\n• Intelligent Categorization (OTP, Bank, Promo)\n• Socket.io & Cross-Platform Web Sync Simulator",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
