package ihe.iti

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import spock.lang.Specification

import ihe.iti.xds_b._2007.*
import javax.xml.ws.*
import javax.xml.ws.soap.*
import java.nio.charset.Charset

/**
 * Created by rahulsomasunderam on 2/28/14.
 */
class RepoSpec extends Specification {

  void setup() {
    System.setProperty( "com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
  }

  void cleanup() {
    System.getProperties().remove("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize");
  }

  def 'accesses external web service'() {
    given: "A Webservice"
    Server server = new Server(8080)
    server.setHandler(new AbstractHandler() {
      @Override
      void handle(String target, Request baseRequest, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        if (target == "/xdsrepositoryb") {
          def bytes = this.class.getResource('/betamax/response.txt').text.getBytes(Charset.forName('UTF-8'))
          response.setContentType("multipart/related; type=\"application/xop+xml\"; boundary=\"uuid:69897320-fe2e-4b04-94e2-1254ce1813c0\"; start=\"<root.message@cxf.apache.org>\"; start-info=\"application/soap+xml\";charset=UTF-8")
          response.setStatus(HttpServletResponse.SC_OK)
          response.setContentLength(bytes.length)

          response.getOutputStream().write(bytes)
          response.getOutputStream().close()
          baseRequest.setHandled(true)
        }
      }
    })
    server.start()

    def wsdl = this.class.getResource('/iti/wsdl/XDS.b_DocumentRepository.wsdl')
    Service service = new DocumentRepositoryService(wsdl)
    def port = service.getDocumentRepositoryPortSoap12(new MTOMFeature(true))
    BindingProvider bp = port

    bp.requestContext[BindingProvider.ENDPOINT_ADDRESS_PROPERTY] = 'http://localhost:8080/xdsrepositoryb'

    and: "a request"
    def request = new RetrieveDocumentSetRequestType().
        withDocumentRequest(
            new RetrieveDocumentSetRequestType.DocumentRequest().
                withDocumentUniqueId('1.42.20130708171302.244').
                withRepositoryUniqueId('2.16.840.1.113883.3.1317.8.1000')
        )

    when: "You send a request"
    def resp = port.documentRepositoryRetrieveDocumentSet(request)

    then: "It ends in success"
    resp.registryResponse.status.endsWith('Success')
    !resp.registryResponse.registryErrorList
    resp.documentResponse.size() == 1
    resp.documentResponse[0].documentUniqueId == '1.42.20130708171302.244'
    resp.documentResponse[0].mimeType == 'text/plain'
    resp.documentResponse[0].document.inputStream.text == this.class.classLoader.getResourceAsStream('XDS.b/four-score.txt').text

    cleanup:
    server.stop()
  }

}
