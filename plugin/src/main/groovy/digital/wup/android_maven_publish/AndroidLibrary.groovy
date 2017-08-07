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

import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.Usage
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext

final class AndroidLibrary implements SoftwareComponentInternal {

    private final UsageContext usage;

    AndroidLibrary(ConfigurationContainer configurations, UsageProvider usageProvider) {
        usage = new RuntimeUsage(configurations, usageProvider)
    }

    @Override
    Set<UsageContext> getUsages() {
        return Collections.singleton(usage)
    }

    @Override
    String getName() {
        return 'android'
    }

    private final class RuntimeUsage implements UsageContext {

        private final ConfigurationContainer configurations
        private DependencySet dependencies
        private UsageProvider usageProvider

        RuntimeUsage(ConfigurationContainer configurations, UsageProvider usageProvider) {
            this.configurations = configurations
            this.usageProvider = usageProvider
        }

        @Override
        Usage getUsage() {
            return usageProvider.getUsage()
        }

        @Override
        Set<PublishArtifact> getArtifacts() {
            Set<PublishArtifact> artifacts = configurations.getByName('archives').allArtifacts.toSet()
            return artifacts.unique(false, new Comparator<PublishArtifact>() {
                @Override
                int compare(PublishArtifact a1, PublishArtifact a2) {
                    "${a1.file.path}${a1.type}${a1.classifier}" <=> "${a2.file.path}${a2.type}${a2.classifier}"
                }
            })
        }

        @Override
        Set<ModuleDependency> getDependencies() {
            if (dependencies == null) {
                dependencies = configurations.getByName("default").getAllDependencies()
            }
            return dependencies.withType(ModuleDependency)
        }
    }
}
