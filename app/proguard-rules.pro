# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Retrofit interfaces
-keep interface com.userplay.bazar22.network.** { *; }

-keepnames class com.userplay.bazar22.models.SendBody
-keepclassmembers class com.userplay.bazar22.models.SendBody { *; }
-keep class com.google.gson.** { *; }

# Keep model classes
-keepclassmembers class com.userplay.bazar22.models.** {
    *;
}
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keepattributes Signature
-keepattributes *Annotation*

# Keep Retrofit related classes
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }


-keepattributes Signature
-keepattributes *Annotation*

-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**
