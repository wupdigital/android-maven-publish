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

import org.gradle.api.publish.maven.internal.dependencies.MavenDependencyInternal
import org.gradle.api.publish.maven.internal.publication.MavenPomInternal
import org.gradle.api.publish.maven.internal.tasks.MavenPomFileGenerator
import org.gradle.api.publish.maven.tasks.GenerateMavenPom

class BugFixedGenerateMavenPom extends GenerateMavenPom {

    @Override
    public void doGenerate() {
        MavenPomInternal pomInternal = (MavenPomInternal) this.getPom()
        MavenPomFileGenerator pomGenerator = new BugFixedMavenPomFileGenerator(pomInternal.getProjectIdentity(), this.getVersionRangeMapper())
        pomGenerator.setPackaging(pomInternal.getPackaging())
        Iterator i$ = pomInternal.getRuntimeDependencies().iterator()

        while (i$.hasNext()) {
            MavenDependencyInternal runtimeDependency = (MavenDependencyInternal) i$.next()
            pomGenerator.addRuntimeDependency(runtimeDependency)
        }

        pomGenerator.withXml(pomInternal.getXmlAction())
        pomGenerator.writeTo(this.getDestination())
    }
}
