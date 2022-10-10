-keepattributes SourceFile, LineNumberTable       # Allows us keep file names and line numbers in crash logs
-keep public class * extends java.lang.Exception  # Keep custom exceptions.

#tell proguard to spare our model field names, lest the app crashes due to NPEs
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation interface com.google.gson.annotations.SerializedName
# Keep class names of Hilt
-keep class dagger.hilt.** { *; }