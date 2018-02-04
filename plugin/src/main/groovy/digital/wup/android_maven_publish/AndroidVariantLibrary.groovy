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
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.attributes.Usage
import org.gradle.api.internal.attributes.ImmutableAttributes
import org.gradle.api.internal.attributes.ImmutableAttributesFactory
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin

final class AndroidVariantLibrary implements SoftwareComponentInternal {

    private final Set<UsageContext> _usages
    private final PublishConfiguration publishConfiguration
    private final ImmutableAttributesFactory attributesFactory;

    AndroidVariantLibrary(ObjectFactory objectFactory, ConfigurationContainer configurations, ImmutableAttributesFactory attributesFactory, PublishConfiguration publishConfiguration) {
        this.publishConfiguration = publishConfiguration
        this.attributesFactory = attributesFactory

        final UsageContext compileUsage = new CompileUsage(configurations, attributesFactory, publishConfiguration, objectFactory.named(Usage.class, Usage.JAVA_API))
        final UsageContext runtimeUsage = new RuntimeUsage(configurations, attributesFactory, publishConfiguration, objectFactory.named(Usage.class, Usage.JAVA_RUNTIME))

        def usages = [compileUsage]
        if (configurations.findByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME)) {
            usages += runtimeUsage
        }
        _usages = Collections.unmodifiableSet(usages.toSet())
    }

    @Override
    Set<UsageContext> getUsages() {
        return _usages
    }

    @Override
    String getName() {
        return publishConfiguration.name
    }

    private static class CompileUsage extends BaseUsage {

        private DependencySet dependencies

        CompileUsage(ConfigurationContainer configurations, ImmutableAttributesFactory attributesFactory, PublishConfiguration publishConfiguration, Usage usage) {
            super(configurations, attributesFactory, publishConfiguration, usage)
        }

        @Override
        String getName() {
            return 'api'
        }

        @Override
        Set<ModuleDependency> getDependencies() {
            if (dependencies == null) {
                def apiElements = publishConfiguration.publishConfig + JavaPlugin.API_ELEMENTS_CONFIGURATION_NAME.capitalize()
                if (configurations.findByName(apiElements)) {
                    dependencies = configurations.findByName(apiElements).allDependencies
                } else {
                    dependencies = configurations.findByName('default').allDependencies
                }
            }
            return dependencies.withType(ModuleDependency)
        }
    }

    private static class RuntimeUsage extends BaseUsage {

        private DependencySet dependencies

        RuntimeUsage(ConfigurationContainer configurations, ImmutableAttributesFactory attributesFactory, PublishConfiguration publishConfiguration, Usage usage) {
            super(configurations, attributesFactory, publishConfiguration, usage)
        }

        @Override
        String getName() {
            return 'runtime'
        }

        @Override
        Set<ModuleDependency> getDependencies() {
            if (dependencies == null) {
                def runtimeElements = publishConfiguration.publishConfig + JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME.capitalize()
                dependencies = configurations.findByName(runtimeElements).allDependencies
            }
            return dependencies.withType(ModuleDependency)
        }
    }

    private static abstract class BaseUsage implements UsageContext {
        protected final ConfigurationContainer configurations
        protected final PublishConfiguration publishConfiguration
        private final Usage usage
        private final ImmutableAttributes attributes

        BaseUsage(ConfigurationContainer configurations, ImmutableAttributesFactory attributesFactory, PublishConfiguration publishConfiguration, Usage usage) {
            this.configurations = configurations
            this.publishConfiguration = publishConfiguration
            this.usage = usage
            this.attributes = attributesFactory.of(Usage.USAGE_ATTRIBUTE, usage)
        }

        @Override
        Usage getUsage() {
            return usage
        }

        @Override
        Set<PublishArtifact> getArtifacts() {
            return publishConfiguration.artifacts
        }

        @Override
        AttributeContainer getAttributes() {
            return attributes
        }
    }
}
