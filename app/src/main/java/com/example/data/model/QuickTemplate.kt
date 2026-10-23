package com.example.data.model

data class QuickTemplate(
    val id: String,
    val textBn: String,
    val textEn: String,
    val iconName: String = "chat"
)

object QuickTemplatesList {
    val defaultTemplates = listOf(
        QuickTemplate("1", "এখন একটু ব্যস্ত, পরে কল করছি।", "I'm busy right now, call you later.", "busy"),
        QuickTemplate("2", "ঠিক আছে, পেয়েছি!", "Got it, thanks!", "check"),
        QuickTemplate("3", "পৌঁছে গেছি!", "I have arrived safely!", "location"),
        QuickTemplate("4", "একটু পর দেখা হচ্ছে।", "See you in a bit.", "time"),
        QuickTemplate("5", "কল দিতে পারবেন?", "Can you please call me?", "call"),
        QuickTemplate("6", "ধন্যবাদ!", "Thank you very much!", "thumb")
    )
}
