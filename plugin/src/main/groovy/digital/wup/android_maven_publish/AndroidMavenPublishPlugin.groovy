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

import groovy.util.logging.Slf4j
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

@Slf4j
class AndroidMavenPublishPlugin implements Plugin<Project> {

    @Override
    void apply(final Project project) {
        project.plugins.apply(MavenPublishPlugin)

        // For backward compatibility
        project.extensions.configure(PublishingExtension, new Action<PublishingExtension>() {
            @Override
            void execute(PublishingExtension publishingExtension) {
                publishingExtension.metaClass.useCompileDependencies << { useCompileDeps ->
                    // Do nothing
                }
            }
        })


        if (isAndroidLibraryPluginApplied(project)) {
            addAndroidComponent(project)
        }
    }

    private static boolean isAndroidLibraryPluginApplied(Project project) {
        return project.plugins.hasPlugin('com.android.library')
    }

    private static void addAndroidComponent(Project project) {
        project.components.add(new AndroidLibrary(project))
    }
}