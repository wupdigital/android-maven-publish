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

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.BaseVariant
import groovy.util.logging.Slf4j
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.attributes.ImmutableAttributesFactory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.AppliedPlugin
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

        project.pluginManager.withPlugin('com.android.library', new Action<AppliedPlugin>() {
            @Override
            void execute(AppliedPlugin appliedPlugin) {
                addSoftwareComponents(project)
            }
        })

        project.pluginManager.withPlugin('com.android.application', new Action<AppliedPlugin>() {
            @Override
            void execute(AppliedPlugin appliedPlugin) {
                addSoftwareComponents(project)
            }
        })
    }

    private void addSoftwareComponents(Project project) {

        def configurations = project.configurations

        variants(project).all { BaseVariant v ->
            def publishConfig = new VariantPublishConfiguration(v)
            project.components.add(new AndroidVariantLibrary(objectFactory, configurations, attributesFactory, publishConfig))
        }

        // For default publish config
        def defaultPublishConfig = new DefaultPublishConfiguration(project)
        project.components.add(new AndroidVariantLibrary(objectFactory, configurations, attributesFactory, defaultPublishConfig))
    }

    private static variants(final Project project) {

        def android = project.extensions.getByType(BaseExtension)
        if (android.hasProperty('libraryVariants')) {
            return android.libraryVariants
        } else if (android.hasProperty('applicationVariants')) {
            return android.applicationVariants
        }
        return null
    }
}