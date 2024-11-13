plugins {
  id("java")
  id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "cz.cuni.mff.vojtusr"
version = "1.0-SNAPSHOT"

repositories {
  mavenLocal()
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
  }

}

dependencies {
  implementation("cz.cuni.mff.vojtusr:InformalMDD-core:1.0-SNAPSHOT") {
    exclude("org.slf4j", "slf4j-api")
  }
  intellijPlatform {
    intellijIdeaCommunity("2024.2.4")
    bundledPlugin("com.intellij.java")

    pluginVerifier()
    zipSigner()
    instrumentationTools()
  }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html


tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
  }

  patchPluginXml {
    sinceBuild.set("232")
    untilBuild.set("242.*")
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
