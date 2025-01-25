plugins {
    id("java")
}

group = "big_data.january"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:3.4.0")
    implementation ("org.slf4j:slf4j-simple:1.7.36")
    implementation ("org.apache.httpcomponents:httpclient:4.5.13")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}