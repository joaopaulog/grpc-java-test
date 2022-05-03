import com.google.protobuf.gradle.*

plugins {
    java
    idea
    id("com.google.protobuf") version "0.8.18"
    application
}

repositories {
    mavenCentral()
}

val grpcVersion = "1.46.0"
val grpcKotlinVersion = "1.2.1"
val protobufVersion = "3.20.1"

dependencies {
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("io.grpc:grpc-api:$grpcVersion")
    implementation("com.google.protobuf:protobuf-java-util:$protobufVersion")
    runtimeOnly("io.grpc:grpc-netty:$grpcVersion")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("build/generated/source/proto/main/grpc")
            srcDir("build/generated/source/proto/main/java")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("Main")
}
