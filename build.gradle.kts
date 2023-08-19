plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.56"
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
}

taboolib {

    install("common")
    install("common-5")
    install("module-ai")
    install("module-chat")
    install("module-configuration")
    install("module-database")
    install("module-metrics")
    install("module-effect")
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
    version = "6.0.11-31"
    description {
        contributors {
            name("洋洋")
            name("纸杯")
        }
        dependencies {
            name("PlaceholderAPI").optional(true)
            name("OriginAttribute").optional(true)
            name("AttributeSystem").optional(true)
            name("AttributePlus").optional(true)
            name("SX-Attribute").optional(true)
            name("WorldGuard").optional(true)
            name("DragonCore").optional(true)
            name("GermPlugin").optional(true)
            name("VirtualEntitySkillModule").optional(true)
            name("GDDTitle").optional(true)
            name("Antikey").optional(true)
            name("DragonCollect").optional(true)
            name("GlowAPI").optional(true)
            name("MonsterItem").optional(true)
        }
    }
    options("skip-kotlin-relocate", "keep-kotlin-module")
}
repositories {
    mavenCentral()
}

dependencies {

    compileOnly("ink.ptms.core:v11900:11900:mapped")
    compileOnly("ink.ptms.core:v11900:11900:universal")
    compileOnly("ink.ptms.core:v11200:11200")
    compileOnly("ink.ptms:nms-all:1.0.0")

    compileOnly("public:ModelEngine:2.5.1")
    compileOnly("com.google.code.gson:gson:2.8.9")
    compileOnly("com.google.guava:guava:30.0-android")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.2.1")

    compileOnly("com.mojang:datafixerupper:4.0.26")

    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
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