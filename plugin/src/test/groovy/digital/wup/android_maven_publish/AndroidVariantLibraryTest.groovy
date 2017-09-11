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

import org.gradle.api.attributes.Usage
import org.gradle.util.TextUtil

class AndroidVariantLibraryTest extends AbstractProjectBuilderSpec {

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
    }

    def 'android library components added by build variant'() {
        when:
        project.android {
            compileSdkVersion 26
            buildToolsVersion '26.0.0'
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
            buildToolsVersion '26.0.0'
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
