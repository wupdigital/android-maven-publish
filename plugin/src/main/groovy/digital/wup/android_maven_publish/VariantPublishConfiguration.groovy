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
import com.android.build.gradle.api.BaseVariantOutput
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.bundling.Zip

class VariantPublishConfiguration implements PublishConfiguration {

    private final Map<String, PublishArtifact> artifacts = new HashMap<>()
    private final BaseVariant variant

    VariantPublishConfiguration(BaseVariant variant) {
        this.variant = variant
    }

    @Override
    String getName() {
        return 'android' + variant.name.capitalize()
    }

    @Override
    String getPublishConfig() {
        return variant.name
    }

    @Override
    Set<PublishArtifact> getArtifacts() {
        def artifacts = variant.outputs.collect { BaseVariantOutput o ->
            return cachedArtifact(o)
        }.toSet()

        return Collections.unmodifiableSet(artifacts)
    }

    private PublishArtifact cachedArtifact(BaseVariantOutput o) {
        PublishArtifact artifact = artifacts.get(o.baseName)

        if (!artifact) {
            artifact = new ArchivePublishArtifact(findArchiveTask(o))
                    .builtBy(o.assemble)
            artifacts.put(o.baseName, artifact)
        }
        return artifact
    }

    private static AbstractArchiveTask findArchiveTask(BaseVariantOutput o) {
        return (AbstractArchiveTask) o.assemble.dependsOn.find {
            (it instanceof Zip) && it.name.startsWith('bundle')
        }
    }
}
