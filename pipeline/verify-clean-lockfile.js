// USAGE:
//
//     node verify-clean-lockfile.js
//
// Verifies that "git status" does not report any pending changed to gradle.lockfile
//
// This is a workaround for Dependabot not understanding natively how to update Gradle
// lockfiles; it serves as a reminder for humans looking at Dependabot PRs to update
// it manually.

const path = require('path');
const process = require('process');
const child_process = require('child_process');

const lockfilePath = path.join(__dirname, '..', 'AccessibilityInsightsForAndroidService', 'app', 'gradle.lockfile');

const gitStatusResult = child_process.execFileSync('git', ['status', '--porcelain=1', '--', lockfilePath]);
const isLockfileChanged = gitStatusResult.toString() !== '';

if (isLockfileChanged) {
    const gitDiffResult = child_process.execFileSync('git', ['diff', '--', lockfilePath]);
    console.error(`
Error: git status reports that there is an unexpected lockfile change
in ${lockfilePath}.

This probably means that you (or Dependabot) has updated a dependency
in a build.gradle file without updating the lockfile. To update the
lockfile:

    1) Pull this branch
    2) Run "./gradlew build" from /AccessibilityInsightsForAndroidService
    3) Commit and push the resulting change to gradle.lockfile
    
Diff of the unexpected change:

${gitDiffResult.toString()}
    `);
    process.exit(1);
} else {
    console.log('Success! git status reports no unexpected lockfile change.');
    process.exit(0);
}
