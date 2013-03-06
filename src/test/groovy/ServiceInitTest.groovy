import junit.framework.TestCase
import ihe.iti.pdqv3._2007.PDQSupplierService

/**
 * TODO Documentation.
 * @author rahulsomasunderam
 * @since 3/6/13 1:40 PM
 */
class ServiceInitTest extends TestCase {
  void testOne() {
    def p = new PDQSupplierService()
    assert p
    assert p.PDQSupplierPortSoap12
  }
}
