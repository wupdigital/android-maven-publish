[![Build Status](https://travis-ci.org/wupdigital/android-maven-publish.svg?branch=master)](https://travis-ci.org/wupdigital/android-maven-publish)
[![Coverage Status](https://coveralls.io/repos/github/wupdigital/android-maven-publish/badge.svg?branch=master)](https://coveralls.io/github/wupdigital/android-maven-publish?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)

# android-maven-publish

Modification of the standard Maven Publish plugin to be compatible with android-library projects (aar).

## Applying the plugin

```
plugins {
    id 'digital.wup.android-maven-publish' version '2.0.0'
}
```
-or-
```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'digital.wup:android-maven-publish:2.0.0'
    }
}

apply plugin: 'digital.wup.android-maven-publish'

```

## Documentation

The android-maven-publish plugin provides a `SoftwareComponent` like `java` plugin (`components.java`).
The android component is used to determine which aar files to publish, and which dependencies should be listed in the generated POM file.

### Usage

Please refer to the standard Maven Publish plugin documentation: https://docs.gradle.org/current/userguide/publishing_maven.html

```
publishing {
    publications {
        mavenAar(MavenPublication) {
            from components.android
        }
    }
}
```

This plugin also fixes [issue](https://github.com/gradle/gradle/issues/1118) by adding a method called `useCompileDependencies`. The method called with a Boolean true parameter will use compile dependencies instead of runtime ones.

```
publishing {
    useCompileDependencies true
    
    publications {
        mavenAar(MavenPublication) {
            from components.java
        }
    }
}
```

## Compatibility information

| Plugin Version | Dependency Information | Gradle Version |
| ------------- | ----------- | ----------- |
| 1.0.0 | digital.wup:android-maven-publish:1.0.0 | 2.4 - 3.3 |
| 2.0.0 | digital.wup:android-maven-publish:2.0.0 | 3.4 - 4.0 |

## License

    Copyright 2017 W.UP, Ltd.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

