plugins {
  id("java-library")
  id("groovy")
  id("com.github.rahulsom.waena.root").version("0.6.1")
  id("com.github.rahulsom.waena.published").version("0.15.1")
}

repositories {
  mavenCentral()
}

val jaxb by configurations.creating

dependencies {
  jaxb("org.jvnet.jaxb2_commons:jaxb2-fluent-api:3.0")
  jaxb("org.jvnet.jaxb2_commons:jaxb2-commons-lang:2.3")
  jaxb("com.sun.xml.bind:jaxb-xjc:4.0.5")
  jaxb("jakarta.jws:jakarta.jws-api:3.0.0")
  jaxb("com.sun.xml.ws:jaxws-tools:4.0.3")

  api("org.jvnet.jaxb2_commons:jaxb2-basics-runtime:2.0.12")
  api("commons-lang:commons-lang:2.6")
  api("com.sun.xml.bind:jaxb-impl:4.0.5")

  compileOnly("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
  compileOnly("jakarta.xml.ws:jakarta.xml.ws-api:4.0.2")

  testImplementation("junit:junit:4.13.2")
  testImplementation("xmlunit:xmlunit:1.6")
  testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
  testImplementation("jakarta.xml.ws:jakarta.xml.ws-api:4.0.2")
  testImplementation("com.sun.xml.ws:jaxws-rt:4.0.3")
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

sourceSets {
  main {
    java {
      srcDir("${layout.buildDirectory.get()}/generated-sources/cda")
      srcDir("${layout.buildDirectory.get()}/generated-sources/iti")
      srcDir("${layout.buildDirectory.get()}/generated-sources/ihe")
    }
  }
}

tasks.withType<Javadoc>().configureEach {
  options {
    (this as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
  }
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

tasks.register("generateCode") {
    dependsOn("syncItiSchemas")

    val cdaOutputDir = layout.buildDirectory.dir("generated-sources/cda")
    val iheOutputDir = layout.buildDirectory.dir("generated-sources/ihe")
    val xjcCommonArgs = listOf("-extension", "-Xfluent-api", "-Xcommons-lang")

    inputs.dir("src/main/resources")
    inputs.dir(layout.buildDirectory.dir("iti-schemas"))
    outputs.dir(cdaOutputDir)
    outputs.dir(iheOutputDir)

    doLast {
        cdaOutputDir.get().asFile.mkdirs()
        iheOutputDir.get().asFile.mkdirs()

        // CDA
        javaexec {
            mainClass.set("com.sun.tools.xjc.Driver")
            classpath = jaxb
            args = xjcCommonArgs + listOf(
                "-d", cdaOutputDir.get().asFile.absolutePath,
                "-b", file("src/main/resources/cda/bindings/bind.xjb").absolutePath,
                "-p", "com.github.rahulsom.cda",
                "-quiet",
                file("src/main/resources/cda/infrastructure/cda").absolutePath
            )
        }

        // WSDL
        val wsdlFiles = listOf(
            "PDQSupplier.wsdl",
            "PIXConsumer.wsdl",
            "PIXManager.wsdl",
            "RFDFormManager.wsdl",
            "SVS_ValueSetRepository.wsdl",
            "XCAInitiatingGatewayQuery.wsdl",
            "XCAInitiatingGatewayRetrieve.wsdl",
            "XCARespondingGatewayQuery.wsdl",
            "XCARespondingGatewayRetrieve.wsdl",
            "XCPDInitiatingGateway.wsdl",
            "XCPDRespondingGateway.wsdl",
            "XDS.b_DocumentRegistry.wsdl",
            "XDS.b_DocumentRepository.wsdl",
            "XDS-I.b_ImagingDocumentSource.wsdl",
        )
        wsdlFiles.forEach { wsdlFile ->
            javaexec {
                mainClass.set("com.sun.tools.ws.WsImport")
                classpath = jaxb
                systemProperty("javax.xml.accessExternalSchema", "all")
                args(
                    "-catalog", file("src/main/resources/ihe-iti.cat.xml").absolutePath,
                    "-b", file("src/main/resources/iti/bindings/bind.xjb").absolutePath,
                    "-extension",
                    "-B-Xfluent-api",
                    "-B-Xcommons-lang",
                    "-B-npa",
                    "-Xnocompile",
                    "-keep",
                    "-quiet",
                    "-s", iheOutputDir.get().asFile.absolutePath,
                    file("src/main/resources/iti/wsdl/$wsdlFile").absolutePath
                )
            }
        }

        // ITI
        javaexec {
            mainClass.set("com.sun.tools.xjc.Driver")
            classpath = jaxb
            args = xjcCommonArgs + listOf(
                "-npa",
                "-d", iheOutputDir.get().asFile.absolutePath,
                "-b", layout.buildDirectory.file("iti-schemas/iti/bindings/bind.xjb").get().asFile.absolutePath,
                "-quiet",
                layout.buildDirectory.dir("iti-schemas/iti/schema").get().asFile.absolutePath
            )
        }
    }
}

tasks.named("compileJava").configure {
  dependsOn("generateCode")
}
tasks.named("sourceJar").configure {
  dependsOn("generateCode")
}
