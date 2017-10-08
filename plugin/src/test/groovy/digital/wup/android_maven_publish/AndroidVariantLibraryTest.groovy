package digital.wup.android_maven_publish

import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.attributes.Usage
import org.gradle.util.TextUtil

class AndroidVariantLibraryTest extends AbstractProjectBuilderSpec {

    AndroidVariantLibrary component

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

    def 'get dependencies from compile configuration'() {
        when:
        project.repositories {
            jcenter()
        }
        project.android {
            compileSdkVersion 26
            buildToolsVersion '26.0.2'
        }
        project.dependencies {
            compile 'com.google.code.gson:gson:2.8.1'
        }
        project.evaluate()
        def usage = component.usages.find { it.getUsage() == Usage.FOR_COMPILE }
        then:
        !usage.dependencies.isEmpty()
        ((ModuleDependency) usage.dependencies[0]).getGroup() == 'com.google.code.gson'
        ((ModuleDependency) usage.dependencies[0]).getName() == 'gson'
    }

    def 'get dependencies from api configuration'() {
        when:
        project.repositories {
            jcenter()
        }
        project.android {
            compileSdkVersion 26
            buildToolsVersion '26.0.2'
        }
        project.dependencies {
            api 'com.google.code.gson:gson:2.8.1'
        }
        project.evaluate()

        def usage = component.usages.find { it.getUsage() == Usage.FOR_COMPILE }
        then:
        !usage.dependencies.isEmpty()
        ((ModuleDependency) usage.dependencies[0]).getGroup() == 'com.google.code.gson'
        ((ModuleDependency) usage.dependencies[0]).getName() == 'gson'
    }

    def 'get dependencies from implementation configuration'() {
        when:
        project.repositories {
            jcenter()
        }
        project.android {
            compileSdkVersion 26
            buildToolsVersion '26.0.2'
        }
        project.dependencies {
            implementation 'com.google.code.gson:gson:2.8.1'
        }
        project.evaluate()
        def usage = component.usages.find { it.getUsage() == Usage.FOR_RUNTIME }
        then:

        !usage.dependencies.isEmpty()
        ((ModuleDependency) usage.dependencies[0]).getGroup() == 'com.google.code.gson'
        ((ModuleDependency) usage.dependencies[0]).getName() == 'gson'
    }

    def 'dependencies for build type'() {
        when:
        project.repositories {
            jcenter()
        }
        project.android {
            defaultPublishConfig 'debug'
            compileSdkVersion 26
            buildToolsVersion '26.0.2'
        }
        project.dependencies {
            releaseCompile 'com.google.code.gson:gson:2.8.1'
        }
        project.evaluate()

        def usage = component.usages.find { it.getUsage() == Usage.FOR_COMPILE }
        then:
        usage.dependencies.isEmpty()
    }

    def 'get default artifacts'() {
        when:
        project.android {
            compileSdkVersion 26
            buildToolsVersion '26.0.2'
        }
        project.evaluate()
        then:
        !component.usages[0].artifacts.isEmpty()
        component.usages[0].artifacts[0].extension == 'aar'
    }

    def 'android library components added by build variant'() {
        when:
        project.android {
            compileSdkVersion 26
            buildToolsVersion '26.0.2'
            defaultPublishConfig 'prodRelease'

            flavorDimensions "color"
            productFlavors {
                dev {
                    dimension 'color'
                }
                prod {
                    dimension 'color'
                }
            }
        }
        project.evaluate()
        then:
        project.components.androidDevDebug != null
        project.components.androidDevRelease != null
        project.components.androidProdDebug != null
        project.components.androidProdRelease != null
    }

    def 'test variant dependencies'() {
        when:
        project.android {
            compileSdkVersion 26
            buildToolsVersion '26.0.2'
            defaultPublishConfig 'prodRelease'

            flavorDimensions "color"
            productFlavors {
                dev {
                    dimension 'color'
                }
                prod {
                    dimension 'color'
                }
            }
        }

        project.dependencies {
            devImplementation 'com.google.code.gson:gson:2.8.1'
        }
        project.evaluate()

        def usageDevRelease = project.components.androidDevRelease.usages.find { it.getUsage() == Usage.FOR_RUNTIME }
        def usageProdRelease = project.components.androidProdRelease.usages.find { it.getUsage() == Usage.FOR_RUNTIME }

        then:
        !usageDevRelease.dependencies.isEmpty()
        usageProdRelease.dependencies.isEmpty()
    }
}
