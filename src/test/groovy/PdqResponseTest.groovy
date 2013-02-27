import junit.framework.TestCase

import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement

import org.hl7.v3.*

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
    ) as PRPAIN201306UV02

    def patient = r.controlActProcess.subject.find{it}.registrationEvent.subject1.patient
    assert patient.providerOrganization.value.name.find{it}.content.find{it} == 'Good Health Clinic'
    EN.metaClass.getPrefixs = { content.findAll {it instanceof JAXBElement}.findAll {it.declaredType == EnPrefix}}
    EN.metaClass.getGivens = { content.findAll {it instanceof JAXBElement}.findAll {it.declaredType == EnGiven}}
    PN pn = patient.patientPerson.value.name[0]
    List<JAXBElement> nameParts = pn.content.findAll { it instanceof JAXBElement }
    assert nameParts.find{it.declaredType == EnGiven}.value.content.find{it} == 'James'
    assert pn.givens[0].value.content[0] == 'James'
    assert nameParts.find{it.declaredType == EnFamily}.value.content.find{it} == 'Jones'

  }
}
