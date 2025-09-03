package ihe.iti;

import com.github.rahulsom.cda.POCDMT000040ClinicalDocument;
import ihe.iti.svs._2008.RetrieveValueSetRequestType;
import ihe.iti.svs._2008.RetrieveValueSetResponseType;
import ihe.util.XmlTestHelper;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import org.hl7.v3.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by rahulsomasunderam on 2/27/14.
 */
class ParseAndRewriteTest {

    @ParameterizedTest(name = "CDA: {1} can be parsed to {2} and recreated")
    @CsvSource({
        "CDA, PaulWilson-Clinic.ccd.xml, com.github.rahulsom.cda.POCDMT000040ClinicalDocument"
    })
    void cdaCanBeParsedAndRecreated(String profile, String file, String clazzName) {
        var parsedObject = parseCda(profile + "/" + file);
        assertEquals(POCDMT000040ClinicalDocument.class, parsedObject.getClass());
        assertTrue(XmlTestHelper.getIrrecoverableDifferences(
            parsedObject, profile + "/" + file, XmlTestHelper.getCdaJaxbContext()
        ).isEmpty());
    }

    @ParameterizedTest(name = "{0}: {1} can be parsed to {2} and recreated")
    @CsvSource({
        "PDQV3, 01_PDQQuery1.xml, org.hl7.v3.PRPAIN201305UV02",
        "PDQV3, 01_PDQQuery1_20110328.xml, org.hl7.v3.PRPAIN201305UV02",
        "PDQV3, 02_PDQQuery1Response.xml, org.hl7.v3.PRPAIN201306UV02",
        "PDQV3, 02_PDQQuery1Response_20110328.xml, org.hl7.v3.PRPAIN201306UV02",
        "PDQV3, 02_PDQQuery1Response_20110504.xml, org.hl7.v3.PRPAIN201306UV02",
        "PDQV3, 03_PDQQuery1Continuation.xml, org.hl7.v3.QUQIIN000003UV01Type",
        "PDQV3, 03_PDQQuery1Continuation-1_20110328.xml, org.hl7.v3.QUQIIN000003UV01Type",
        "PDQV3, 04_PDQQuery1ContinuationResponse.xml, org.hl7.v3.PRPAIN201306UV02",
        "PDQV3, 04_PDQQuery1ContinuationResponse_20110328.xml, org.hl7.v3.PRPAIN201306UV02",
        "PDQV3, 04_PDQQuery1ContinuationResponse_20110504.xml, org.hl7.v3.PRPAIN201306UV02",
        "PDQV3, 05_PDQQuery1Cancel.xml, org.hl7.v3.QUQIIN000003UV01Type",
        "PDQV3, 05_PDQQuery1Cancel_20110328.xml, org.hl7.v3.QUQIIN000003UV01Type",
        "PDQV3, 06_PDQQuery1CancelAck.xml, org.hl7.v3.MCCIIN000002UV01",
        "PDQV3, 06_PDQQuery1CancelAck_20110328.xml, org.hl7.v3.MCCIIN000002UV01",
        
        "PIXV3, 01_PatientRegistryRecordAdded1.xml, org.hl7.v3.PRPAIN201301UV02",
        "PIXV3, 01_PatientRegistryRecordAdded1_20110228.xml, org.hl7.v3.PRPAIN201301UV02",
        "PIXV3, 01_PatientRegistryRecordAdded1_20110328.xml, org.hl7.v3.PRPAIN201301UV02",
        "PIXV3, 01_PatientRegistryRecordAdded1_20110504.xml, org.hl7.v3.PRPAIN201301UV02",
        "PIXV3, 02_PatientRegistryRecordAdded1Ack.xml, org.hl7.v3.MCCIIN000002UV01",
        "PIXV3, 02_PatientRegistryRecordAdded1Ack_20110228.xml, org.hl7.v3.MCCIIN000002UV01",
        "PIXV3, 02_PatientRegistryRecordAdded1Ack_20110328.xml, org.hl7.v3.MCCIIN000002UV01",
        "PIXV3, 03_PatientRegistryRecordAdded2.xml, org.hl7.v3.PRPAIN201301UV02",
        "PIXV3, 03_PatientRegistryRecordAdded2_20110228.xml, org.hl7.v3.PRPAIN201301UV02",
        "PIXV3, 03_PatientRegistryRecordAdded2_20110328.xml, org.hl7.v3.PRPAIN201301UV02",
        "PIXV3, 04_PatientRegistryRecordRevised2.xml, org.hl7.v3.PRPAIN201302UV02",
        "PIXV3, 04_PatientRegistryRecordRevised2_20110228.xml, org.hl7.v3.PRPAIN201302UV02",
        "PIXV3, 04_PatientRegistryRecordRevised2_20110328.xml, org.hl7.v3.PRPAIN201302UV02",
        "PIXV3, 05_PatientRegistryDuplicatesResolved.xml, org.hl7.v3.PRPAIN201304UV02",
        "PIXV3, 05_PatientRegistryDuplicatesResolved_20110328.xml, org.hl7.v3.PRPAIN201304UV02",
        "PIXV3, 05_PatientRegistryDuplicatesResolved_20110504.xml, org.hl7.v3.PRPAIN201304UV02",
        "PIXV3, 06_PIXQuery1.xml, org.hl7.v3.PRPAIN201309UV02",
        "PIXV3, 06_PIXQuery1_20110228.xml, org.hl7.v3.PRPAIN201309UV02",
        "PIXV3, 06_PIXQuery1_20110328.xml, org.hl7.v3.PRPAIN201309UV02",
        "PIXV3, 07_PIXQuery1Response.xml, org.hl7.v3.PRPAIN201310UV02",
        "PIXV3, 07_PIXQuery1Response_20110228.xml, org.hl7.v3.PRPAIN201310UV02",
        "PIXV3, 07_PIXQuery1Response_20110328.xml, org.hl7.v3.PRPAIN201310UV02",
        
        "SVS, RetrieveValueSetRequest.xml, ihe.iti.svs._2008.RetrieveValueSetRequestType",
        "SVS, RetrieveValueSetResponse.xml, ihe.iti.svs._2008.RetrieveValueSetResponseType",
        
        "XCPD, XCPDCrossGatewayPatientDiscoveryResponseAEError.xml, org.hl7.v3.PRPAIN201306UV02",
        "XCPD, XCPDCrossGatewayPatientDiscoveryResponseForMoreDemographics.xml, org.hl7.v3.PRPAIN201306UV02",
        
        "XDS.b, RegisterDocumentSet-bRequest.xml, oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest",
        "XDS.b, RegistryStoredQueryRequest.xml, oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest",
        "XDS.b, RegistryStoredQueryResponse.xml, oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse"
    })
    void itiCanBeParsedAndRecreated(String profile, String file, String clazzName) throws Exception {
        var parsedObject = parseIti(profile + "/" + file);
        var expectedClass = Class.forName(clazzName);
        assertEquals(expectedClass, parsedObject.getClass());
        assertTrue(XmlTestHelper.getIrrecoverableDifferences(
            parsedObject, profile + "/" + file
        ).isEmpty());
    }

    private Object parse(String file, JAXBContext jaxbContext) {
        try {
            var obj = jaxbContext.createUnmarshaller().unmarshal(
                this.getClass().getClassLoader().getResourceAsStream(file)
            );
            if (obj instanceof JAXBElement<?> jaxbElement) {
                return jaxbElement.getValue();
            } else {
                return obj;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse " + file, e);
        }
    }

    private Object parseCda(String file) {
        return parse(file, XmlTestHelper.getCdaJaxbContext());
    }

    private Object parseIti(String file) {
        return parse(file, XmlTestHelper.getItiJaxbContext());
    }
}