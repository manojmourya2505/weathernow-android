# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepattributes InnerClasses

# kotlinx.serialization
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.manojmourya.weathernow.**$$serializer { *; }
-keepclassmembers class com.manojmourya.weathernow.** { *** Companion; }
-keepclasseswithmembers class com.manojmourya.weathernow.** { kotlinx.serialization.KSerializer serializer(...); }

# Room
-keep class * extends androidx.room.RoomDatabase
