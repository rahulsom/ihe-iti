package ihe.iti

import co.freeside.betamax.Betamax
import co.freeside.betamax.Recorder
import com.sun.xml.internal.ws.developer.JAXWSProperties
import org.junit.Rule
import spock.lang.Specification

import ihe.iti.xds_b._2007.*
import javax.xml.ws.*
import javax.xml.ws.soap.*

/**
 * Created by rahulsomasunderam on 2/28/14.
 */
class RepoSpec extends Specification {

  @Rule
  Recorder recorder = new Recorder()

  void setup() {
    System.setProperty( "com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
  }

  void cleanup() {
    System.getProperties().remove("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize");
  }

  @Betamax(tape = 'repo-retrieve')
  def 'accesses external web service'() {
    given: "A Webservice"
    def wsdl = this.class.getResource('/iti/wsdl/XDS.b_DocumentRepository.wsdl')
    Service service = new DocumentRepositoryService(wsdl)
    def port = service.getDocumentRepositoryPortSoap12(new MTOMFeature(true))
    BindingProvider bp = port

    bp.requestContext[BindingProvider.ENDPOINT_ADDRESS_PROPERTY] = 'http://hd-josette/hd/services/xdsrepositoryb'

    and: "a request"
    def request = new RetrieveDocumentSetRequestType().
        withDocumentRequest(
            new RetrieveDocumentSetRequestType.DocumentRequest().
                withDocumentUniqueId('1.42.20130708171302.244').
                withRepositoryUniqueId('2.16.840.1.113883.3.1317.8.1000')
        )

    println "Request created"

    when: "You send a request"
    def resp = port.documentRepositoryRetrieveDocumentSet(request)

    then: "It ends in success"
    resp.registryResponse.status.endsWith('Success')
    !resp.registryResponse.registryErrorList
    resp.documentResponse.size() == 1
    resp.documentResponse[0].documentUniqueId == '1.42.20130708171302.244'
    resp.documentResponse[0].mimeType == 'text/plain'
    resp.documentResponse[0].document.inputStream.text == this.class.classLoader.getResourceAsStream('XDS.b/four-score.txt').text
  }
  
}
