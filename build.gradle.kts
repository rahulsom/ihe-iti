import com.github.rahulsom.waena.WaenaExtension

plugins {
  id("java-library")
  id("groovy")
  alias(libs.plugins.waena.root)
  alias(libs.plugins.waena.published)
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

repositories {
  mavenCentral()
}

val jaxb by configurations.creating

dependencies {
  jaxb(libs.jaxb.fluent.api)
  jaxb(libs.jaxb.commons.lang)
  jaxb(libs.jaxb.xjc)
  jaxb(libs.jakarta.jws.api)
  jaxb(libs.jaxws.tools)

  api(libs.jaxb.basics.runtime)
  api(libs.commons.lang3)
  api(libs.jaxb.impl)

  compileOnly(libs.jakarta.xml.bind.api)
  compileOnly(libs.jakarta.xml.ws.api)

  testImplementation(libs.junit)
  testImplementation(libs.xmlunit)
  testImplementation(libs.spock.core)
  testImplementation(libs.jakarta.xml.ws.api)
  testImplementation(libs.jaxws.rt)
  testImplementation(libs.cxf.rt.frontend.jaxws)
  testImplementation(libs.cxf.rt.transports.http)
  testImplementation(libs.jetty.server)
  testImplementation(libs.servlet.api)
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

val generateCode = tasks.register("generateCode") {
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
        project.javaexec {
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
            project.javaexec {
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
        project.javaexec {
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
  inputs.files(generateCode)
}
tasks.named("sourcesJar").configure {
  inputs.files(generateCode)
}

waena {
  license.set(WaenaExtension.License.Apache2)
  publishMode.set(WaenaExtension.PublishMode.Central)
}
