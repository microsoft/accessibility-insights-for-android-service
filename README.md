<!--
Copyright (c) Microsoft Corporation. All rights reserved.
Licensed under the MIT License.
-->

## Accessibility Insights for Android<sup>TM</sup> Service

[![Build Status](https://dev.azure.com/accessibility-insights/accessibility-insights-for-android-service/_apis/build/status/Accessibility%20Insights%20for%20Android%20Service%20CI?branchName=main)](https://dev.azure.com/accessibility-insights/accessibility-insights-for-android-service/_build/latest?definitionId=35&branchName=main)

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

1. Open the project with Android Studio
    * This must be the Android project `AccessibilityInsightsForAndroidService` inside the repo, **not** the top-level `accessibility-insights-for-android-service` folder
    
1. The project will automatically sync and build
    * See [Known issues](#known-issues) below for common issues
    
1. Change the launch options in Android Studio: 
    * Under the **Run** menu, select **Edit configurations...**
		* Ensure that the **app** is selected in the left navigation pane
		* Under **General** > **Launch Options**, change **Launch** from "Default Activity" to "Nothing"
		* Select **Apply**, then **Ok**

1. Run the app from Android Studio (this will install the APK onto the emulator). The app won't show up in the list of programs--it shows up as a downloaded accessibility service. It will be off by default. To activate it:
    * Open the Settings app in the emulator. *If you have trouble opening the emulator, see [Unable to start emulator](#unable-to-start-emulator) under [Known Issues](#known-issues) below*
    * Scroll down and select **Accessibility**
    * Select "Accessibility Insights for Android Service" under **Downloaded services**
    * Toggle on **Use service** to enable the service
    * Select **Allow** on the resulting dialog to grant the necessary permissions
    * An "Exposing sensitive info during casting/recording" dialog should appear. Select **Start now** 

#### Triggering requests in the browser

While the service is running, forward the emulated device port `62442` (set in the [ServerThread](https://github.com/microsoft/accessibility-insights-for-android-service/blob/main/AccessibilityInsightsForAndroidService/app/src/main/java/com/microsoft/accessibilityinsightsforandroidservice/ServerThread.java)) to the same port on your machine by running `adb forward tcp:62442 tcp:62442` from a command prompt.

A success will either print the port number back or print nothing at all.

Once the port forwarding is set up you can manually trigger requests by hitting [endpoints](https://github.com/microsoft/accessibility-insights-for-android-service/blob/main/AccessibilityInsightsForAndroidService/app/src/main/java/com/microsoft/accessibilityinsightsforandroidservice/RequestHandlerFactory.java) in your browser.
* **Config**
  * Navigate to [http://localhost:62442/AccessibilityInsights/config](http://localhost:62442/AccessibilityInsights/config) to view device configuration.
  * Returned data includes:
    * `deviceName` - the name of the device from the Android [`Build.MODEL`](https://developer.android.com/reference/android/os/Build#MODEL)
    * `packageName` - the package the associated root [`AccessibilityInfoNode`](https://developer.android.com/reference/android/view/accessibility/AccessibilityNodeInfo) comes from
    * `serviceVersion` - the version of the service

* **Raw Axe Results** (will soon be removed)
  * Navigate to [http://localhost:62442/AccessibilityInsights/result](http://localhost:62442/AccessibilityInsights/result) to view returned JSON results.
  * Returned data includes:
    * `axeConf` - the set [AxeConf](https://github.com/dequelabs/axe-android/blob/develop/src/main/java/com/deque/axe/android/AxeConf.java) object listing which rules will run
    * `axeContext` - the Context axe-android is running the rules on
      * `axeDevice` - the device the context was built on
      * `axeEventStream` - the AccessibilityEvent Stream since the last view refresh
      * `axeMetadata`
        *  `analysisTimestamp` - the timestamp at time of analysis
        *  `appIdentifier` - the Android PackageName
        *  `axeVersion` - implementation version of package
        *  `screenTitle` - title of current screen
      * `axeView` - the serializable view hierarchy at the time the context was built
      * `screenshot` - the screenshot at the time the Context was built (this will display as a wall of text BitMap in the browser)
    * `axeRuleResults` - an array of [AxeRuleResult](https://github.com/dequelabs/axe-android/blob/develop/src/main/java/com/deque/axe/android/AxeRuleResult.java) objects. Each has:
      * `axeViewId` - the ID of the view it's associated with (corresponds to a view listed above in `axeContext`)
      * `impact` - the severity of the issue
      * `props` - the properties used in determining the outcome
      * `ruleId` - the ID of the rule
      * `ruleSummary` - short summary of the rule.
      * `status` - the status of the rule (PASS, FAIL, etc)

* **Raw Axe and ATFA Results** (results version 2)
  * Navigate to [http://localhost:62442/AccessibilityInsights/result_v2](http://localhost:62442/AccessibilityInsights/result_v2) to view returned JSON results.
  * Returned data includes:
    * `AxeResults`
      * `axeConf` - the set [AxeConf](https://github.com/dequelabs/axe-android/blob/develop/src/main/java/com/deque/axe/android/AxeConf.java) object listing which rules will run
      * `axeContext` - the Context axe-android is running the rules on
        * `axeDevice` - the device the context was built on
        * `axeEventStream` - the AccessibilityEvent Stream since the last view refresh
        * `axeMetadata`
          *  `analysisTimestamp` - the timestamp at time of analysis
          *  `appIdentifier` - the Android PackageName
          *  `axeVersion` - implementation version of package
          *  `screenTitle` - title of current screen
        * `axeView` - the serializable view hierarchy at the time the context was built
        * `screenshot` - the screenshot at the time the Context was built (this will display as a wall of text BitMap in the browser)
      * `axeRuleResults` - an array of [AxeRuleResult](https://github.com/dequelabs/axe-android/blob/develop/src/main/java/com/deque/axe/android/AxeRuleResult.java) objects. Each has:
        * `axeViewId` - the ID of the view it's associated with (corresponds to a view listed above in `axeContext`)
        * `impact` - the severity of the issue
        * `props` - the properties used in determining the outcome
        * `ruleId` - the ID of the rule
        * `ruleSummary` - short summary of the rule.
        * `status` - the status of the rule (PASS, FAIL, etc)
    * `ATFARules` - an array of [AccessibilityHierarchyCheck](https://github.com/google/Accessibility-Test-Framework-for-Android/blob/master/src/main/java/com/google/android/apps/common/testing/accessibility/framework/AccessibilityHierarchyCheck.java) objects. Each has:
      * `class` - the name of the class for the check
      * `titleMessage` - the human-readable title of the check
      * `category` - the type of accessibility check (see [Category](https://github.com/google/Accessibility-Test-Framework-for-Android/blob/7ab5fdb5e2cb675edb752c0d0d9cae3986c0bb0c/src/main/java/com/google/android/apps/common/testing/accessibility/framework/AccessibilityCheck.java#L36))
      * `helpUrl` - the URL for a help article related to the check
      * `resultIdsAndMetadata` - a map providing possible result IDs and additional metadata about the check
    * `ATFAResults`- an array of [AccessibilityHierarchyCheckResult](https://github.com/google/Accessibility-Test-Framework-for-Android/blob/master/src/main/java/com/google/android/apps/common/testing/accessibility/framework/AccessibilityHierarchyCheckResult.java) objects. Each has:
      * `AccessibilityHierarchyCheckResult.element` - a [ViewHierarchyElement](https://github.com/google/Accessibility-Test-Framework-for-Android/blob/master/src/main/java/com/google/android/apps/common/testing/accessibility/framework/uielement/ViewHierarchyElement.java) object corresponding to the result 
      * `AccessibilityHierarchyCheckResult.resultId` - an ID grouping all results from a single class
      * `AccessibilityCheckResult.checkClass` - the name of the class for the check which reported the result
      * `AccessibilityCheckResult.type` - the status of the result (see [AccessibilityCheckResultType](https://github.com/google/Accessibility-Test-Framework-for-Android/blob/7ab5fdb5e2cb675edb752c0d0d9cae3986c0bb0c/src/main/java/com/google/android/apps/common/testing/accessibility/framework/AccessibilityCheckResult.java#L49))
      * `AccessibilityHierarchyCheckResult.metadata` - an object which implements [ResultMetadata](https://github.com/google/Accessibility-Test-Framework-for-Android/blob/master/src/main/java/com/google/android/apps/common/testing/accessibility/framework/ResultMetadata.java) containing additional information about the result
      
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

<!-- ## Trademarks -->

## Trademarks

This project may contain trademarks or logos for projects, products, or services. Authorized use of Microsoft trademarks or logos is subject to and must follow [Microsoft's Trademark & Brand Guidelines](https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks/usage/general). Use of Microsoft trademarks or logos in modified versions of this project must not cause confusion or imply Microsoft sponsorship. Any use of third-party trademarks or logos are subject to those third-party's policies.

<!-- ## FAQ -->

## Contributing

All contributions are welcome! Please read through our guidelines on [contributions](./CONTRIBUTING.md) to this project.

## Code of Conduct

Please read through our [Code of Conduct](./CODE_OF_CONDUCT.md) to this project.

Android is a trademark of Google LLC.
