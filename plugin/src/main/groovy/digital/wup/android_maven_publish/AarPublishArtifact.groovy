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

import org.gradle.api.internal.artifacts.publish.AbstractPublishArtifact

final class AarPublishArtifact extends AbstractPublishArtifact {
    private final aarFile
    private final String artifactId

    AarPublishArtifact(final File aarFile, final String artifactId) {
        super(new Object[0])
        this.aarFile = aarFile
        this.artifactId = artifactId
    }

    @Override
    String getName() {
        return artifactId
    }

    @Override
    String getExtension() {
        return 'aar'
    }

    @Override
    String getType() {
        return 'aar'
    }

    @Override
    String getClassifier() {
        return null
    }

    @Override
    File getFile() {
        return aarFile
    }

    @Override
    Date getDate() {
        return null
    }
}
