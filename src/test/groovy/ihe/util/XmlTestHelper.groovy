package ihe.util

import ihe.iti.svs._2008.RetrieveValueSetRequestType
import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.Difference
import org.hl7.v3.PRPAIN201306UV02

import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement
import javax.xml.namespace.QName

/**
 * Helps with testing xml inputs
 * @author rahulsomasunderam
 */
class XmlTestHelper {

  @Lazy
  static jaxbContext = JAXBContext.newInstance(PRPAIN201306UV02,RetrieveValueSetRequestType)

  static <T> List getIrrecoverableDifferences(r, file) {
    def sw = new StringWriter()
    try {
      jaxbContext.createMarshaller().marshal(r, sw)
    } catch (Exception e) {
      def j = new JAXBElement(new QName('foo','bar'), r.class, r)
      sw = new StringWriter()
      jaxbContext.createMarshaller().marshal(j, sw)
    }

    def inString = XmlTestHelper.classLoader.getResourceAsStream(file).text
    def outString = sw.toString()

    def diff = new Diff(inString, outString)
    def dd = new DetailedDiff(diff)
    def irrecoverableDifferences = dd.allDifferences.findAll { Difference d ->
      !d.recoverable && d.controlNodeDetail.xpathLocation == d.testNodeDetail.xpathLocation &&
          d.description != 'number of child nodes'
    }

    irrecoverableDifferences.each { Difference d ->
      println d
      println ''
    }

    irrecoverableDifferences
  }
}
