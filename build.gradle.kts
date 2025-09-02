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

val jaxb by configurations.creating

dependencies {
  jaxb("org.jvnet.jaxb2_commons:jaxb2-fluent-api:3.0")
  jaxb("org.jvnet.jaxb2_commons:jaxb2-commons-lang:2.3")
  jaxb("com.sun.xml.bind:jaxb-xjc:2.3.9")

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
      srcDir("${layout.buildDirectory.get()}/generated-sources/iti")
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

tasks.register("syncItiSchemas", Sync::class) {
  from("src/main/resources")
  into("${layout.buildDirectory.get()}/iti-schemas")
  include("iti/**", "w3/**", "ihe-iti.cat")

  exclude("**/XDW-2012-04-02.xsd")
  exclude("**/XDW/POCD_MT000040.xsd")
  exclude("**/PRPA_IN201303UV02.xsd")
  exclude("**/SVS.xsd")
  exclude("**/ESVS-20100726.xsd")
  exclude("**/XCPD_PLQ.xsd")
  exclude("**/XDS.b_DocumentRepository.xsd")
  exclude("**/XDSI.b_ImagingDocumentSource.xsd")
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

tasks.register("generateIti", JavaExec::class) {
  mainClass.set("com.sun.tools.xjc.Driver")
  classpath = jaxb
  args(
    "-extension",
    "-Xfluent-api",
    "-Xcommons-lang",
    "-npa",
    "-d", "target/generated-sources/ihe",
    "-b", "${layout.buildDirectory.get()}/iti-schemas/iti/bindings/bind.xjb",
    "-quiet",
    "${layout.buildDirectory.get()}/iti-schemas/iti/schema"
  )
  doFirst {
    file("target/generated-sources/ihe").mkdirs()
  }
  inputs.dir("${layout.buildDirectory.get()}/iti-schemas")
  outputs.dir("target/generated-sources/ihe")
  dependsOn("syncItiSchemas")
}

tasks.named("compileJava").configure {
  dependsOn("generateCda", "generateIti")
}
tasks.named("sourceJar").configure {
  dependsOn("generateCda", "generateIti")
}
