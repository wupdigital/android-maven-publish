package digital.wup.android_maven_publish

import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder

class TestUtil {

    static ProjectInternal createRootProject(File rootDir) {
        return ProjectBuilder
                .builder()
                .withProjectDir(rootDir)
                .build()
    }

    static ProjectInternal createChildProject(ProjectInternal parent, String name, File projectDir = null) {
        return ProjectBuilder
                .builder()
                .withName(name)
                .withParent(parent)
                .withProjectDir(projectDir)
                .build();
    }
}
