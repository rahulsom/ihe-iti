plugins {
  id("java-library")
  id("groovy")
  id("com.github.rahulsom.waena.root").version("0.6.1")
  id("com.github.rahulsom.waena.published").version("0.6.1")
  id("com.github.dkorotych.gradle-maven-exec").version("3.0.2")
}

repositories {
  mavenCentral()
}

dependencies {
  api("org.jvnet.jaxb2_commons:jaxb2-basics-runtime:2.0.12")
  api("commons-lang:commons-lang:2.6")
  compileOnly("javax.xml.bind:jaxb-api:2.3.1")
  compileOnly("com.sun.xml.bind:jaxb-core:2.3.0.1")
  testImplementation("junit:junit:4.13.2")
  testImplementation("xmlunit:xmlunit:1.6")
  testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
  testImplementation("org.apache.cxf:cxf-rt-frontend-jaxws:4.1.3")
  testImplementation("org.apache.cxf:cxf-rt-transports-http:4.1.3")
  testImplementation("org.eclipse.jetty:jetty-server:9.4.58.v20250814")
  testImplementation("javax.servlet:javax.servlet-api:4.0.1")
}

group = "com.github.rahulsom"
description = "IHE ITI"

configure<nebula.plugin.contacts.ContactsExtension> {
  validateEmails = true
  addPerson("rahulsom@noreply.github.com", delegateClosureOf<nebula.plugin.contacts.Contact> {
    moniker("Rahul Somasunderam")
    roles("owner")
    github("https://github.com/rahulsom")
  })
}

val generateClients = tasks.create<com.github.dkorotych.gradle.maven.exec.MavenExec>("generateClients") {
  goals("generate-sources")
  inputs.dir("src/main/resources")
  inputs.file("pom.xml")
  inputs.file("maven/maven-wrapper.properties")
  outputs.dir("target")
}

tasks.getByName("compileJava").dependsOn(generateClients)
tasks.getByName("sourceJar").dependsOn(generateClients)

sourceSets {
  main {
    java {
      srcDir("target/generated-sources/cda")
      srcDir("target/generated-sources/ihe")
    }
  }
}

tasks.withType<Javadoc>().configureEach {
  options {
    (this as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
  }
}

tasks.getByName<Delete>("clean") {
  delete.add("target")
}

tasks.getByName<Jar>("sourceJar") {
  exclude("**/sun-jaxb.episode")
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
