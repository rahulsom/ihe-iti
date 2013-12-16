package com.github.rahulsom;

import org.hl7.v3.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Helper for creating commonly used classes
 *
 * @author rahulsomasunderam
 */
public class ItiHelper {
  public static final String DATE_FORMAT = "yyyyMMdd";
  public static final String DATE_TIME_FORMAT = "yyyyMMddHHmmss";

  public static <T> JAXBElement<T> jaxb(String tagname, Class<T> clazz, T value) {
    return new JAXBElement<T>(new QName("urn:hl7-org:v3", tagname), clazz, value);
  }

  public static TS ts(String value) {
    return new TS().withValue(value);
  }

  public static TS ts(Date value, String format) {
    return ts(new SimpleDateFormat(format).format(value));
  }

  public static TS ts(Date value) {
    return ts(value, DATE_TIME_FORMAT);
  }

  public static ST st(String value) {
    return new ST().withContent(value);
  }

  public static INT intOf(BigInteger value) {
    return new INT().withValue(value);
  }

  public static INT intOf(int value) {
    return new INT().withValue(BigInteger.valueOf(value));
  }

  public static II ii(String root, String extension, String namespace) {
    return new II().withRoot(root).withExtension(extension).withAssigningAuthorityName(namespace);
  }

  public static II ii(String root, String extension) {
    return ii(root, extension, null);
  }

  public static II ii(String root) {
    return ii(root, null, null);
  }

  public static CS cs(String code, String codeSystem) {
    return new CS().withCode(code).withCodeSystem(codeSystem);
  }

  public static CS cs(String code) {
    return cs(code, null);
  }

  public static CD cd(String code, String codeSystem) {
    return new CD().withCode(code).withCodeSystem(codeSystem);
  }

  public static CD cd(String code) {
    return cd(code, null);
  }

  public static CE ce(String code) {
    return new CE().withCode(code);
  }

  public static PN pn(Collection<JAXBElement<? extends ENXP>> data) {
    List arg = new ArrayList();
    arg.addAll(data);
    return new PN().withContent(arg);
  }

  public static EN en(Collection<JAXBElement<? extends ENXP>> data) {
    List arg = new ArrayList();
    arg.addAll(data);
    return new EN().withContent(arg);
  }

  public static AD ad(Collection<JAXBElement<? extends ADXP>> data) {
    List arg = new ArrayList();
    arg.addAll(data);
    return new AD().withContent(arg);
  }

  public static PN pn(JAXBElement<? extends ENXP> ... data) {
    return new PN().withContent(data);
  }

  public static EN en(JAXBElement<? extends ENXP> ... data) {
    return new EN().withContent(data);
  }

  public static AD ad(JAXBElement<? extends ADXP> ... data) {
    return new AD().withContent(data);
  }

  public static class En {
    public static JAXBElement<EnGiven> given(String value) {
      return jaxb("given", EnGiven.class, new EnGiven().withContent(value));
    }

    public static JAXBElement<EnFamily> family(String value) {
      return jaxb("family", EnFamily.class, new EnFamily().withContent(value));
    }
  }

  public static class Ad {
    public static JAXBElement<AdxpStreetAddressLine> streetAddressLine(String value) {
      return jaxb("streetAddressLine", AdxpStreetAddressLine.class, new AdxpStreetAddressLine().withContent(value));
    }

    public static JAXBElement<AdxpCity> city(String value) {
      return jaxb("city", AdxpCity.class, new AdxpCity().withContent(value));
    }

    public static JAXBElement<AdxpState> state(String value) {
      return jaxb("state", AdxpState.class, new AdxpState().withContent(value));
    }

    public static JAXBElement<AdxpCountry> country(String value) {
      return jaxb("country", AdxpCountry.class, new AdxpCountry().withContent(value));
    }

    public static JAXBElement<AdxpPostalCode> postalCode(String value) {
      return jaxb("postalCode", AdxpPostalCode.class, new AdxpPostalCode().withContent(value));
    }
  }
}
