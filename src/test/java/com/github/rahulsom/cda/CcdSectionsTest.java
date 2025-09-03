package com.github.rahulsom.cda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by rahul on 7/11/16.
 */
class CcdSectionsTest {
    @Test
    void checkProblemSectionIsCorrectlyBuilt() {
        var pr = CcdSections.Problem;
        assertEquals(CcdSections.LOINC, pr.getCodeSystem());
    }
}