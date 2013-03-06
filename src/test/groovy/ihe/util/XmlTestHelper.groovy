package ihe.util

import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.Difference

import javax.xml.bind.JAXBContext

/**
 * Helps with testing xml inputs
 * @author rahulsomasunderam
 */
class XmlTestHelper {

  /**
   * Validates parse and rewrite of an xml, with option to run additional tests in closure
   *
   * @param clazz The Class that would be the returned on parsing the file
   * @param file The file (resource) name inside the test classloader
   * @param closure Code to execute after preliminary tests are completed
   */
  static <T> void withMatching(Class<T> clazz, String file, Closure closure = {}) {
    JAXBContext jaxbContext = JAXBContext.newInstance(clazz)
    def r = jaxbContext.createUnmarshaller().unmarshal(XmlTestHelper.classLoader.getResourceAsStream(file)) as T
    def sw = new StringWriter()
    jaxbContext.createMarshaller().marshal(r, sw)

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

    assert !irrecoverableDifferences

    closure?.call(r)
  }
}
