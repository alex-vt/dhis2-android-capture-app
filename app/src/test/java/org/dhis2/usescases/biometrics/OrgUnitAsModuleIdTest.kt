package org.dhis2.usescases.biometrics

import org.junit.Assert
import org.junit.Test

class OrgUnitAsModuleIdTest {

    private val orgUnit1 = "orgUnit1"
    private val orgUnit2 = "orgUnit2"
    private val orgUnit3 = "orgUnit3"

    private val path = "$orgUnit1/$orgUnit2/$orgUnit3"

    @Test
    fun `Should return the same org unit if orgUnitLevelAsModuleId is 0`() {
        val orgUnit = getOrgUnitAsModuleIdByPath(orgUnit3, path, 0)

        Assert.assertEquals(orgUnit3, orgUnit)
    }

    @Test
    fun `Should return the parent org unit if orgUnitLevelAsModuleId is -1`() {
        val orgUnit = getOrgUnitAsModuleIdByPath(orgUnit3, path, -1)

        Assert.assertEquals(orgUnit2, orgUnit)
    }

    @Test
    fun `Should return the parent of the parent org unit if orgUnitLevelAsModuleId is -2`() {
        val orgUnit = getOrgUnitAsModuleIdByPath(orgUnit3, path, -2)

        Assert.assertEquals(orgUnit1, orgUnit)
    }

    @Test
    fun `Should return last parent if orgUnitLevelAsModuleId is greater than parent counts`() {
        val orgUnit = getOrgUnitAsModuleIdByPath(orgUnit3, path, -3)

        Assert.assertEquals(orgUnit1, orgUnit)
    }

    @Test
    fun `Should return selected org unit if doesn't exist in path`() {
        val orgUnit = getOrgUnitAsModuleIdByPath("orgUnit4", path, -3)

        Assert.assertEquals("orgUnit4", orgUnit)
    }
}