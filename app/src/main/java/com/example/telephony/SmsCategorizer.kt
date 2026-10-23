package com.example.telephony

import java.util.regex.Pattern

object SmsCategorizer {

    private val OTP_REGEX = Pattern.compile("\\b(\\d{4,8})\\b")

    fun extractOtp(body: String): String? {
        val lower = body.lowercase()
        val isOtpContext = lower.contains("code") ||
                lower.contains("otp") ||
                lower.contains("verification") ||
                lower.contains("verify") ||
                lower.contains("security code") ||
                lower.contains("pin") ||
                lower.contains("পাসওয়ার্ড") ||
                lower.contains("কোড")

        if (isOtpContext) {
            val matcher = OTP_REGEX.matcher(body)
            if (matcher.find()) {
                return matcher.group(1)
            }
        }
        return null
    }

    fun categorize(sender: String, body: String): String {
        val lowerBody = body.lowercase()
        val lowerSender = sender.lowercase()

        // 1. OTP check
        if (extractOtp(body) != null || lowerBody.contains("one time password") || lowerBody.contains("verification code")) {
            return "OTP"
        }

        // 2. Transaction / Banking check
        val isBankOrFinancial = lowerSender.contains("bank") ||
                lowerSender.contains("bkash") ||
                lowerSender.contains("nagad") ||
                lowerSender.contains("rocket") ||
                lowerSender.contains("upay") ||
                lowerSender.contains("visa") ||
                lowerSender.contains("mastercard")

        val hasTxKeywords = lowerBody.contains("cash in") ||
                lowerBody.contains("cash out") ||
                lowerBody.contains("debited") ||
                lowerBody.contains("credited") ||
                lowerBody.contains("balance") ||
                lowerBody.contains("trxid") ||
                lowerBody.contains("transferred") ||
                lowerBody.contains("a/c") ||
                lowerBody.contains("ac no") ||
                lowerBody.contains("লেনদেন") ||
                lowerBody.contains("ব্যালেন্স")

        if (isBankOrFinancial || hasTxKeywords) {
            return "TRANSACTION"
        }

        // 3. Promotion / Offers
        val hasPromoKeywords = lowerBody.contains("offer") ||
                lowerBody.contains("discount") ||
                lowerBody.contains("sale") ||
                lowerBody.contains("cashback") ||
                lowerBody.contains("mb pack") ||
                lowerBody.contains("gb internet") ||
                lowerBody.contains("dial *") ||
                lowerBody.contains("promo") ||
                lowerBody.contains("অফার") ||
                lowerBody.contains("ডিসকাউন্ট") ||
                lowerBody.contains("রিচার্জ")

        if (hasPromoKeywords || lowerSender.contains("offer") || lowerSender.contains("promo")) {
            return "PROMOTION"
        }

        return "PERSONAL"
    }
}
