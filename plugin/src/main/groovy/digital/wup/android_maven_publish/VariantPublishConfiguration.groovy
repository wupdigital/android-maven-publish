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
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.tasks.bundling.AbstractArchiveTask

class VariantPublishConfiguration implements PublishConfiguration {

    final BaseVariant variant

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
        def artifacts = variant.outputs.collect { o ->
            def archiveTask = project.tasks.findByName("bundle${variant.name.capitalize()}")

            return new ArchivePublishArtifact(archiveTask as AbstractArchiveTask)
                    .builtBy(o.assemble)
        }.toSet()

        return Collections.unmodifiableSet(artifacts)
    }
}
