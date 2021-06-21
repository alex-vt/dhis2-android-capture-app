package org.dhis2.form.data

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.dhis2.form.model.ActionType
import org.dhis2.form.model.RowAction
import org.hisp.dhis.android.core.common.FeatureType
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GeometryControllerTest {

    private val geometryParser: GeometryParser = mock()
    private lateinit var controller: GeometryController

    @Before
    fun setUp() {
        controller = GeometryController(geometryParser)
    }

    @Test
    fun `Should return null if coordinates is null`() {
        assertTrue(
            controller.generateLocationFromCoordinates(FeatureType.POINT, null) == null
        )
    }

    @Test
    fun `Should parse coordinates`() {
        val point = listOf(12.0, 12.0)
        whenever(
            geometryParser.parsePoint(any())
        ) doReturn point

        val result = controller.generateLocationFromCoordinates(
            FeatureType.POINT, "coordinates"
        )

        assertTrue(
            result?.type() == FeatureType.POINT &&
                result.coordinates()?.isNotEmpty() == true
        )
    }

    @Test
    fun `Should return coordinates callback`() {
        var currentCallback: Int = -1
        val coordinateCallback = controller.getCoordinatesCallback(
            {
                currentCallback = 0
            },
            { currentCallback = 1 },
            { _, _, _ -> currentCallback = 2 }
        )

        coordinateCallback.onItemAction(RowAction(id = "fieldUid", type = ActionType.ON_SAVE))
        assertTrue(currentCallback == 0)
        coordinateCallback.currentLocation("fieldUid")
        assertTrue(currentCallback == 1)
        coordinateCallback.mapRequest("fieldUid", FeatureType.POINT.name, null)
        assertTrue(currentCallback == 2)
    }
}
