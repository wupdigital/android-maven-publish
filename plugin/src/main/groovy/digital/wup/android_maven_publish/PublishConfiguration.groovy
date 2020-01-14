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

import org.gradle.api.artifacts.PublishArtifact

interface PublishConfiguration {

    /**
     * Component name for publish
     *
     * @return Name of component
     */
    String getName()

    /**
     * Publish configuration name or variant name
     * @return
     */
    String getPublishConfig()

    /**
     * Publish artifacts
     *
     * @return Artifacts
     */
    Set<PublishArtifact> getArtifacts()
}
