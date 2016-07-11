package com.github.rahulsom.cda

import spock.lang.Specification

/**
 * Created by rahul on 7/11/16.
 */
class CcdSectionsSpec extends Specification {
    def "Check Problem section is correctly built"() {
        when: "I get problem"
        def pr = CcdSections.Problem

        then: "It should be valid"
        pr.codeSystem == CcdSections.LOINC

    }
}
