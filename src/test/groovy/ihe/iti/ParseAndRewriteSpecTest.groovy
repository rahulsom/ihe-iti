package ihe.iti

import ihe.iti.svs._2008.RetrieveValueSetRequestType
import ihe.iti.svs._2008.RetrieveValueSetResponseType
import ihe.util.XmlTestHelper
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse
import org.hl7.v3.*
import spock.lang.Specification
import spock.lang.Unroll

import javax.xml.bind.JAXBElement

/**
 * Created by rahulsomasunderam on 2/27/14.
 */
class ParseAndRewriteSpecTest extends Specification {

  @Unroll
  def "#profile: #file can be parsed to #clazz and recreated"() {

    expect:
    parse("${profile}/${file}").class == clazz && !XmlTestHelper.getIrrecoverableDifferences(
        parse("${profile}/${file}"), "${profile}/${file}"
    )

    where:
    profile | file                                                              | clazz
    'PDQV3' | '01_PDQQuery1.xml'                                                | PRPAIN201305UV02
    'PDQV3' | '01_PDQQuery1_20110328.xml'                                       | PRPAIN201305UV02
    'PDQV3' | '02_PDQQuery1Response.xml'                                        | PRPAIN201306UV02
    'PDQV3' | '02_PDQQuery1Response_20110328.xml'                               | PRPAIN201306UV02
    'PDQV3' | '02_PDQQuery1Response_20110504.xml'                               | PRPAIN201306UV02
    'PDQV3' | '03_PDQQuery1Continuation.xml'                                    | QUQIIN000003UV01Type
    'PDQV3' | '03_PDQQuery1Continuation-1_20110328.xml'                         | QUQIIN000003UV01Type
    'PDQV3' | '04_PDQQuery1ContinuationResponse.xml'                            | PRPAIN201306UV02
    'PDQV3' | '04_PDQQuery1ContinuationResponse_20110328.xml'                   | PRPAIN201306UV02
    'PDQV3' | '04_PDQQuery1ContinuationResponse_20110504.xml'                   | PRPAIN201306UV02
    'PDQV3' | '05_PDQQuery1Cancel.xml'                                          | QUQIIN000003UV01Type
    'PDQV3' | '05_PDQQuery1Cancel_20110328.xml'                                 | QUQIIN000003UV01Type
    'PDQV3' | '06_PDQQuery1CancelAck.xml'                                       | MCCIIN000002UV01
    'PDQV3' | '06_PDQQuery1CancelAck_20110328.xml'                              | MCCIIN000002UV01

    'PIXV3' | '01_PatientRegistryRecordAdded1.xml'                              | PRPAIN201301UV02
    'PIXV3' | '01_PatientRegistryRecordAdded1_20110228.xml'                     | PRPAIN201301UV02
    'PIXV3' | '01_PatientRegistryRecordAdded1_20110328.xml'                     | PRPAIN201301UV02
    'PIXV3' | '01_PatientRegistryRecordAdded1_20110504.xml'                     | PRPAIN201301UV02
    'PIXV3' | '02_PatientRegistryRecordAdded1Ack.xml'                           | MCCIIN000002UV01
    'PIXV3' | '02_PatientRegistryRecordAdded1Ack_20110228.xml'                  | MCCIIN000002UV01
    'PIXV3' | '02_PatientRegistryRecordAdded1Ack_20110328.xml'                  | MCCIIN000002UV01
    'PIXV3' | '03_PatientRegistryRecordAdded2.xml'                              | PRPAIN201301UV02
    'PIXV3' | '03_PatientRegistryRecordAdded2_20110228.xml'                     | PRPAIN201301UV02
    'PIXV3' | '03_PatientRegistryRecordAdded2_20110328.xml'                     | PRPAIN201301UV02
    'PIXV3' | '04_PatientRegistryRecordRevised2.xml'                            | PRPAIN201302UV02
    'PIXV3' | '04_PatientRegistryRecordRevised2_20110228.xml'                   | PRPAIN201302UV02
    'PIXV3' | '04_PatientRegistryRecordRevised2_20110328.xml'                   | PRPAIN201302UV02
    'PIXV3' | '05_PatientRegistryDuplicatesResolved.xml'                        | PRPAIN201304UV02
    'PIXV3' | '05_PatientRegistryDuplicatesResolved_20110328.xml'               | PRPAIN201304UV02
    'PIXV3' | '05_PatientRegistryDuplicatesResolved_20110504.xml'               | PRPAIN201304UV02
    'PIXV3' | '06_PIXQuery1.xml'                                                | PRPAIN201309UV02
    'PIXV3' | '06_PIXQuery1_20110228.xml'                                       | PRPAIN201309UV02
    'PIXV3' | '06_PIXQuery1_20110328.xml'                                       | PRPAIN201309UV02
    'PIXV3' | '07_PIXQuery1Response.xml'                                        | PRPAIN201310UV02
    'PIXV3' | '07_PIXQuery1Response_20110228.xml'                               | PRPAIN201310UV02
    'PIXV3' | '07_PIXQuery1Response_20110328.xml'                               | PRPAIN201310UV02

    'SVS'   | 'RetrieveValueSetRequest.xml'                                     | RetrieveValueSetRequestType
    'SVS'   | 'RetrieveValueSetResponse.xml'                                    | RetrieveValueSetResponseType

    'XCPD'  | 'XCPDCrossGatewayPatientDiscoveryResponseAEError.xml'             | PRPAIN201306UV02
    'XCPD'  | 'XCPDCrossGatewayPatientDiscoveryResponseForMoreDemographics.xml' | PRPAIN201306UV02

    'XDS.b' | 'RegisterDocumentSet-bRequest.xml'                                | SubmitObjectsRequest
    'XDS.b' | 'RegistryStoredQueryRequest.xml'                                  | AdhocQueryRequest
    'XDS.b' | 'RegistryStoredQueryResponse.xml'                                 | AdhocQueryResponse
    // TODO XCA
    // TODO XCPD Missing
    // TODO XDS.b Missing
    // TODO XDW

  }

  def parse(String file) {
    def jaxbContext = XmlTestHelper.jaxbContext
    def obj = jaxbContext.createUnmarshaller().unmarshal(
        this.class.classLoader.getResourceAsStream(file)
    )
    if (obj instanceof JAXBElement) {
      obj.value
    } else {
      obj
    }
  }
}
