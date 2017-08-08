package digital.wup.android_maven_publish

import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.attributes.Usage
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.util.TextUtil

class AndroidLibraryTest extends AbstractProjectBuilderSpec {

    AndroidLibrary component

    def 'setup'() {
        File srcFolder = new File(root, "src${File.separator}main")
        srcFolder.mkdirs()
        File manifest = new File(srcFolder, 'AndroidManifest.xml')
        manifest.createNewFile()
        PrintWriter writer = new PrintWriter(manifest, 'UTF-8')
        writer.print(TextUtil.toPlatformLineSeparators("""<manifest
            package="digital.wup.testmavenpublish">
            </manifest>"""))
        writer.close()
        project.plugins.apply 'com.android.library'
        project.plugins.apply(AndroidMavenPublishPlugin)
        component = project.components.android

    }

    def 'runtime usage available'() {
        expect:
        !component.usages.isEmpty()
        component.usages[0].usage == Usage.FOR_RUNTIME
    }

    def 'compile usage available'() {
        when:
        project.repositories {
            jcenter()
        }
        project.android {
            compileSdkVersion 26
            buildToolsVersion '26.0.0'
        }
        project.publishing {
            publications {
                useCompileDependencies true
                maven(MavenPublication) {
                    from project.components.android
                }
            }
        }
        project.evaluate()
        then:
        project.useCompileDependencies == true
        project.components.android.usages[0].usage == Usage.FOR_COMPILE
    }

    def 'get dependencies from default configuration'() {
        when:
        project.repositories {
            jcenter()
        }
        project.android {
            compileSdkVersion 26
            buildToolsVersion '26.0.0'
        }
        project.dependencies {
            compile 'com.google.code.gson:gson:2.8.1'
        }
        project.evaluate()
        then:
        !component.usages[0].dependencies.isEmpty()
        ((ModuleDependency) component.usages[0].dependencies[0]).getGroup() == 'com.google.code.gson'
        ((ModuleDependency) component.usages[0].dependencies[0]).getName() == 'gson'
    }

    def 'dependencies for build type'() {
        when:
        project.repositories {
            jcenter()
        }
        project.android {
            defaultPublishConfig 'debug'
            compileSdkVersion 26
            buildToolsVersion '26.0.0'
        }
        project.dependencies {
            releaseCompile 'com.google.code.gson:gson:2.8.1'
        }
        project.evaluate()

        then:
        component.usages[0].dependencies.isEmpty()
    }

    def 'get default artifacts'() {
        when:
        project.android {
            compileSdkVersion 26
            buildToolsVersion '26.0.0'
        }
        project.evaluate()
        then:
        component.usages[0].artifacts.size() == 1
        component.usages[0].artifacts[0].extension == 'aar'
    }

    def 'get non default artifacts'() {
        when:
        project.android {
            publishNonDefault true // Deprecated it has no effect
            compileSdkVersion 26
            buildToolsVersion '26.0.0'

            buildTypes {
                debug {}
            }
        }
        project.evaluate()
        then:
        component.usages[0].artifacts.size() == 1
    }
}
