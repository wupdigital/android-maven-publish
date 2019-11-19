[![Build Status](https://travis-ci.org/wupdigital/android-maven-publish.svg?branch=master)](https://travis-ci.org/wupdigital/android-maven-publish)
[![Coverage Status](https://coveralls.io/repos/github/wupdigital/android-maven-publish/badge.svg?branch=master)](https://coveralls.io/github/wupdigital/android-maven-publish?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)

# android-maven-publish

Modification of the standard Maven Publish plugin to be compatible with _android-library_ projects (_aar_).

## Applying the plugin

``` groovy
plugins {
    id 'digital.wup.android-maven-publish' version '3.6.2'
}
```

-or-

``` groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'digital.wup:android-maven-publish:3.6.2'
    }
}

apply plugin: 'digital.wup.android-maven-publish'
```

## Documentation

The _android-maven-publish_ plugin provides a `SoftwareComponent` similar to the `java` plugin (`components.java`).
The `android` component is used to determine which _aar_ files are published and which dependencies should be listed in the generated _POM_ file.

### Usage

Please refer to the standard [Maven Publish plugin documentation](https://docs.gradle.org/current/userguide/publishing_maven.html).

``` groovy
publishing {
    publications {
        mavenAar(MavenPublication) {
            from components.android
        }
    }
}
```

If you want to publish sources with your artifact:

``` groovy
task sourceJar(type: Jar) {
    from android.sourceSets.main.java.srcDirs
    classifier "sources"
}

publishing {
    publications {
        mavenAar(MavenPublication) {
            from components.android
            
            groupId 'com.company.mylib'
            artifactId 'mylib'
            version '1.0.0'
            artifact(sourceJar)
        }
    }
}
```


If you want to publish custom variants:

``` groovy
publishing {
    publications {
        android.libraryVariants.all { variant ->

            "maven${variant.name.capitalize()}Aar"(MavenPublication) {
                from components.findByName("android${variant.name.capitalize()}")
                groupId 'digital.wup.test-publish'
                artifactId 'test-publish'
                version "1.0.0-${variant.name}"
            }
        }
    }
}
```

## Compatibility information

| Plugin Version | Dependency Information | Gradle Version |
| ------------- | ----------- | ----------- |
| 1.0.0 | digital.wup:android-maven-publish:1.0.0 | 2.4 - 3.3   |
| 2.0.0 | digital.wup:android-maven-publish:2.0.0 | 3.4 - 4.0   |
| 3.0.0 | digital.wup:android-maven-publish:3.0.0 | 3.4 - 4.1   |
| 3.1.1 | digital.wup:android-maven-publish:3.1.1 | 4.2 - 4.3.x |
| 3.2.0 | digital.wup:android-maven-publish:3.2.0 | 4.4         |
| 3.3.0 | digital.wup:android-maven-publish:3.3.0 | 4.5         |
| 3.4.0 | digital.wup:android-maven-publish:3.4.0 | 4.5 - 4.6   |
| 3.5.1 | digital.wup:android-maven-publish:3.5.1 | 4.7         |
| 3.6.3 | digital.wup:android-maven-publish:3.6.3 | 4.8 -       |

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

