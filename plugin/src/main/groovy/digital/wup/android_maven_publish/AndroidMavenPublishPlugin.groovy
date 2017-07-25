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

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.internal.publication.MavenPublicationInternal
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.api.tasks.TaskContainer
import org.gradle.model.Mutate
import org.gradle.model.RuleSource

class AndroidMavenPublishPlugin implements Plugin<Project> {

    @Override
    void apply(final Project project) {
        project.plugins.apply(MavenPublishPlugin)

        project.extensions.configure(PublishingExtension.class, new Action<PublishingExtension>() {
            @Override
            void execute(PublishingExtension publishingExtension) {
                publishingExtension.metaClass.useCompileDependencies << {

                    if (project.properties.containsKey('useCompileDependencies')) {
                        return Boolean.parseBoolean(String.valueOf(project.properties['useCompileDependencies']))
                    }
                    return false
                }
            }
        })

        if (isAndroidLibraryPluginApplied(project)) {
            addAndroidComponent(project)
        }
    }

    private static boolean isAndroidLibraryPluginApplied(Project project) {
        return project.plugins.hasPlugin('com.android.library')
    }

    private static void addAndroidComponent(Project project) {
        Task assemble = project.tasks.findByName('assemble')

        AarPublishArtifact artifact = new AarPublishArtifact(project)
        artifact.builtBy(assemble)
        project.components.add(new AndroidLibrary(project.configurations, artifact))
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    static class Rule extends RuleSource {

        @Mutate
        public void realizePublishingTasks(TaskContainer tasks, PublishingExtension extension) {

            if (!extension.useCompileDependencies()) {
                return
            }

            PublicationContainer publications = extension.getPublications();
            Iterator i$ = publications.withType(MavenPublicationInternal.class).iterator();

            while (i$.hasNext()) {
                MavenPublicationInternal publication = (MavenPublicationInternal) i$.next();
                String publicationName = publication.getName();
                this.createGeneratePomTask(tasks, publication, publicationName);
            }
        }

        @SuppressWarnings("GrMethodMayBeStatic")
        private void createGeneratePomTask(TaskContainer tasks,
                                           final MavenPublicationInternal publication,
                                           final String publicationName) {
            String descriptorTaskName = "generatePomFileFor" + publicationName.capitalize() + "Publication";

            //noinspection GroovyAssignabilityCheck
            GenerateMavenPom oldGenerateMavenPomTask = tasks.getByName(descriptorTaskName)

            tasks.remove(oldGenerateMavenPomTask)

            tasks.create(descriptorTaskName, BugFixedGenerateMavenPom.class, new Action<BugFixedGenerateMavenPom>() {
                @Override
                void execute(BugFixedGenerateMavenPom bugFixedGenerateMavenPom) {

                    bugFixedGenerateMavenPom.description = oldGenerateMavenPomTask.description
                    bugFixedGenerateMavenPom.group = oldGenerateMavenPomTask.group
                    bugFixedGenerateMavenPom.pom = oldGenerateMavenPomTask.pom
                    bugFixedGenerateMavenPom.destination = oldGenerateMavenPomTask.destination
                }
            })

            publication.setPomFile(((Task) tasks.getByName(descriptorTaskName)).getOutputs().getFiles());
        }
    }
}