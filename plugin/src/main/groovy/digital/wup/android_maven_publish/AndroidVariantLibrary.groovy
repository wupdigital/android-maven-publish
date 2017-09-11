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

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.Usage
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.AbstractArchiveTask

final class AndroidVariantLibrary implements SoftwareComponentInternal {

    private final String variantName
    private final UsageContext compileUsage
    private final UsageContext runtimeUsage

    AndroidVariantLibrary(Project project, BaseVariant variant) {
        variantName = variant.name
        compileUsage = new CompileUsage(project, variant)
        runtimeUsage = new RuntimeUsage(project, variant)
    }

    @Override
    Set<UsageContext> getUsages() {
        return Collections.unmodifiableSet([compileUsage, runtimeUsage].toSet())
    }

    @Override
    String getName() {
        return "android${variantName.capitalize()}"
    }


    private static class CompileUsage extends BaseUsage {

        private DependencySet dependencies

        CompileUsage(Project project, BaseVariant variant) {
            super(project, variant)
        }

        @Override
        Usage getUsage() {
            return Usage.FOR_COMPILE
        }

        @Override
        Set<ModuleDependency> getDependencies() {
            if (dependencies == null) {
                dependencies = configurations.findByName(variant.name + JavaPlugin.API_ELEMENTS_CONFIGURATION_NAME.capitalize()).allDependencies
            }
            return dependencies.withType(ModuleDependency)
        }
    }

    private static class RuntimeUsage extends BaseUsage {

        private DependencySet dependencies

        RuntimeUsage(Project project, BaseVariant variant) {
            super(project, variant)
        }

        @Override
        Usage getUsage() {
            return Usage.FOR_RUNTIME
        }

        @Override
        Set<ModuleDependency> getDependencies() {
            if (dependencies == null) {
                dependencies = configurations.findByName(variant.name + JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME.capitalize()).allDependencies
            }
            return dependencies.withType(ModuleDependency)
        }
    }

    private static abstract class BaseUsage implements UsageContext {
        protected final Project project
        protected final BaseVariant variant
        protected final ConfigurationContainer configurations

        BaseUsage(Project project, BaseVariant variant) {
            this.project = project
            this.configurations = project.configurations
            this.variant = variant
        }

        @Override
        Set<PublishArtifact> getArtifacts() {
            def artifacts = variant.outputs.collect { o ->
                def archiveTask = project.tasks.findByName("bundle${variant.name.capitalize()}")

                return new ArchivePublishArtifact(archiveTask as AbstractArchiveTask)
                        .builtBy(o.assemble)
            }.toSet()

            return Collections.unmodifiableSet(artifacts)
        }
    }
}
