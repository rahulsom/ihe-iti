package ihe.iti

import ihe.util.XmlTestHelper
import org.hl7.v3.*
import org.junit.Test

import javax.xml.bind.JAXBElement

/**
 * Tests PRPAIN201306UV02 marshaling and unmarshaling and also support for mixed content
 *
 * @author Rahul Somasunderam
 */
class PdqTest {
  @Test
  void testQuery1Response() {
    XmlTestHelper.withMatching(PRPAIN201306UV02, 'PDQV3/02_PDQQuery1Response.xml') { PRPAIN201306UV02 r ->
      def patient = r.controlActProcess.subject.find { it }.registrationEvent.subject1.patient
      assert patient.providerOrganization.value.name.find { it }.content.find { it } == 'Good Health Clinic'
      PN pn = patient.patientPerson.value.name[0]
      List<JAXBElement> nameParts = pn.content.findAll { it instanceof JAXBElement }
      assert nameParts.find { it.declaredType == EnGiven }.value.content.find { it } == 'James'
      assert nameParts.find { it.declaredType == EnFamily }.value.content.find { it } == 'Jones'
    }
  }

  @Test
  void testQuery1() {
    XmlTestHelper.withMatching(PRPAIN201305UV02, 'PDQV3/01_PDQQuery1.xml')
  }

}
