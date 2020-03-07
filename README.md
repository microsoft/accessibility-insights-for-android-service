<!--
Copyright (c) Microsoft Corporation. All rights reserved.
Licensed under the MIT License.
-->

## Accessibility Insights for Android<sup>TM</sup> Service

[![Build Status](https://dev.azure.com/accessibility-insights/accessibility-insights-for-android-service/_apis/build/status/Accessibility%20Insights%20for%20Android%20Service%20CI?branchName=master)](https://dev.azure.com/accessibility-insights/accessibility-insights-for-android-service/_build/latest?definitionId=35&branchName=master)

Accessibility Insights for Android Service is a service for Android that helps in assessing the accessibility of Android applications.

<!-- ### Running the service -->

### Building the code

#### Prerequisites

* Download and install [Android Studio](https://developer.android.com/studio/)

    * If you plan on using Android Studio's emulator, make sure "Android Virtual Device" is checked in the download setup dialog
    * Selecting the standard installation (recommended) will also install Android SDK tools
    
* Emulator (optional, if not using a physical device):

    * From Android Studio, start Android Virtual Device (AVD) Manager: **Tools** > **AVD Manager**
    * Select **Create Virtual Device**. *This may require downloading the selected system image. If possible, select a newer device, such as Pixel 3 API 29 .*

***Note:** We do not recommend using a personal Android device for testing and development; we recommend using a testing device or emulator. Software under development may not yet be fully secure, and many development tools require permissions that could present a potential risk to your device and data.*

#### Setup (Windows, Linux, Mac)

*While the instructions for Windows, Linux, and Mac are the same, the instructions below are written with Windows-style file paths.*

1. Fork and clone the repository

1. Obtain a copy of axe-android.jar from [axe-android](https://github.com/dequelabs/axe-android) and move it to `accessibility-insights-android-service\AccessibilityInsightsForAndroidService\app\libs`:
    * Clone [axe-android](https://github.com/dequelabs/axe-android) in a separate folder
    * Open the axe-android project in Android Studio and wait for sync to complete
    * From the **Build** menu, select **Make Project**
    * Copy resulting axe-android.jar from `build\libs` in axe-android to `accessibility-insights-android-service\AccessibilityInsightsForAndroidService\app\libs` *(You may have to create the libs folder yourself)*
    
1. Open the project with Android Studio
    * This must be the Android project `AccessibilityInsightsForAndroidService` inside the repo, **not** the top-level `accessibility-insights-for-android-service` folder
    
1. The project will automatically sync and build
    * See [Known issues](#known-issues) below for common issues
    
1. Change the launch options in Android Studio: 
    * Under the **Run** menu, select **Edit configurations...**
		* Ensure that the **app** is selected in the left navigation pane
		* Under **General** > **Launch Options**, change **Launch** from "Default Activity" to "Nothing"
		* Select **Apply**, then **Ok**
    
1. 	Run the app from Android Studio (this will install the APK onto the emulator). The app won't show up in the list of programs--it shows up as a downloaded accessibility service. It will be off by default. To activate it:
    * Open the Settings app in the emulator. *If you have trouble opening the emulator, see [Unable to start emulator](#unable-to-start-emulator) under [Known Issues](#known-issues) below*
    * Scroll down and select **Accessibility**
    * Select "Accessibility Insights for Android Service" under **Downloaded services**
    * Toggle on **Use service** to enable the service
    * Select **Allow** on the resulting dialog to grant the necessary permissions
    * An "Exposing sensitive info during casting/recording" dialog should appear. Select **Start now** 
    
#### Known issues

##### Gradle sync fails

Restarting Android Studio and waiting for everything to load before building the project could solve the issue

##### SDK setup issues

* Error message: Failed to install the following Android SDK packages as some licences have not been accepted.

  * Select the "Install missing SDK package(s)" link at the end of the error message. Follow the prompt to agree to license terms and install missing SDK tools

##### Unable to start emulator

* **(Windows)** Error message: Intel HAXM is required to run this AVD. VT-x is disabled in BIOS. Enable VT-x in your BIOS security settings (refer to documentation for your computer).

  * Virtualization might not be enabled on your computer. To check if it is, open Task Manager and select the Performance tab. If "Virtualization" is disabled, [follow these instructions to enable virtualization in your BIOS](https://www.howtogeek.com/213795/how-to-enable-intel-vt-x-in-your-computers-bios-or-uefi-firmware/). The option to enable virtualization might be under "Security settings"

  * Sometimes the issue is caused by Hyper-V blocking other virtualization apps, so you could also try to turn off Hyper-V.

* **(Linux)** Error message: KVM is required to run this AVD. /dev/kvm device: permission denied. Grant current user access to /dev/kvm

  * Open a Terminal window and run the following:
    ```bash
    sudo apt install qemu-kvm
    sudo adduser $USER kvm
    ```
  * Restart your machine

<!-- ### More Information -->

<!-- ### Testing -->

<!-- ## Data/Telemetry -->

## Reporting security vulnerabilities

If you believe you have found a security vulnerability in this project, please follow [these steps](https://technet.microsoft.com/en-us/security/ff852094.aspx) to report it. For more information on how vulnerabilities are disclosed, see [Coordinated Vulnerability Disclosure](https://technet.microsoft.com/en-us/security/dn467923).

<!-- ## FAQ -->

## Contributing

All contributions are welcome! Please read through our guidelines on [contributions](./CONTRIBUTING.md) to this project.

## Code of Conduct

Please read through our [Code of Conduct](./CODE_OF_CONDUCT.md) to this project.

Android is a trademark of Google LLC.
