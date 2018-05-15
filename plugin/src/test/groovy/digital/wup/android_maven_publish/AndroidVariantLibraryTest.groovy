package digital.wup.android_maven_publish

import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.attributes.Usage
import org.gradle.api.internal.model.DefaultObjectFactory
import org.gradle.api.internal.model.NamedObjectInstantiator
import org.gradle.api.model.ObjectFactory
import org.gradle.internal.reflect.DirectInstantiator
import org.gradle.util.TextUtil

class AndroidVariantLibraryTest extends AbstractProjectBuilderSpec {

    AndroidVariantLibrary component
    ObjectFactory objectFactory =  new DefaultObjectFactory(DirectInstantiator.INSTANCE, NamedObjectInstantiator.INSTANCE);
    Usage runtime
    Usage compile

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
        runtime = objectFactory.named(Usage.class, Usage.JAVA_RUNTIME);
        compile = objectFactory.named(Usage.class, Usage.JAVA_API);

    }

    def 'get dependencies from compile configuration'() {
        when:
        project.repositories {
            jcenter()
        }
        project.android {
            compileSdkVersion 27
            buildToolsVersion '27.0.3'
        }
        project.dependencies {
            compile 'com.google.code.gson:gson:2.8.1'
        }
        project.evaluate()
        def usage = component.usages.find { it.getUsage() == compile }
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
            compileSdkVersion 27
            buildToolsVersion '27.0.3'
        }
        project.dependencies {
            api 'com.google.code.gson:gson:2.8.1'
        }
        project.evaluate()

        def usage = component.usages.find { it.getUsage() == compile }
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
            compileSdkVersion 27
            buildToolsVersion '27.0.3'
        }
        project.dependencies {
            implementation 'com.google.code.gson:gson:2.8.1'
        }
        project.evaluate()
        def usage = component.usages.find { it.getUsage() == runtime }
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
            compileSdkVersion 27
            buildToolsVersion '27.0.3'
        }
        project.dependencies {
            releaseCompile 'com.google.code.gson:gson:2.8.1'
        }
        project.evaluate()

        def usage = component.usages.find { it.getUsage() == compile }
        then:
        usage.dependencies.isEmpty()
    }

    def 'get default artifacts'() {
        when:
        project.android {
            compileSdkVersion 27
            buildToolsVersion '27.0.3'
        }
        project.evaluate()
        then:
        !component.usages[0].artifacts.isEmpty()
        component.usages[0].artifacts[0].extension == 'aar'
    }

    def 'android library components added by build variant'() {
        when:
        project.android {
            compileSdkVersion 27
            buildToolsVersion '27.0.3'
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
            compileSdkVersion 27
            buildToolsVersion '27.0.3'
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

        def usageDevRelease = project.components.findByName('androidDevRelease').usages.find {
            it.getUsage() == runtime
        }
        def usageProdRelease = project.components.androidProdRelease.usages.find {
            it.getUsage() == runtime
        }

        then:
        !usageDevRelease.dependencies.isEmpty()
        usageProdRelease.dependencies.isEmpty()
    }
}
