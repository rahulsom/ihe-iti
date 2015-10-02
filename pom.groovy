project {
  modelVersion '4.0.0'
  groupId 'com.github.rahulsom'
  artifactId 'ihe-iti'
  version '0.10-SNAPSHOT'
  name 'IHE ITI'
  description 'Codegen for IHE ITI Profiles.'
  url 'https://rahulsom.github.com/ihe-iti'
  licenses {
    license {
      name 'The Apache Software License, Version 2.0'
      url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
      distribution 'repo'
      comments 'A business-friendly OSS license'
    }
  }
  developers {
    developer {
      id 'rahulsom'
      name 'Rahul Somasunderam'
      email 'rahul.som@gmail.com'
    }
  }
  mailingLists {
    mailingList {
      name 'Main List'
      subscribe 'ihe-iti-subscribe@googlegroups.com'
      unsubscribe 'ihe-iti-unsubscribe@googlegroups.com'
      post 'ihe-iti@googlegroups.com'
      archive 'https://groups.google.com/d/forum/ihe-iti'
    }
  }
  scm {
    connection 'scm:git:git@github.com:rahulsom/ihe-iti.git'
    developerConnection 'scm:git:git@github.com:rahulsom/ihe-iti.git'
    url 'https://github.com/rahulsom/ihe-iti.git'
  }
  issueManagement {
    system 'GitHub'
    url 'https://github.com/rahulsom/ihe-iti/issues'
  }
  ciManagement {
    system 'travis'
    url 'https://travis-ci.org/rahulsom/ihe-iti'
  }
  distributionManagement {
    repository {
      id 'sonatype-nexus-staging'
      name 'Nexus OSS Staging'
      url 'https://oss.sonatype.org/service/local/staging/deploy/maven2'
    }
    snapshotRepository {
      id 'nexus-oss-snapshot'
      name 'Nexus OSS Snapshots'
      url 'https://oss.sonatype.org/content/repositories/snapshots'
    }
  }
  properties {
    'jaxws.plugin.version' '2.2'
    'junit.version' '4.11'
    'jaxb.basics.version' '0.6.4'
    'gec.version' '2.9.1-01'
    'project.reporting.outputEncoding' 'UTF-8'
    'project.build.sourceEncoding' 'UTF-8'
    'cxf.version' '2.2.3'
    'jaxb.plugin.version' '0.9.0'
    'jaxb.impl.version' '2.2.6'
    'groovy.version' '2.4.5'
  }
  dependencies {
    dependency {
      groupId 'junit'
      artifactId 'junit'
      version '${junit.version}'
      scope 'test'
    }
    dependency {
      groupId 'org.codehaus.groovy'
      artifactId 'groovy-all'
      version '${groovy.version}'
      scope 'provided'
      optional 'true'
    }
    dependency {
      groupId 'com.sun.xml.bind'
      artifactId 'jaxb-impl'
      version '${jaxb.impl.version}'
      scope 'provided'
    }
    dependency {
      groupId 'org.jvnet.jaxb2_commons'
      artifactId 'jaxb2-basics-runtime'
      version '${jaxb.basics.version}'
    }
    dependency {
      groupId 'xmlunit'
      artifactId 'xmlunit'
      version '1.4'
      scope 'test'
    }
    dependency {
      groupId 'commons-lang'
      artifactId 'commons-lang'
      version '2.6'
    }
    dependency {
      groupId 'org.spockframework'
      artifactId 'spock-core'
      version '0.7-groovy-2.0'
      scope 'test'
    }
    dependency {
      groupId 'co.freeside'
      artifactId 'betamax'
      version '1.1.2'
      scope 'test'
    }
    dependency {
      groupId 'org.apache.cxf'
      artifactId 'cxf-rt-frontend-jaxws'
      version '${cxf.version}'
      scope 'test'
    }
    dependency {
      groupId 'org.apache.cxf'
      artifactId 'cxf-rt-transports-http'
      version '${cxf.version}'
      scope 'test'
    }
  }
  build {
    plugins {
      plugin {
        groupId 'com.rimerosolutions.maven.plugins'
        artifactId 'wrapper-maven-plugin'
        version '0.0.4'
        configuration {
          mavenVersion '3.3.2'
        }
      }
      plugin {
        groupId 'org.jvnet.jax-ws-commons'
        artifactId 'jaxws-maven-plugin'
        version '${jaxws.plugin.version}'
        executions {
          execution {
            goals {
              goal 'wsimport'
            }
            configuration {
              vmArgs {
                vmArg '-Djavax.xml.accessExternalSchema=all'
              }
              catalog '${basedir}/src/main/resources/ihe-iti.cat'
              bindingFiles {
                bindingFile '${basedir}/src/main/resources/iti/wsdlbind/jaxws.xjb'
              }
              wsdlDirectory '${basedir}/src/main/resources/iti/wsdl'
              wsdlFiles {
                wsdlFile 'PDQSupplier.wsdl'
                wsdlFile 'PIXConsumer.wsdl'
                wsdlFile 'PIXManager.wsdl'
                wsdlFile 'RFDFormManager.wsdl'
                wsdlFile 'SVS_ValueSetRepository.wsdl'
                wsdlFile 'XCAInitiatingGatewayQuery.wsdl'
                wsdlFile 'XCAInitiatingGatewayRetrieve.wsdl'
                wsdlFile 'XCARespondingGatewayQuery.wsdl'
                wsdlFile 'XCARespondingGatewayRetrieve.wsdl'
                wsdlFile 'XCPDInitiatingGateway.wsdl'
                wsdlFile 'XCPDRespondingGateway.wsdl'
                wsdlFile 'XDS.b_DocumentRegistry.wsdl'
                wsdlFile 'XDS.b_DocumentRepository.wsdl'
                wsdlFile 'XDS-I.b_ImagingDocumentSource.wsdl'
              }
              extension 'true'
              xadditionalHeaders 'true'
              sourceDestDir '${project.build.directory}/generated-sources/ihe'
              xjcArgs {
                xjcArg '-Xfluent-api'
                xjcArg '-Xcommons-lang'
              }
            }
          }
        }
        dependencies {
          dependency {
            groupId 'org.jvnet.jaxb2_commons'
            artifactId 'jaxb2-fluent-api'
            version '3.0'
          }
          dependency {
            groupId 'org.jvnet.jaxb2_commons'
            artifactId 'jaxb2-commons-lang'
            version '2.3'
          }
        }
      }
      plugin {
        groupId 'org.jvnet.jaxb2.maven2'
        artifactId 'maven-jaxb22-plugin'
        version '${jaxb.plugin.version}'
        executions {
          execution {
            id 'iti'
            goals {
              goal 'generate'
            }
            configuration {
              catalog '${basedir}/src/main/resources/ihe-iti.cat'
              forceRegenerate 'true'
              schemaDirectory 'src/main/resources/iti/schema'
              schemaIncludes {
                include '**/*.xsd'
              }
              schemaExcludes {
                exclude '**/XDW-2012-04-02.xsd'
                exclude '**/XDW/POCD_MT000040.xsd'
                exclude '**/PRPA_IN201303UV02.xsd'
                exclude '**/SVS.xsd'
                exclude '**/ESVS-20100726.xsd'
                exclude '**/XCPD_PLQ.xsd'
                exclude '**/XDS.b_DocumentRepository.xsd'
                exclude '**/XDSI.b_ImagingDocumentSource.xsd'
              }
              bindingDirectory '${basedir}/src/main/resources/iti/bindings'
              generateDirectory '${project.build.directory}/generated-sources/ihe'
            }
          }
          execution {
            id 'cda'
            goals {
              goal 'generate'
            }
            configuration {
              forceRegenerate 'true'
              schemaDirectory 'src/main/resources/cda/infrastructure/cda'
              schemaIncludes {
                include '**/*.xsd'
              }
              bindingDirectory '${basedir}/src/main/resources/cda/bindings'
              generateDirectory '${project.build.directory}/generated-sources/cda'
              generatePackage 'com.github.rahulsom.cda'
            }
          }
          [
              X12_837P: '837_Q1_Pro.xsd',
              X12_837I: '837_Q3_Ins.xsd',
          ].each { k, v ->
            execution {
              id k
              goals {
                goal 'generate'
              }
              configuration {
                forceRegenerate 'true'
                schemaDirectory "src/main/resources/x12"
                schemaIncludes {
                  include v
                }
                // bindingDirectory '${basedir}/src/main/resources/cda/bindings'
                generateDirectory "\${project.build.directory}/generated-sources/${k}".toString()
                generatePackage "com.github.rahulsom.${k}".toString()
              }
            }
          }

        }
        configuration {
          plugins {
            plugin {
              groupId 'org.jvnet.jaxb2_commons'
              artifactId 'jaxb2-fluent-api'
              version '3.0'
            }
            plugin {
              groupId 'org.jvnet.jaxb2_commons'
              artifactId 'jaxb2-commons-lang'
              version '2.3'
            }
          }
          args {
            arg '-Xfluent-api'
            arg '-Xcommons-lang'
            arg '-npa'
          }
        }
      }
      plugin {
        artifactId 'maven-compiler-plugin'
        version '3.1'
        dependencies {
          dependency {
            groupId 'org.codehaus.groovy'
            artifactId 'groovy-eclipse-compiler'
            version '${gec.version}'
          }
          dependency {
            groupId 'org.codehaus.groovy'
            artifactId 'groovy-eclipse-batch'
            version '2.3.7-01'
          }
        }
        configuration {
          compilerId 'groovy-eclipse-compiler'
        }
      }
      plugin {
        groupId 'org.codehaus.groovy'
        artifactId 'groovy-eclipse-compiler'
        version '${gec.version}'
        extensions 'true'
      }
      plugin {
        artifactId 'maven-source-plugin'
        version '2.2.1'
        executions {
          execution {
            id 'attach-sources'
            goals {
              goal 'jar'
            }
          }
        }
      }
      plugin {
        artifactId 'maven-javadoc-plugin'
        version '2.9'
        executions {
          execution {
            id 'attach-javadocs'
            goals {
              goal 'jar'
            }
            configuration {
              additionalparam '-Xdoclint:none'
            }
          }
        }
        configuration {
          quiet 'true'
          maxmemory '800m'
        }
      }
      plugin {
        groupId 'com.github.damage-control.report'
        artifactId 'damage-control-maven-plugin'
        version '1.1.0'
        executions {
          execution {
            phase 'test'
            goals {
              goal 'report'
            }
          }
        }
        configuration {
          skip 'false'
          testResultsFolders {
            testResultsFolder 'target/surefire-reports'
            testResultsFolder 'target/failsafe-reports'
          }
        }
      }
      plugin {
        artifactId 'maven-surefire-plugin'
        version '2.11'
        configuration {
          testFailureIgnore 'true'
          includes {
            include '**/*Spec.class'
          }
        }
      }
      plugin {
        artifactId 'maven-failsafe-plugin'
        version '2.16'
        executions {
          execution {
            phase 'test'
            goals {
              goal 'integration-test'
            }
          }
        }
      }
    }
  }
  reporting {
    plugins {
      plugin {
        artifactId 'maven-javadoc-plugin'
        version '2.9'
        configuration {
          maxmemory '512m'
          quiet 'true'
        }
      }
    }
  }
}
