#!/usr/bin/env bash
emulator_config="system-images;android-$ANDROID_API_LEVEL;google_apis;x86_64"
emulator_name="emulator_api_$ANDROID_API_LEVEL"

# Install AVD files
echo "y" | $ANDROID_HOME/tools/bin/sdkmanager --install "$emulator_config"
# Create emulator
echo "no" | $ANDROID_HOME/tools/bin/avdmanager create avd -n "$emulator_name" -k "$emulator_config" --force

$ANDROID_HOME/emulator/emulator -list-avds

echo "Starting emulator"

# Start emulator in background
nohup $ANDROID_HOME/emulator/emulator -avd "$emulator_name" -no-snapshot -no-boot-anim > /dev/null 2>&1 &
$ANDROID_HOME/platform-tools/adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed | tr -d '\r') ]]; do sleep 1; done; input keyevent 82'
sleep 30 # pause to wait for the emulator to finish booting

$ANDROID_HOME/platform-tools/adb devices
echo "Emulator started"

$ANDROID_HOME/platform-tools/adb install app-debug.apk
$ANDROID_HOME/platform-tools/adb shell appops set com.microsoft.accessibilityinsightsforandroidservice PROJECT_MEDIA allow
$ANDROID_HOME/platform-tools/adb shell settings put secure enabled_accessibility_services com.microsoft.accessibilityinsightsforandroidservice/com.microsoft.accessibilityinsightsforandroidservice.AccessibilityInsightsForAndroidService
sleep 10 # pause to let the accessibility service start
