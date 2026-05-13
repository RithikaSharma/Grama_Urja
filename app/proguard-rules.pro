# Keep Supabase / Ktor / kotlinx-serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.gramaurja.app.**$$serializer { *; }
-keepclassmembers class com.gramaurja.app.** { *** Companion; }
-keepclasseswithmembers class com.gramaurja.app.** { kotlinx.serialization.KSerializer serializer(...); }
