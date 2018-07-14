import com.android.build.gradle.api.LibraryVariant

plugins {
    id("com.android.library")
    id("digital.wup.android-maven-publish")

}

repositories {
    google()
    jcenter()
}

android {
    compileSdkVersion(27)
    buildToolsVersion("27.0.3")
    defaultConfig {
        minSdkVersion(15)
        targetSdkVersion(27)
        versionCode = 1
        versionName = "${project.version}"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

publishing {
    (publications) {

        // Publish the release aar artifact
        "defaultAar"(MavenPublication::class) {
            from(components["android"])
            groupId = "digital.wup.android-maven-publish"
            version = "${project.version}"

        }
    }
}