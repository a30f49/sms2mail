# SMS2Mail App ProGuard Rules

# 保持调试信息
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 保持应用主要类
-keep class com.example.sms2mail.MainActivity { *; }
-keep class com.example.sms2mail.SmsReceiver { *; }
-keep class com.example.sms2mail.SmsMonitorService { *; }
-keep class com.example.sms2mail.EmailService { *; }

# JavaMail API 相关规则
-keep class javax.mail.** { *; }
-keep class javax.activation.** { *; }
-keep class com.sun.mail.** { *; }
-dontwarn javax.mail.**
-dontwarn javax.activation.**
-dontwarn com.sun.mail.**

# Android 组件
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.app.Application

# 保持序列化类
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 移除日志
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}