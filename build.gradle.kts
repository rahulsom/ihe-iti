import com.github.rahulsom.waena.WaenaExtension
import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
  id("java-library")
  alias(libs.plugins.waena.root)
  alias(libs.plugins.waena.published)
  alias(libs.plugins.testLogger)
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
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

  testImplementation(platform(libs.junit.bom))
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.junit.params)
  testImplementation(libs.xmlunit)
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

abstract class GenerateCodeTask : DefaultTask() {
    @get:InputDirectory
    abstract val resourcesDir: DirectoryProperty

    @get:InputDirectory
    abstract val itiSchemasDir: DirectoryProperty

    @get:OutputDirectory
    abstract val cdaOutputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val iheOutputDir: DirectoryProperty

    @get:Classpath
    abstract val jaxbClasspath: ConfigurableFileCollection

    @get:Inject
    abstract val execOps: ExecOperations

    @TaskAction
    fun generate() {
        cdaOutputDir.get().asFile.mkdirs()
        iheOutputDir.get().asFile.mkdirs()

        val xjcCommonArgs = listOf("-extension", "-Xfluent-api", "-Xcommons-lang")

        // CDA
        print("Generating code from CDA schemas...")
        execOps.javaexec {
            mainClass.set("com.sun.tools.xjc.Driver")
            classpath = jaxbClasspath
            args = xjcCommonArgs + listOf(
                "-d", cdaOutputDir.get().asFile.absolutePath,
                "-b", project.file("src/main/resources/cda/bindings/bind.xjb").absolutePath,
                "-p", "com.github.rahulsom.cda",
                "-quiet",
                project.file("src/main/resources/cda/infrastructure/cda").absolutePath
            )
        }
        println("... Done.")

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
            print("Generating code from $wsdlFile ...")
            execOps.javaexec {
                mainClass.set("com.sun.tools.ws.WsImport")
                classpath = jaxbClasspath
                systemProperty("javax.xml.accessExternalSchema", "all")
                args(
                    "-catalog", project.file("src/main/resources/ihe-iti.cat.xml").absolutePath,
                    "-b", project.file("src/main/resources/iti/bindings/bind.xjb").absolutePath,
                    "-extension",
                    "-B-Xfluent-api",
                    "-B-Xcommons-lang",
                    "-B-npa",
                    "-Xnocompile",
                    "-keep",
                    "-quiet",
                    "-s", iheOutputDir.get().asFile.absolutePath,
                    project.file("src/main/resources/iti/wsdl/$wsdlFile").absolutePath
                )
            }
            println("... Done.")
        }

        // ITI
        print("Generating code from ITI schemas...")
        execOps.javaexec {
            mainClass.set("com.sun.tools.xjc.Driver")
            classpath = jaxbClasspath
            args = xjcCommonArgs + listOf(
                "-npa",
                "-d", iheOutputDir.get().asFile.absolutePath,
                "-b", project.layout.buildDirectory.file("iti-schemas/iti/bindings/bind.xjb").get().asFile.absolutePath,
                "-quiet",
                project.layout.buildDirectory.dir("iti-schemas/iti/schema").get().asFile.absolutePath
            )
        }
        println("... Done.")
    }
}

val generateCode = tasks.register<GenerateCodeTask>("generateCode") {
    dependsOn("syncItiSchemas")

    resourcesDir.set(layout.projectDirectory.dir("src/main/resources"))
    itiSchemasDir.set(layout.buildDirectory.dir("iti-schemas"))
    cdaOutputDir.set(layout.buildDirectory.dir("generated-sources/cda"))
    iheOutputDir.set(layout.buildDirectory.dir("generated-sources/ihe"))
    jaxbClasspath.from(jaxb)
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

testlogger {
  theme = if (System.getProperty("idea.active") == "true") ThemeType.PLAIN else ThemeType.MOCHA
  slowThreshold = 5000

}
