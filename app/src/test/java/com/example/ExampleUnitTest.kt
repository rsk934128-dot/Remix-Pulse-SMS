package com.example

import com.example.telephony.SmsCategorizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testOtpExtraction() {
        val message = "Your verification code is 849201. Do not share with anyone."
        val otp = SmsCategorizer.extractOtp(message)
        assertEquals("849201", otp)
        assertEquals("OTP", SmsCategorizer.categorize("Google", message))
    }

    @Test
    fun testTransactionCategorization() {
        val txMessage = "Cash In Tk 2,500.00 from 01712345678 successful. Fee Tk 0.00. Balance Tk 14,820.50. TrxID 9H87G65F."
        val category = SmsCategorizer.categorize("bKash", txMessage)
        assertEquals("TRANSACTION", category)
    }

    @Test
    fun testPromotionCategorization() {
        val promoMessage = "Special 5GB internet pack at 108 Tk for 7 days! Dial *121*5050# to activate."
        val category = SmsCategorizer.categorize("GP-Offers", promoMessage)
        assertEquals("PROMOTION", category)
    }

    @Test
    fun testPersonalCategorization() {
        val personalMessage = "কালকে বিকেল ৫টায় কফি শপে দেখা হচ্ছে তো?"
        val category = SmsCategorizer.categorize("+8801711223344", personalMessage)
        assertEquals("PERSONAL", category)
    }
}
