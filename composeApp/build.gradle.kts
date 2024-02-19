import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.javacv.platform)

            implementation(files("libs/tess4j-5.10.0.jar"))
            implementation("net.sourceforge.tess4j:tess4j:5.10.0") {
                exclude(group = "net.sourceforge.tess4j", module = "tess4j")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.jooheon.maple.potion.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.jooheon.maple.potion"
            packageVersion = "1.0.0"
        }
    }
}
