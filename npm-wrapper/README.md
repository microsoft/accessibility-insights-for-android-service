## accessibility-insights-for-android-service-bin

This NPM package is a thin wrapper around the [Accessibility Insights for Android Service](../README.md) APK file. The package bundles a copy of the APK and exports the path of the bundled APK (and its associated NOTICE file) as the only library exports.

This wrapper package is intended for consumption by [Accessibility Insights for Android](https://github.com/microsoft/accessibility-insights-web); **we make no guarantees about its API stability or fitness for other purposes.**

### Versioning

This wrapper package's version matches the version of the bundled APK. It **does not use semantic versioning**; we reserve the right to make breaking API changes to the wrapper package without a major version update.

### Usage

```js
import { noticePath, apkPath, apkVersionName } from 'accessibility-insights-for-android-service-bin';

console.log(`Absolute path of the APK bundled with the package: ${apkPath}`);
console.log(`APK_VERSION_NAME of the bundled APK: ${apkVersionName}`);
console.log(`Absolute path of the NOTICE file for the APK: ${noticePath}`);
```

### Reporting security vulnerabilities

If you believe you have found a security vulnerability in this project, please follow [these steps](https://technet.microsoft.com/en-us/security/ff852094.aspx) to report it. For more information on how vulnerabilities are disclosed, see [Coordinated Vulnerability Disclosure](https://technet.microsoft.com/en-us/security/dn467923).

### Contributing

All contributions are welcome! Please read through our guidelines on [contributions](../CONTRIBUTING.md) to this project.

### Code of Conduct

Please read through our [Code of Conduct](../CODE_OF_CONDUCT.md) to this project.

Android is a trademark of Google LLC.