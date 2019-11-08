# SlidingBottomSheet

This app includes has a google map that shows the near-by events based on user current location
which we are getting from LocationViewModel class. On particular POI selection application shows
details which include the title, description and the list of images of those events. It also
includes the feature to show directions in the form of Polyline between POI and user current
location and if your current location is within the 5 km of the selected event than notification
pop up with details.

* __Register your app on Google console with the same package name and insert MAP_API_kEY in
keys.properties file to run the application.__

# Demo
![Sliding Up](screenshots/sliding_up_video.gif)

## Languages, libraries and tools used

* __[Kotlin](https://developer.android.com/kotlin)__
* __[Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)__
* __[Koin](https://github.com/InsertKoinIO/koin)__
* __[Coil](https://coil-kt.github.io/coil/getting_started/)__
* __[Google Map](https://developers.google.com/maps/documentation/android-sdk/intro)__
* __[Android Material Design](https://material.io/components/)__
* __[Android Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html)__

Above Features are used to make code simple, generic, understandable, clean and easily maintainable for future development.
Especially **Koin** for dependency injection and **Kotlin Coroutines** for asynchronous API call.

### BottomSheetBehavior
* This class is also in Java as this class includes the code for **Resizing map with sliding BottomSheet in Android**.
As Android’s BottomSheetBehavior doesn’t provide a middle state/ middle anchor. It only supports completely-expanded
and completely-collapsed states.

# Prerequisites
* __Android Studio 3.5__
* __Gradle version 3.5.1__
* __Kotlin version 1.3.50__
* __Android Device with USB Debugging Enabled__


# Built With
* __[Android Studio](https://developer.android.com/studio/index.html)__ - The Official IDE for Android
* __[Kotlin](https://developer.android.com/kotlin)__ - Language used to build the application
* __[Gradle](https://gradle.org)__ - Build tool for Android Studio
