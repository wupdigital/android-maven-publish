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
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.attributes.ImmutableAttributesFactory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

import javax.inject.Inject

@Slf4j
class AndroidMavenPublishPlugin implements Plugin<Project> {

    private ObjectFactory objectFactory
    private ImmutableAttributesFactory attributesFactory

    @Inject
    public AndroidMavenPublishPlugin(ObjectFactory objectFactory, ImmutableAttributesFactory attributesFactory) {
        this.objectFactory = objectFactory
        this.attributesFactory = attributesFactory
    }

    @Override
    void apply(final Project project) {
        project.plugins.apply(MavenPublishPlugin)

        if (isAndroidLibraryPluginApplied(project)) {
            def android = project.extensions.getByType(LibraryExtension)

            def configurations = project.configurations

            android.libraryVariants.all { v ->
                def publishConfig = new VariantPublishConfiguration(v)
                project.components.add(new AndroidVariantLibrary(objectFactory, configurations, attributesFactory, publishConfig))
            }

            // For default publish config
            def defaultPublishConfig = new DefaultPublishConfiguration(project)
            project.components.add(new AndroidVariantLibrary(objectFactory, configurations, attributesFactory, defaultPublishConfig))
        }
    }

    private static boolean isAndroidLibraryPluginApplied(Project project) {
        return project.plugins.hasPlugin('com.android.library')
    }
}