
### Just Music - Project Code Understanding

This is a Just Music app for Listening Music with great features.
By interacting with this app you can browse the latest albums,songs and many more

**Architecture and Pattern**
- Clean Architecture
- MVVM Architecture
- Single Activity
- Multi Module Implementation

**Included features**
- Home screen widgets
- Online and Offline browse albums and songs

### Evaluation of the Project Structure (Multi Module)

### App
The presentation layer contains components involved in showing information to the user which consists views and viewModels.

### Data
The data layer is responsible for selecting the proper data source for the domain layer. It contains the implementations of the repositories declared in the domain layer.

Components of data layer include:

- **Models**

  - **Dto Models**: Defines POJO of network responses.
  - **Entity Models**: Defines the schema of SQLite database by using Room.

- **Repositories**: Responsible for exposing data to the domain layer.
- **Mappers**: They perform data transformation between domain, dto and entity models.
- **Network**: This is responsible for performing network operations eg. defining API endpoints using[ ](https://square.github.io/retrofit/)[Retrofit](https://square.github.io/retrofit/).
- **Cache**: This is responsible for performing caching operations using[ ](https://developer.android.com/training/data-storage/room)[Room](https://developer.android.com/training/data-storage/room).
- **Data Source**: Responsible for deciding which data source (network or cache) will be used when fetching data.

### Domain
This is the core layer of the application. The domain layer is independent of any other layers thus domain models and business logic can be independent from other layers.This means that changes in other layers will have no effect on domain layer eg. screen UI (presentation layer) or changing database (data layer) will not result in any code change withing domain layer.

Components of domain layer include:
- **Models**: Defines the core structure of the data that will be used within the application.
- **Repositories**: Interfaces used by the use cases. Implemented in the data layer.
- **Use cases/Interacts**: They enclose a single action, like getting data from a database or posting to a service. They use the repositories to resolve the action they are supposed to do.

### Shared
Define or Implement similar Class, interface, Util Methods which may be required to use across the Multiple modules

###  Project Structure Appearance - In Android Studio
<img src="https://user-images.githubusercontent.com/10848154/182030106-49fd6657-b878-4066-84f7-1b0512edf5c5.png"/>

- ### **Tech Stack**
This project uses many of the popular libraries, plugins and tools for make a **Android Eco System**
### **Libraries.**
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Dependency Injection library.
- [Jetpack
  ](https://developer.android.com/jetpack)
    - [Android KTX](https://developer.android.com/kotlin/ktx.html) - Provide concise, idiomatic Kotlin to Jetpack and Android platform APIs.
    - [AndroidX](https://developer.android.com/jetpack/androidx) - Major improvement to the original Android[ ](https://developer.android.com/topic/libraries/support-library/index)[Support Library](https://developer.android.com/topic/libraries/support-library/index), which is no longer maintained.
    - [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle) - Perform actions in response to a change in the lifecycle status of another component, such as activities and fragments.
    - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Designed to store and manage UI-related data in a lifecycle conscious way. The ViewModel class allows data to survive configuration changes such as screen rotations.
    - [Room](https://developer.android.com/training/data-storage/room) - Provides an abstraction layer over SQLite used for offline data caching.
    - [Navigation Component](https://developer.android.com/guide/navigation/navigation-getting-started)-Component that allows easier implementation of navigation from simple button clicks to more complex patterns.
- [Retrofit](https://square.github.io/retrofit/) - Type-safe http client and supports coroutines out of the box.
- [Moshi](https://github.com/square/moshi) - Moshi is a modern JSON library for Android, Java and Kotlin. It makes it easy to parse JSON into Java and Kotlin classes.
- [OkHttp-Logging-Interceptor](https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/README.md) - Logs HTTP request and response data.
- [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) - Library Support for coroutines.
- [Flow](https://developer.android.com/kotlin/flow) - Flows are built on top of coroutines and can provide multiple values. A flow is conceptually a stream of data that can be computed asynchronously.
- [Material Design](https://material.io/develop/android/docs/getting-started/) - Build awesome beautiful UIs.
- [Coin](https://github.com/coil-kt/coil)- Image Library from loading images from the server and caching in memory.
- [ExoPlayer](https://github.com/google/ExoPlayer) - ExoPlayer is an application level media player for Android. It provides an alternative to Android’s MediaPlayer API for playing audio and video both locally and over the Internet.


### Multi Module Versioning file management

- Defined dependencies at independent gradle file, thus versioning is consistent across different modules,File name: **dependencies.gradle**

![Screen Shot 2565-07-31 at 21 37 24](https://user-images.githubusercontent.com/10848154/219452957-b1a62dc5-3413-4dfb-9353-e7b36a6fdcfc.png)

### How to update dependency version in multi module?

- Version can be update by opening Project Structure from Android studio, **Android Studio -> File -> Project Structure -> Suggestions**

![Screen Shot 2565-07-31 at 21 42 43](https://user-images.githubusercontent.com/10848154/182035449-4f63df17-af78-4a90-a83c-506fe0ddb07b.png)

- ### **What is Clean Architecture?**
A well planned architecture is extremely important for an app to scale and all architectures have one common goal- to manage the complexity of your app. This isn't something to be worried about in smaller apps however it may prove very useful when working on apps with longer development lifecycle and a bigger team.

Clean architecture was proposed by[ ](https://en.wikipedia.org/wiki/Robert_C._Martin)[Robert C. Martin](https://en.wikipedia.org/wiki/Robert_C._Martin) in 2012 in the[ ](http://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)[Clean Code Blog](http://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) and it follows the SOLID principle.

- ### **Questions & Answers**

|**Q1** |Why clean architecture used in this project|
| :- | :- |
|**Ans** .|<p>Clean architecture is used for better code maintainability and testability of the project .</p><p></p><p>But we have to agree sometime managing that architecture is kind of burden </p>|
|**Q2**|Why use a coil as an image library?|
|**Ans.**|<p>An image loading library for Android backed by Kotlin Coroutines.

### Disclaimer
- Complex architectures like the pure clean architecture can also increase code complexity since decoupling your code also means creating lots of data transformations(mappers) and models,that may end up increasing the learning curve of your code to a point where it would be better to use a simpler architecture like MVVM.
