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

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.PublishArtifact

class DefaultPublishConfiguration implements PublishConfiguration {

    private Project project

    DefaultPublishConfiguration(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return 'android'
    }

    @Override
    String getPublishConfig() {
        def android = project.extensions.getByType(LibraryExtension)
        return android.defaultPublishConfig
    }

    @Override
    Set<PublishArtifact> getArtifacts() {
        def configurations = project.configurations
        def artifacts = configurations.getByName(Dependency.ARCHIVES_CONFIGURATION).allArtifacts.toSet()

        // Fix duplicated artifact when use android gradle build tools 2.3.3 or lower
        return artifacts.unique(false, new Comparator<PublishArtifact>() {
            @Override
            int compare(PublishArtifact a1, PublishArtifact a2) {
                "${a1.file.path}${a1.type}${a1.classifier}" <=> "${a2.file.path}${a2.type}${a2.classifier}"
            }
        })
    }
}
