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
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.Usage
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext

final class AndroidLibrary implements SoftwareComponentInternal {

    private final UsageContext compileUsage;
    private final RuntimeUsage runtimeUsage;

    AndroidLibrary(Project project) {
        compileUsage = new CompileUsage(project)
        runtimeUsage = new RuntimeUsage(project)
    }

    @Override
    Set<UsageContext> getUsages() {
        return Collections.unmodifiableSet([compileUsage, runtimeUsage].toSet())
    }

    @Override
    String getName() {
        return 'android'
    }

    private final class CompileUsage implements UsageContext {

        private Project project
        private ConfigurationContainer configurations
        private DependencySet dependencies

        CompileUsage(Project project) {
            this.project = project
            this.configurations = project.configurations
        }

        @Override
        Usage getUsage() {
            return Usage.FOR_COMPILE
        }

        @Override
        Set<PublishArtifact> getArtifacts() {
            return Collections.unmodifiableSet(configurations.getByName('archives').allArtifacts.toSet())
        }

        @Override
        Set<ModuleDependency> getDependencies() {
            if (dependencies == null) {
                def configurationName = "${project.android.defaultPublishConfig}ApiElements"
                dependencies = configurations.getByName(configurationName).allDependencies
            }
            return dependencies.withType(ModuleDependency)
        }
    }

    private final class RuntimeUsage implements UsageContext {

        private Project project
        private ConfigurationContainer configurations
        private DependencySet dependencies

        RuntimeUsage(Project project) {
            this.project = project
            this.configurations = project.configurations
        }

        @Override
        Usage getUsage() {
            return Usage.FOR_RUNTIME
        }

        @Override
        Set<PublishArtifact> getArtifacts() {
            return Collections.unmodifiableSet(configurations.getByName('archives').allArtifacts.toSet())
        }

        @Override
        Set<ModuleDependency> getDependencies() {
            if (dependencies == null) {
                def configurationName = "${project.android.defaultPublishConfig}RuntimeElements"
                dependencies = configurations.getByName(configurationName).allDependencies
            }
            return dependencies.withType(ModuleDependency)
        }
    }
}
