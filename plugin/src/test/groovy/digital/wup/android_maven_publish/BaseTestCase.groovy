/*
 * Copyright 2017 W.UP Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package digital.wup.android_maven_publish

import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.testfixtures.ProjectBuilder

import java.security.CodeSource

abstract class BaseTestCase extends GroovyTestCase {

    protected static final String PROJECT_NAME = 'android-test'

    protected final Project buildAndroidProject() {

        Project p = ProjectBuilder.builder()
                .withProjectDir(getAndroidTestDir())
                .withName(PROJECT_NAME)
                .build()
        p.apply plugin: 'com.android.library'
        p.plugins.apply(AndroidMavenPublishPlugin.class)

        p.repositories {
            jcenter()
        }
        p.android {
            compileSdkVersion 25
            buildToolsVersion "25.0.3"
        }
        p.dependencies {
            compile 'com.google.code.gson:gson:2.8.1'
        }

        p.publishing {
            publications {
                mavenAar(MavenPublication) {
                    from p.components.android

                }
            }
            repositories {
                maven {
                    url mavenLocal().url
                }
            }
        }
        p.evaluate()
        return p
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected final Project buildJavaProject() {
        Project p = ProjectBuilder.builder()
                .build()
        p.apply plugin: 'java'
        p.apply plugin: AndroidMavenPublishPlugin

        p.evaluate()
        return p
    }

    /**
     * Returns the gradle plugin test folder.
     */
    protected final File getAndroidTestDir() {
        CodeSource source = getClass().getProtectionDomain().getCodeSource()
        if (source != null) {
            URL location = source.getLocation();
            try {
                File dir = new File(location.toURI())
                assertTrue(dir.getPath(), dir.exists())
                File f = dir.getParentFile().getParentFile().getParentFile().getParentFile()

                return new File(f, PROJECT_NAME)
            } catch (URISyntaxException e) {
                fail(e.getLocalizedMessage())
            }
        }
        fail("Fail to get tests folder")
    }

    protected final File getIntTestDir() {
        CodeSource source = getClass().getProtectionDomain().getCodeSource()
        if (source != null) {
            URL location = source.getLocation();
            try {
                File dir = new File(location.toURI())
                assertTrue(dir.getPath(), dir.exists())
                File f = dir.getParentFile().getParentFile().getParentFile().getParentFile()

                return new File(f, 'android-int-test')
            } catch (URISyntaxException e) {
                fail(e.getLocalizedMessage())
            }
        }
        fail("Fail to get tests folder")
    }


    protected final File getProjectAarOutputsDir() {
        new File(getAndroidTestDir(), "build${File.separator}outputs${File.separator}aar")
    }
}
