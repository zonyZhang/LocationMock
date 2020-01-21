-keepattributes *Annotation*
-keepattributes *JavascriptInterface*

# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses
-verbose

-dontpreverify

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers public class * {
   public void onEvent*(***);
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
-dontwarn android.app.**

-ignorewarnings

# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-keep class android.support.v7.widget.RecyclerView{*;}
-keep class android.support.v7.widget.RecyclerView$Adapter{*;}
-keep class android.support.v7.widget.RecyclerView$*{*;}
-keep class android.support.v7.widget.RecyclerView$LayoutManager{*;}
-keep class android.support.v7.widget.GridLayoutManager{*;}
-keep class android.support.v7.widget.LinearLayoutManager{*;}
-keep class android.support.v4.app.*{*;}
-keep class android.support.v4.app.Fragment{*;}
-keep class android.support.v4.app.FragmentActivity{*;}
-keep class android.support.v4.app.NotificationCompat{*;}
-keep class android.support.v4.app.FragmentTabHost{*;}
-keep class android.support.v4.app.FragmentController{*;}
-keep class android.support.v4.app.FragmentStatePagerAdapter{*;}
-keep class android.support.v4.app.FragmentManager{*;}
-keep class android.support.v4.app.FragmentTransaction{*;}
-keep class android.support.v4.view.animation.FastOutSlowInInterpolator{*;}

-keep class android.support.v4.view.ViewPager{*;}
-keep class android.support.v4.view.ViewPager$OnPageChangeListener{*;}
-keep interface android.support.v4.view.ViewPager$OnPageChangeListener{*;}
-keep class android.support.v4.view.PagerAdapter{*;}
-keep class android.support.v4.view.ViewCompat{*;}
-keep class android.support.v4.view.MotionEventCompat{*;}

-keep class android.support.v4.app.Fragment$SavedState
-keep class android.support.v4.widget.ViewDragHelper{*;}
-keep class android.support.v4.widget.ViewDragHelper$*{*;}
-keep class android.support.v4.util.ArrayMap{*;}
-keep class android.support.v4.content.ContextCompat{*;}
-keep class android.support.v4.view.animation.PathInterpolatorCompat{*;}
-keep class android.support.v4.util.SparseArrayCompat{*;}

-keep class com.zony.mock.domain.*{*;}

#amap start
-dontwarn com.amap.api.**
-dontwarn com.a.a.**
-dontwarn com.autonavi.**
-keep class com.amap.api.**  {*;}
-keep class com.autonavi.**  {*;}
-keep class com.a.a.**  {*;}
#amap end

#AutoPackerTag remove all logs when release start
-assumenosideeffects class android.util.Log {
    public static *** e(...);
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
}

-assumenosideeffects class com.cdel.grassland.util.LogUtil {
    public static *** e(...);
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
}
#AutoPackerTag remove all logs when release end