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
      srcDir("${layout.buildDirectory.get()}/generated-sources/cda")
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

val jaxb by configurations.creating

dependencies {
  jaxb("org.jvnet.jaxb2_commons:jaxb2-fluent-api:3.0")
  jaxb("org.jvnet.jaxb2_commons:jaxb2-commons-lang:2.3")
  jaxb("com.sun.xml.bind:jaxb-xjc:2.3.9")
}

tasks.register("generateCda", JavaExec::class) {
  mainClass.set("com.sun.tools.xjc.Driver")
  classpath = jaxb
  args(
    "-extension",
    "-Xfluent-api",
    "-Xcommons-lang",
    "-d", "${layout.buildDirectory.get()}/generated-sources/cda",
    "-b", "${layout.projectDirectory}/src/main/resources/cda/bindings/bind.xjb",
    "-p", "com.github.rahulsom.cda",
    "-quiet",
    "${layout.projectDirectory}/src/main/resources/cda/infrastructure/cda"
  )
  doFirst {
    file("${layout.buildDirectory.get()}/generated-sources/cda").mkdirs()
  }
  inputs.dir("src/main/resources/cda")
  outputs.dir("${layout.buildDirectory.get()}/generated-sources/cda")
}

tasks.named("compileJava").configure {
  dependsOn("generateCda")
}
tasks.named("sourceJar").configure {
  dependsOn("generateCda")
}
