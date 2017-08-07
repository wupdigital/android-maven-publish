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

import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.GenerateMavenPom

class AndroidMavenPublishPluginTest extends AbstractProjectBuilderSpec {

    PublishingExtension publishing

    def 'setup'() {
        project.plugins.apply 'com.android.library'
        project.plugins.apply(AndroidMavenPublishPlugin)
        publishing = project.extensions.getByType(PublishingExtension)
    }

    def 'android maven publish applied'() {
        expect:
        project.plugins.hasPlugin(AndroidMavenPublishPlugin)
    }

    def 'use runtime dependencies'() {
        expect:
        project.useCompileDependencies == false
    }

    def 'android library component has added'() {
        expect:
        project.components.android != null
        project.components.android instanceof AndroidLibrary
    }

    def 'use compile deps property configurable with publishing extension'() {
        when:
        publishing.useCompileDependencies(true)

        then:
        project.useCompileDependencies == true
    }

    def 'generate pom task stay in original'() {
        when:
        project.ext.useCompileDependencies = false
        publishing.publications.create('test', MavenPublication)
        closeTaskContainer()

        then:
        project.tasks['generatePomFileForTestPublication'] != null
        project.tasks['generatePomFileForTestPublication'] instanceof GenerateMavenPom
    }

    def 'pom dir moves with build dir'() {
        when:
        project.ext.useCompileDependencies = true
        publishing.publications.create('test', MavenPublication)
        def newBuildDir = project.file('changed')
        project.buildDir = newBuildDir
        closeTaskContainer()

        then:
        project.tasks['generatePomFileForTestPublication'].destination == new File(newBuildDir, 'publications/test/pom-default.xml')
    }

    void closeTaskContainer() {
        project.getTasks().realize()
    }
}
