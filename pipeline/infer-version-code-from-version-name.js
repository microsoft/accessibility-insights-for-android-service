// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
const process = require('process');
const versionName = process.env['APK_VERSION_NAME'];

if (versionName == null) {
    console.error('APK_VERSION_NAME env var not set!');
    process.exit(1);
}

const versionNameRegex = /^(\d+)\.(\d+)\.(\d+)$/;
const versionNameMatches = versionNameRegex.exec(versionName);
if (versionNameMatches == null) {
    console.error(`APK_VERSION_NAME ${versionName} not in expected format (1.2.3)`);
    process.exit(1);
}

const [major, minor, patch] = versionNameMatches.slice(1).map(component => parseInt(component));
if (`${major}.${minor}.${patch}` !== versionName) {
    console.error(`APK_VERSION_NAME ${versionName} has extra leading zeros (should be ${major}.${minor}.${patch})`)
    process.exit(1);
}

if (major > 99 || minor > 99 || patch > 999) {
    console.error(`APK_VERSION_NAME ${versionName} has an oversized component; it must fit in format xx.yy.zzz`);
    process.exit(1);
}

// for version x.y.z, versionCode is xxyyzzz
const versionCode = (major * 100000) + (minor * 1000) + patch;
console.log(`Success! Inferred version code: ${versionCode}`);

// This is the magic syntax to set an output variable in Azure Pipelines
console.log(`##vso[task.setvariable variable=APK_VERSION_CODE]${versionCode}`);
