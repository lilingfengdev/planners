plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.40"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
//    kotlin("jvm") version "1.0.0"
}

taboolib {
    install("common")
    install("common-5")
    install("module-ai")
    install("module-chat")
    install("module-configuration")
    install("module-database")
    install("module-metrics")
//    install("module-effect")
    install("module-kether")
    install("module-lang")
    install("module-navigation")
    install("module-navigation")
    install("module-nms")
    install("module-nms-util")
    install("module-ui")
    install("expansion-command-helper")
    install("platform-bukkit")
    classifier = null
    version = "6.0.9-58"
    description {
        dependencies {
            name("PlaceholderAPI").optional(true)
            name("OriginAttribute").optional(true)
            name("AttributeSystem").optional(true)
            name("AttributePlus").optional(true)
            name("SX-Attribute").optional(true)
        }
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {

    compileOnly("public:ModelEngine:2.5.1")
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11800:11800-minimize:api")
    compileOnly("ink.ptms.core:v11800:11800-minimize:mapped")
    compileOnly("com.google.code.gson:gson:2.8.5")
    compileOnly("com.google.guava:guava:21.0")
    compileOnly("com.elmakers.mine.bukkit:EffectLib:9.4")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.withType<Jar> {
    destinationDir = file("F:/Server/Spigot 1.12.2 - 赏金测试/plugins")
//    destinationDir = file("F:/Server/purpur 1.18.2/plugins")
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        maven { url = uri("https://repo1.maven.org/maven2") }
        maven { url = uri("http://maven.elmakers.com/repository") }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}
