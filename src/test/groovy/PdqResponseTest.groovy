import junit.framework.TestCase
import org.hl7.v3.EnFamily
import org.hl7.v3.EnGiven
import org.hl7.v3.PRPAIN201306UV02

import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement

/**
 * Tests PRPAIN201306UV02 marshaling and unmarshaling and also support for
 * mixed content
 * @author Rahul Somasunderam
 */
class PdqResponseTest extends TestCase {
  void testOne() {
    JAXBContext c = JAXBContext.newInstance(PRPAIN201306UV02)
    PRPAIN201306UV02 r = c.createUnmarshaller().unmarshal(
        this.class.classLoader.getResourceAsStream('PDQV3/02_PDQQuery1Response.xml')
    )
    def patient = r.controlActProcess.subject.find{it}.registrationEvent.subject1.patient
    assert patient.providerOrganization.value.name.find{it}.content.find{it} == 'Good Health Clinic'

    def nameParts = patient.patientPerson.value.name[0].content.findAll { it instanceof JAXBElement }
    assert nameParts.find{it.value instanceof EnGiven}.value.content.find{it} == 'James'
    assert nameParts.find{it.value instanceof EnFamily}.value.content.find{it} == 'Jones'

  }
}
