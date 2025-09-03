package ihe.util

import com.github.rahulsom.cda.POCDMT000040ClinicalDocument
import ihe.iti.svs._2008.RetrieveValueSetRequestType
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType
import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.Difference
import org.hl7.v3.PRPAIN201306UV02

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.JAXBElement
import javax.xml.namespace.QName

/**
 * Helps with testing xml inputs
 * @author rahulsomasunderam
 */
class XmlTestHelper {

  @Lazy
  static itiJaxbContext = JAXBContext.newInstance(
      PRPAIN201306UV02,RetrieveValueSetRequestType,
      ProvideAndRegisterDocumentSetRequestType, RetrieveDocumentSetRequestType,
      AdhocQueryResponse, RegistryResponseType, RetrieveDocumentSetResponseType
  )

  @Lazy
  static cdaJaxbContext = JAXBContext.newInstance(POCDMT000040ClinicalDocument)

  static <T> List getIrrecoverableDifferences(T r, String file, JAXBContext theContext = itiJaxbContext) {
    def sw = new StringWriter()
    try {
      theContext.createMarshaller().marshal(r, sw)
    } catch (Exception e) {
      def j = new JAXBElement(new QName('foo','bar'), r.class, r)
      sw = new StringWriter()
      theContext.createMarshaller().marshal(j, sw)
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
