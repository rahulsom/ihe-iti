package ihe.iti;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.Callback;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ihe.iti.xds_b._2007.*;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.soap.MTOMFeature;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by rahulsomasunderam on 2/28/14.
 */
class RepoTest {

    @BeforeEach
    void setup() {
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
    }

    @AfterEach
    void cleanup() {
        System.getProperties().remove("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize");
    }

    @Test
    void accessesExternalWebService() throws Exception {
        Server server = new Server(0); // Use any available port
        server.setHandler(new Handler.Abstract() {
            @Override
            public boolean handle(Request request, Response response, Callback callback) throws Exception {
                if ("/xdsrepositoryb".equals(request.getHttpURI().getPath())) {
                    var responseText = loadResource("betamax/response.txt");
                    var bytes = responseText.getBytes(StandardCharsets.UTF_8);
                    response.getHeaders().put("Content-Type", "multipart/related; type=\"application/xop+xml\"; " +
                        "boundary=\"uuid:69897320-fe2e-4b04-94e2-1254ce1813c0\"; " +
                        "start=\"<root.message@cxf.apache.org>\"; " +
                        "start-info=\"application/soap+xml\";charset=UTF-8");
                    response.setStatus(200);
                    response.getHeaders().put("Content-Length", String.valueOf(bytes.length));

                    response.write(true, java.nio.ByteBuffer.wrap(bytes), callback);
                    return true;
                }
                return false;
            }
        });
        server.start();
        
        int actualPort = ((org.eclipse.jetty.server.ServerConnector) server.getConnectors()[0]).getLocalPort();

        try {
            var wsdl = new File("src/main/resources/iti/wsdl/XDS.b_DocumentRepository.wsdl");
            var service = new DocumentRepositoryService(wsdl.toURI().toURL());
            var port = service.getDocumentRepositoryPortSoap12(new MTOMFeature(true));
            BindingProvider bp = (BindingProvider) port;

            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
                "http://localhost:" + actualPort + "/xdsrepositoryb");

            var request = new RetrieveDocumentSetRequestType()
                .withDocumentRequest(
                    new RetrieveDocumentSetRequestType.DocumentRequest()
                        .withDocumentUniqueId("1.42.20130708171302.244")
                        .withRepositoryUniqueId("2.16.840.1.113883.3.1317.8.1000")
                );

            var resp = port.documentRepositoryRetrieveDocumentSet(request);

            assertTrue(resp.getRegistryResponse().getStatus().endsWith("Success"));
            assertNull(resp.getRegistryResponse().getRegistryErrorList());
            assertEquals(1, resp.getDocumentResponse().size());
            assertEquals("1.42.20130708171302.244", resp.getDocumentResponse().get(0).getDocumentUniqueId());
            assertEquals("text/plain", resp.getDocumentResponse().get(0).getMimeType());
            assertEquals(loadResource("XDS.b/four-score.txt"), 
                new String(resp.getDocumentResponse().get(0).getDocument().getInputStream().readAllBytes(), 
                    StandardCharsets.UTF_8));

        } finally {
            server.stop();
        }
    }

    private String loadResource(String path) throws IOException {
        try (var is = this.getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IOException("Resource not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}