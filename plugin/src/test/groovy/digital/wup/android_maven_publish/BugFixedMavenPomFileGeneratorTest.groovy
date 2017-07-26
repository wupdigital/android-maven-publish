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

import org.gradle.api.artifacts.DependencyArtifact
import org.gradle.api.publication.maven.internal.VersionRangeMapper
import org.gradle.api.publish.maven.internal.dependencies.MavenDependencyInternal
import org.gradle.api.publish.maven.internal.publication.DefaultMavenProjectIdentity
import org.gradle.util.CollectionUtils
import spock.lang.Specification

class BugFixedMavenPomFileGeneratorTest extends Specification {

    def root = new File("build/tmp/test")
    def projectIdentity = new DefaultMavenProjectIdentity("group-id", "artifact-id", "1.0")
    def rangeMapper = Stub(VersionRangeMapper)
    def generator = new BugFixedMavenPomFileGenerator(projectIdentity, rangeMapper)

    def "writes regular dependency"() {
        def dependency = Mock(MavenDependencyInternal)
        when:
        generator.addRuntimeDependency(dependency)

        then:
        dependency.artifacts >> new HashSet<DependencyArtifact>()
        dependency.groupId >> "dep-group"
        dependency.artifactId >> "dep-name"
        dependency.version >> "dep-version"
        dependency.excludeRules >> []
        rangeMapper.map("dep-version") >> "maven-dep-version"

        and:
        with(pom) {
            dependencies.dependency.size() == 1
            with(dependencies[0].dependency[0]) {
                groupId == "dep-group"
                artifactId == "dep-name"
                version == "maven-dep-version"
                scope == "compile"
            }
        }
    }

    def "writes dependency with artifacts"() {
        def dependency = Mock(MavenDependencyInternal)
        def artifact1 = Mock(DependencyArtifact)
        def artifact2 = Mock(DependencyArtifact)

        when:
        generator.addRuntimeDependency(dependency)

        then:
        dependency.artifacts >> CollectionUtils.toSet([artifact1, artifact2])
        dependency.groupId >> "dep-group"
        dependency.version >> "dep-version"
        dependency.excludeRules >> []
        rangeMapper.map("dep-version") >> "maven-dep-version"
        artifact1.name >> "artifact-1"
        artifact1.type >> "type-1"
        artifact1.classifier >> "classifier-1"
        artifact2.name >> "artifact-2"
        artifact2.type >> null
        artifact2.classifier >> null

        and:
        with(pom) {
            dependencies.dependency.size() == 2
            with(dependencies[0].dependency[0]) {
                groupId == "dep-group"
                artifactId == "artifact-1"
                version == "maven-dep-version"
                type == "type-1"
                classifier == "classifier-1"
                scope == "compile"
            }
            with(dependencies[0].dependency[1]) {
                groupId == "dep-group"
                artifactId == "artifact-2"
                version == "maven-dep-version"
                type.empty
                classifier.empty
                scope == "compile"
            }
        }
    }

    private def getPom() {
        return new XmlSlurper().parse(pomFile);
    }

    private File getPomFile() {
        def pomFile = new File(root, "pom.xml")
        generator.writeTo(pomFile)
        return pomFile
    }
}
