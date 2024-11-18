package org.dhis2.usescases.biometrics.ui

import org.dhis2.data.biometrics.SimprintsItem
import org.junit.Assert
import org.junit.Test

class SequentialSearchTest{

    @Test
    fun `Should return age in months to 6`() {
        val biometricsSearch = SequentialSearch.BiometricsSearch(
            previousSearch = null,
            nextActions = emptyList(),
            listOf(SimprintsItem(guid = "guid1", confidence = 80f)),
            sessionId = "1111-1111",
            isAgeNotSupported = false
        )

        Assert.assertEquals("1111-1111", biometricsSearch.sequentialSessionId)
    }

}