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

import org.gradle.api.DomainObjectSet
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.bundling.Zip

class AndroidMavenPublishPluginIntegTest extends AbstractProjectBuilderSpec {

    private static final API_CONFIGURATION_NAME = 'api'
    private static final IMPLEMENTATION_CONFIGURATION_NAME = 'implementation'
    private static final RELEASE_RUNTIME_CLASSPATH_CONFIGURATION_NAME = 'releaseRuntimeElements'
    private static final DEBUG_RUNTIME_CLASSPATH_CONFIGURATION_NAME = 'releaseRuntimeElements'


    def 'applies android-maven-publish plugin'() {
        when:
        project.plugins.apply 'com.android.library'
        project.plugins.apply(AndroidMavenPublishPlugin)
        then:
        project.plugins.findPlugin(AndroidMavenPublishPlugin)
    }

    def 'adds Android library component'() {
        given:
        project.plugins.apply 'com.android.library'
        project.pluginManager.apply(AndroidMavenPublishPlugin)
        project.android.compileSdkVersion = 27
        project.dependencies.add(API_CONFIGURATION_NAME, 'org:api1:1.0')
        project.dependencies.constraints.add(API_CONFIGURATION_NAME, 'org:api2:2.0')
        project.dependencies.add(IMPLEMENTATION_CONFIGURATION_NAME, 'org:impl1:1.0')
        project.dependencies.constraints.add(IMPLEMENTATION_CONFIGURATION_NAME, 'org:impl2:2.0')

        when:
        project.evaluate()

        and:
        def archiveTask = findArchiveTask('release')
        def androidLibrary = project.components.getByName('android')
        def runtimeUsage = androidLibrary.usages[0]
        def apiUsage = androidLibrary.usages[1]

        then:
        runtimeUsage.artifacts.collect { it.archiveTask } == [archiveTask]
        runtimeUsage.dependencies.size() == 2
        runtimeUsage.dependencies == project.configurations.getByName(RELEASE_RUNTIME_CLASSPATH_CONFIGURATION_NAME).allDependencies.withType(ModuleDependency)
        runtimeUsage.dependencyConstraints.size() == 2
        runtimeUsage.dependencyConstraints == getDependencyConstraints(project.configurations.getByName(RELEASE_RUNTIME_CLASSPATH_CONFIGURATION_NAME))

        apiUsage.artifacts.collect { it.archiveTask } == [archiveTask]
        apiUsage.dependencies.size() == 1
        apiUsage.dependencies == project.configurations.getByName(API_CONFIGURATION_NAME).allDependencies.withType(ModuleDependency)
        apiUsage.dependencyConstraints.size() == 1
        apiUsage.dependencyConstraints == getDependencyConstraints(project.configurations.getByName(API_CONFIGURATION_NAME))
    }

    def 'adds Android library component for debug build configuration'() {
        given:
        project.plugins.apply 'com.android.library'
        project.pluginManager.apply(AndroidMavenPublishPlugin)
        project.android.compileSdkVersion = 27
        project.dependencies.add(API_CONFIGURATION_NAME, 'org:api1:1.0')
        project.dependencies.constraints.add(API_CONFIGURATION_NAME, 'org:api2:2.0')
        project.dependencies.add(IMPLEMENTATION_CONFIGURATION_NAME, 'org:impl1:1.0')
        project.dependencies.constraints.add(IMPLEMENTATION_CONFIGURATION_NAME, 'org:impl2:2.0')

        when:
        project.evaluate()

        and:
        def archiveTask = findArchiveTask('debug')
        def androidLibrary = project.components.getByName('androidDebug')
        def runtimeUsage = androidLibrary.usages[0]
        def apiUsage = androidLibrary.usages[1]

        then:
        runtimeUsage.artifacts.collect { it.archiveTask } == [archiveTask]
        runtimeUsage.dependencies.size() == 2
        runtimeUsage.dependencies == project.configurations.getByName(DEBUG_RUNTIME_CLASSPATH_CONFIGURATION_NAME).allDependencies.withType(ModuleDependency)
        runtimeUsage.dependencyConstraints.size() == 2
        runtimeUsage.dependencyConstraints == getDependencyConstraints(project.configurations.getByName(DEBUG_RUNTIME_CLASSPATH_CONFIGURATION_NAME))

        apiUsage.artifacts.collect { it.archiveTask } == [archiveTask]
        apiUsage.dependencies.size() == 1
        apiUsage.dependencies == project.configurations.getByName(API_CONFIGURATION_NAME).allDependencies.withType(ModuleDependency)
        apiUsage.dependencyConstraints.size() == 1
        apiUsage.dependencyConstraints == getDependencyConstraints(project.configurations.getByName(API_CONFIGURATION_NAME))
    }

    private final AbstractArchiveTask findArchiveTask(String buildType) {
        return (AbstractArchiveTask) project.tasks.findByName("assemble${buildType.capitalize()}").dependsOn.find {
            (it instanceof Zip) && it.name.startsWith('bundle')
        }
    }

    private static final DomainObjectSet<DependencyConstraint> getDependencyConstraints(Configuration configuration) {
        if (configuration.hasProperty('allDependencyConstraints')) {
            return configuration.allDependencyConstraints.withType(DependencyConstraint)
        }
        return configuration.allDependencies.withType(DependencyConstraint)
    }
}
