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

import org.gradle.api.internal.project.ProjectInternal
import org.gradle.util.TextUtil
import spock.lang.Specification

abstract class AbstractProjectBuilderSpec extends Specification {

    protected File root

    protected ProjectInternal project
    private static int testFolderId = 0

    def 'setup'() {
        root = new File("build/tmp/test-app${testFolderId++}")
        root.mkdirs()
        cleanFolder(root)
        project = TestUtil.createRootProject(root)

        def srcFolder = createMainSourceSetFolder(root)
        createAndroidManifest(srcFolder, 'digital.wup.android_maven_publish.integ_test')

    }

    static File createMainSourceSetFolder(File target) {
        File srcFolder = new File(target, "src${File.separator}main")
        srcFolder.mkdirs()
        return srcFolder
    }

    static void createAndroidManifest(File target, String packageName) {
        File manifest = new File(target, 'AndroidManifest.xml')
        manifest.createNewFile()
        PrintWriter writer = new PrintWriter(manifest, 'UTF-8')
        writer.print(TextUtil.toPlatformLineSeparators("""<manifest
            package="$packageName">
            </manifest>"""))
        writer.close()
    }

    static void cleanFolder(File folder) {
        File[] files = folder.listFiles()
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    cleanFolder(f)
                } else {
                    f.delete()
                }
            }
        }
    }
}
