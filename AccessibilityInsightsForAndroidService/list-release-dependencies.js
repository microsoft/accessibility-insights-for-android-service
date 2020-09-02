// USAGE:
//
//     node list-release-dependencies.js [/path/to/gradle.lockfile]
//
// Lists the *release* dependencies from a gradle.lockfile (by default, /app/gradle.lockfile).
//
// A "release" dependency is one that is distributed with our releases, that is, one which is
// used in at least one non-test, non-lint Gradle task.

const fs = require('fs');
const path = require('path');
const process = require('process');

function isDevTarget(target) {
    return target === 'lintClassPath' || /Test/.test(target);
}
function isReleaseTarget(target) {
    return !isDevTarget(target);
}
function isEmptyLine(line) {
    // whitespace until EOL or start of comment
    return /^\s*(\#|$)/.test(line);
}

const lockfilePath = process.argv[2] || path.join(__dirname, 'app', 'gradle.lockfile');
const lockfileContent = fs.readFileSync(lockfilePath).toString();
const lockfileLines = lockfileContent.split(/\r?\n/);

for (const line of lockfileLines) {
    if (isEmptyLine(line) || line.startsWith('empty=')) {
        continue;
    }

    const parts = line.split('=');
    if (parts.length !== 2) {
        throw new Error(`malformatted line: ${line}`);
    }

    const [dep, targetsLine] = parts;
    const targets = targetsLine.split(',');

    if (targets.some(isReleaseTarget)) {
        console.log(dep);
    }
}
