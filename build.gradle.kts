// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("room_version", "2.6.1")
        set("nav_version", "2.7.7")
    }

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }
}

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.0.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.21-1.0.15" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}