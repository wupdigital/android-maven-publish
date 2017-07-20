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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

class AndroidMavenPublishPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply(MavenPublishPlugin)

        if (isAndroidLibraryPluginApplied(project)) {
            addAndroidComponent(project)
        }
    }

    private static boolean isAndroidLibraryPluginApplied(Project project) {
        return project.plugins.hasPlugin('com.android.library')
    }

    private static void addAndroidComponent(Project project) {
        Task assemble = project.tasks.findByName('assemble')

        File aarFile = new File("${project.buildDir}${File.separator}outputs${File.separator}${project.name}-release.aar")

        AarPublishArtifact artifact = new AarPublishArtifact(aarFile, project.name)
        artifact.builtBy(assemble)
        project.components.add(new AndroidLibrary(project.configurations, artifact))
    }
}