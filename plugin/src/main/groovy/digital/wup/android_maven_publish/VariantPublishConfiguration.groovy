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
import org.gradle.api.Task
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.internal.provider.AbstractProperty
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
        def artifacts = Collections.singleton(cachedArtifact(variant))

        return Collections.unmodifiableSet(artifacts)
    }

    private PublishArtifact cachedArtifact(BaseVariant variant) {
        PublishArtifact artifact = artifacts.get(variant.baseName)

        if (!artifact) {

            final assemble = findAssembleTask(variant)
            final archiveTask = findArchiveTask(assemble)

            artifact = new ArchivePublishArtifact(archiveTask)
                    .builtBy(assemble)
            artifacts.put(variant.baseName, artifact)
        }
        return artifact
    }

    private static Task findAssembleTask(BaseVariant variant) {
        if (variant.metaClass.properties*.name.contains('assembleProvider')) {
            return variant.assembleProvider.get()
        } else {
            //noinspection GrDeprecatedAPIUsage
            return variant.assemble;
        }
    }

    private static AbstractArchiveTask findArchiveTask(Task assemble) {
        def abstractArchiveTask
        assemble.dependsOn.each {
            if (it instanceof Zip && it.name.startsWith('bundle')) {
                abstractArchiveTask = it
            } else if (it instanceof AbstractProperty) {
                it.producer.visitProducerTasks { task ->
                    if (task instanceof Zip && task.name.startsWith('bundle')) {
                        abstractArchiveTask = task
                    }
                }
            }
        }

        if (abstractArchiveTask == null) {
            throw new IllegalStateException("Archive task not found for ${variant.name}, this should never happen.")
        }

        return abstractArchiveTask
    }
}