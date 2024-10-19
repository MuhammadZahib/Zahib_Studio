
Project Title: Zahib Studio (Image Capture and Upload App)
 

Table of Contents
Overview
Features
Technologies Used
Setup Instructions
Running the App
Contributing
License
Overview
This project is an Android application that continuously captures images from the device's camera and uploads them to a server using Kotlin Coroutines. The app follows the MVVM architecture and utilizes WorkManager for managing background uploads.

Features
Continuous image capture using CameraX API
Upload images to a server with retry and backoff strategies
Background processing of uploads using WorkManager
Optimized image handling to minimize network resource usage
Technologies Used
Kotlin: Programming language for Android development
Android Jetpack: Libraries for managing UI, background tasks, and architecture
Retrofit: Library for making network requests
WorkManager: API for managing background work
Coroutines: For asynchronous programming
CameraX: Simplified camera API for Android

Setup Instructions
Clone the repository:
git clone https://github.com/MuhammadZahib/Zahib_Studio.git
cd your-repo
Open the project in Android Studio.

Make sure you have the latest version of the Android SDK installed.

Add your server endpoint URL in the appropriate place in your code (e.g., in the ApiService interface).

Running the App
Connect your Android device or start an emulator.
Select the appropriate device in Android Studio.
Click on the "Run" button or use the command
Contributing
Contributions are welcome! Please fork the repository and submit a pull request for any enhancements or bug fixes.

License
This project is licensed under the MIT License.


Short Documentation
Project Documentation: Image Capture and Upload App

Overview
This document outlines the approach taken in developing the Image Capture and Upload App, focusing on coroutine management, optimization, and concurrency handling.

Coroutine Management
Use of ViewModel: The app utilizes ViewModel to manage UI-related data in a lifecycle-conscious way. This ensures that UI components are not recreated unnecessarily and can retain their state across configuration changes.
ViewModelScope: Coroutine work is launched within the viewModelScope, which automatically cancels any ongoing work when the ViewModel is cleared, preventing memory leaks.
Optimization Techniques
Image Resizing: Before uploading images, they are resized to reduce file size without compromising quality. This is crucial for minimizing network usage and speeding up uploads.
Controlled Frequency of Image Capture: The app captures images at a controlled rate (e.g., every second) to limit the number of images processed and uploaded, which helps manage device resources efficiently.
Concurrency Handling
WorkManager for Background Tasks: The app uses WorkManager to handle image uploads, ensuring that uploads are performed even when the app is minimized or closed. This also allows for scheduling retries in case of failures.
Limit Concurrent Uploads: By configuring WorkManager, the app limits the number of concurrent uploads to 3 at a time. This prevents overwhelming the network and device resources, ensuring smooth operation.
Exponential Backoff for Retries: The WorkManager's backoff criteria are set to exponential, which helps to manage retries effectively after upload failures, reducing the likelihood of continuous failures during network issues.
Conclusion
This project showcases a practical implementation of capturing and uploading images in an Android application, utilizing modern architectural practices and efficient resource management techniques. For more details, please refer to the README and explore the codebase.
