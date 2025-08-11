plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "cz.cuni.mff"
version = "1.1"

repositories {
    mavenLocal()
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }

}

dependencies {
    implementation("cz.cuni.mff:UMLift-core:1.0-SNAPSHOT") {
    }
    intellijPlatform {
        intellijIdeaCommunity("2025.2")
        bundledPlugin("com.intellij.java")

        pluginVerifier()
        zipSigner()
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    patchPluginXml {
        sinceBuild.set("242.23726.103")
        untilBuild.set("252.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
