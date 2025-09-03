package ihe.iti;

import ihe.util.XmlTestHelper;
import org.hl7.v3.EnFamily;
import org.hl7.v3.EnGiven;
import org.hl7.v3.PN;
import org.hl7.v3.PRPAIN201306UV02;
import org.junit.jupiter.api.Test;

import jakarta.xml.bind.JAXBElement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests PRPAIN201306UV02 marshaling and unmarshaling and also support for mixed content
 * @author rahulsomasunderam
 */
class PdqTest {
    @Test
    void nameCanBeExtractedFromPdqResponse() throws Exception {
        var jaxbContext = XmlTestHelper.getItiJaxbContext();

        var r = jaxbContext.createUnmarshaller().unmarshal(
            PdqTest.class.getClassLoader().getResourceAsStream("PDQV3/02_PDQQuery1Response.xml")
        );

        assertInstanceOf(PRPAIN201306UV02.class, r);
        var r1 = (PRPAIN201306UV02) r;

        var id = XmlTestHelper.getIrrecoverableDifferences(r, "PDQV3/02_PDQQuery1Response.xml");
        assertTrue(id.isEmpty(), "There should be no differences");

        var patient = r1.getControlActProcess().getSubject().stream()
            .findFirst()
            .orElseThrow()
            .getRegistrationEvent()
            .getSubject1()
            .getPatient();

        assertEquals("Good Health Clinic", 
            patient.getProviderOrganization()
                .getValue()
                .getName().stream()
                .findFirst()
                .orElseThrow()
                .getContent().stream()
                .findFirst()
                .orElseThrow());

        PN pn = patient.getPatientPerson().getValue().getName().get(0);
        List<JAXBElement<?>> nameParts = pn.getContent().stream()
            .filter(content -> content instanceof JAXBElement)
            .map(content -> (JAXBElement<?>) content)
            .collect(java.util.stream.Collectors.toList());

        var givenName = nameParts.stream()
            .filter(element -> element.getDeclaredType() == EnGiven.class)
            .findFirst()
            .orElseThrow();
        assertEquals("James", 
            ((EnGiven) givenName.getValue()).getContent().stream()
                .findFirst()
                .orElseThrow());

        var familyName = nameParts.stream()
            .filter(element -> element.getDeclaredType() == EnFamily.class)
            .findFirst()
            .orElseThrow();
        assertEquals("Jones", 
            ((EnFamily) familyName.getValue()).getContent().stream()
                .findFirst()
                .orElseThrow());
    }
}