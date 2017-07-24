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
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.internal.component.Usage
import org.gradle.testkit.runner.GradleRunner
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4.class)
class AndroidMavenPublishTest extends BaseTestCase {

    @Ignore
    @Test
    public void integrationTest() {
        //runGradle('2.4', '1.1.2', '21.1.1')
        //runGradle('2.4', '1.2.0', '21.1.1')
        //runGradle('2.5', '1.2.0', '21.1.1')
        //runGradle('2.6', '1.2.0', '21.1.1')
        //runGradle('2.7', '1.2.0', '21.1.1')
        //runGradle('2.8', '1.2.0', '21.1.1')
        //runGradle('2.9', '1.2.0', '21.1.1')
        //runGradle('2.9', '1.3.0', '21.1.1')
        //runGradle('2.4', '1.3.1', '21.1.1')
        //runGradle('2.5', '1.3.1', '21.1.1')
        //runGradle('2.6', '1.3.1', '21.1.1')
        //runGradle('2.7', '1.3.1', '21.1.1')
        //runGradle('2.8', '1.3.1', '21.1.1')
        //runGradle('2.9', '1.3.1', '21.1.1')
        //runGradle('2.9', '1.5.0', '21.1.1')
        //runGradle('2.10', '1.5.0', '21.1.1')
        //runGradle('2.11', '1.5.0', '21.1.1')
        //runGradle('2.12', '1.5.0', '21.1.1')
        //runGradle('2.13', '1.5.0', '21.1.1')
        //runGradle('2.10', '2.0.0', '21.1.1')
        //runGradle('2.11', '2.0.0', '21.1.1')
        //runGradle('2.12', '2.0.0', '21.1.1')
        //runGradle('2.13', '2.0.0', '21.1.1')
        //runGradle('2.10', '2.1.0', '23.0.2')
        //runGradle('2.11', '2.1.0', '23.0.2')
        //runGradle('2.12', '2.1.0', '23.0.2')
        //runGradle('2.13', '2.1.0', '23.0.2')
        runGradle('2.14.1', '2.1.3', '23.0.3')
        runGradle('2.14.1', '2.2.3', '23.0.3')
        runGradle('3.3', '2.3.3', '25.0.3')
    }

    private void runGradle(String gradleVersion, String gradleToolsVersion, buildToolsVersion) {
        GradleRunner runner = GradleRunner.create()
                .withProjectDir(getIntTestDir())
                .withArguments("-PgradleBuildToolsVersion=${gradleToolsVersion}",
                "-PbuildToolsVersion=${buildToolsVersion}",
                '-PmavenPublishVersion=1.0.0-SNAPSHOT', 'clean', 'build', 'publish')
                .withGradleVersion(gradleVersion)

        runner.build()
    }

    @Test
    public void testMavenPublishPluginRequired() {
        Project project = buildAndroidProject()

        assertNotNull('maven-publish plugin hasn\'t applied', project.plugins.findPlugin('maven-publish'))
    }

    @Test
    public void testAndroidComponent() {
        Project project = buildAndroidProject()

        assertNotNull('Android component not found', project.components.getByName('android'))
        def android = project.components.android
        assertTrue('Android component is not instance of AndroidLibrary', android instanceof AndroidLibrary)
        assertEquals('Android library\'s name is not android', 'android', android.getName())
        assertFalse('Usages is empty', android.getUsages().isEmpty())
    }

    @Test
    public void testCompileUsage() {
        def component = buildAndroidProject().components.android

        Usage usage = component.getUsages().getAt(0)
        assertEquals('It is not compile usage', 'compile', usage.getName())
        assertFalse("Artifacts not found", usage.getArtifacts().isEmpty())

        def artifact = usage.getArtifacts().getAt(0)

        assertTrue(artifact instanceof AarPublishArtifact)

        assertFalse(usage.getDependencies().isEmpty())
    }

    @Test
    public void testAarArtifact() {
        Project project = buildAndroidProject()
        AndroidLibrary android = project.components.android
        Usage usage = android.getUsages().getAt(0)
        AarPublishArtifact artifact = usage.getArtifacts().getAt(0)
        assertEquals(PROJECT_NAME, artifact.getName())
        assertNull('Artifact classifier is not null', artifact.getClassifier())
        assertNull('Artifact date is not null', artifact.getDate())
        assertEquals('Artifact extension is not aar', 'aar', artifact.getExtension())
        assertEquals('Artifact aar path is not correct',
                new File(getProjectAarOutputsDir(), "${PROJECT_NAME}-release.aar").path, artifact.getFile().path)
        assertEquals('Artifact type is not aar', 'aar', artifact.getType())
    }

    @Test(expected = UnknownDomainObjectException)
    public void testAndroidLibraryPluginNotApplied() throws UnknownDomainObjectException {
        Project project = buildJavaProject()
        project.components.getByName('android')
    }
}
