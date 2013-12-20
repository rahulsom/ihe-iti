package ihe.iti

import ihe.util.XmlTestHelper
import org.hl7.v3.EnFamily
import org.hl7.v3.EnGiven
import org.hl7.v3.PN
import org.hl7.v3.PRPAIN201305UV02
import org.hl7.v3.PRPAIN201306UV02
import spock.lang.Specification

import javax.xml.bind.JAXBElement


/**
 * Tests PRPAIN201306UV02 marshaling and unmarshaling and also support for mixed content
 * @author rahulsomasunderam
 */
class PdqSpecTest extends Specification {
  def "test if name can be extracted from a PDQ Response"() {
    given: "a parser"
    def jaxbContext = XmlTestHelper.jaxbContext

    when: "the message is parsed"
    def r = jaxbContext.createUnmarshaller().unmarshal(
        PdqSpecTest.classLoader.getResourceAsStream('PDQV3/02_PDQQuery1Response.xml')
    )

    then: "a valid object should be returned"
    r instanceof PRPAIN201306UV02
    def r1 = r as PRPAIN201306UV02

    when: "The object's xml is compared against the original"
    def id = XmlTestHelper.getIrrecoverableDifferences(r, 'PDQV3/02_PDQQuery1Response.xml')

    then: "There should be no differences"
    !id

    when: "The patient is found"
    def patient = r1.controlActProcess.subject.find { it }.registrationEvent.subject1.patient

    then: "It's provider should be 'Good Health Clinic'"
    patient.providerOrganization.value.name.find { it }.content.find { it } == 'Good Health Clinic'

    when: "The patient's name is found"
    PN pn = patient.patientPerson.value.name[0]
    List<JAXBElement> nameParts = pn.content.findAll { it instanceof JAXBElement }

    then: "It's first name name is 'James'"
    nameParts.find { it.declaredType == EnGiven }.value.content.find { it } == 'James'

    and: "Last name should be jones"
    nameParts.find { it.declaredType == EnFamily }.value.content.find { it } == 'Jones'
  }

  def "test query can be parsed and recreated"() {
    given: "a parser"
    def jaxbContext = XmlTestHelper.jaxbContext

    when: "the message is parsed"
    def r = jaxbContext.createUnmarshaller().unmarshal(
        PdqSpecTest.classLoader.getResourceAsStream('PDQV3/01_PDQQuery1.xml')
    )

    then: "a valid object should be returned"
    r instanceof PRPAIN201305UV02

    when: "The object's xml is compared against the original"
    def id = XmlTestHelper.getIrrecoverableDifferences(r, 'PDQV3/01_PDQQuery1.xml')

    then: "There should be no differences"
    !id
  }
}
