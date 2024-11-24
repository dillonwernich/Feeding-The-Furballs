pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {url = uri ("https://jitpack.io")} //add
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {url = uri ("https://jitpack.io")} //add
    }
}

rootProject.name = "FeedingTheFurballs"
include(":app")
