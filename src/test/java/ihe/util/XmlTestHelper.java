package ihe.util;

import com.github.rahulsom.cda.POCDMT000040ClinicalDocument;
import ihe.iti.svs._2008.RetrieveValueSetRequestType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.hl7.v3.PRPAIN201306UV02;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helps with testing xml inputs
 * @author rahulsomasunderam
 */
public class XmlTestHelper {

    private static JAXBContext itiJaxbContext;
    private static JAXBContext cdaJaxbContext;
    
    public static JAXBContext getItiJaxbContext() {
        if (itiJaxbContext == null) {
            try {
                itiJaxbContext = JAXBContext.newInstance(
                    "org.hl7.v3:ihe.iti.svs._2008:ihe.iti.xds_b._2007:oasis.names.tc.ebxml_regrep.xsd.query._3:oasis.names.tc.ebxml_regrep.xsd.rs._3:oasis.names.tc.ebxml_regrep.xsd.lcm._3"
                );
            } catch (JAXBException e) {
                throw new RuntimeException("Failed to create ITI JAXBContext", e);
            }
        }
        return itiJaxbContext;
    }

    public static JAXBContext getCdaJaxbContext() {
        if (cdaJaxbContext == null) {
            try {
                cdaJaxbContext = JAXBContext.newInstance(POCDMT000040ClinicalDocument.class);
            } catch (JAXBException e) {
                throw new RuntimeException("Failed to create CDA JAXBContext", e);
            }
        }
        return cdaJaxbContext;
    }

    public static <T> List<Difference> getIrrecoverableDifferences(T r, String file) {
        return getIrrecoverableDifferences(r, file, getItiJaxbContext());
    }

    public static <T> List<Difference> getIrrecoverableDifferences(T r, String file, JAXBContext theContext) {
        try {
            var sw = new StringWriter();
            try {
                theContext.createMarshaller().marshal(r, sw);
            } catch (Exception e) {
                var j = new JAXBElement<>(new QName("foo", "bar"), (Class<T>) r.getClass(), r);
                sw = new StringWriter();
                theContext.createMarshaller().marshal(j, sw);
            }

            String inString = readResourceAsString(file);
            String outString = sw.toString();

            var diff = new Diff(inString, outString);
            var dd = new DetailedDiff(diff);
            
            @SuppressWarnings("unchecked")
            List<Difference> allDifferences = dd.getAllDifferences();
            
            var irrecoverableDifferences = allDifferences.stream()
                .filter(d -> !d.isRecoverable() 
                    && d.getControlNodeDetail().getXpathLocation() != null
                    && d.getTestNodeDetail().getXpathLocation() != null
                    && d.getControlNodeDetail().getXpathLocation().equals(d.getTestNodeDetail().getXpathLocation())
                    && !"number of child nodes".equals(d.getDescription()))
                .collect(Collectors.toList());

            irrecoverableDifferences.forEach(d -> {
                System.out.println(d);
                System.out.println();
            });

            return irrecoverableDifferences;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get irrecoverable differences", e);
        }
    }

    private static String readResourceAsString(String resourcePath) throws IOException {
        try (InputStream is = XmlTestHelper.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}