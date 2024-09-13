package org.dhis2.form.ui.provider.inputfield

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import org.dhis2.commons.resources.ColorUtils
import org.dhis2.commons.resources.ResourceManager
import org.dhis2.form.model.biometrics.BiometricsDataElementStatus
import org.dhis2.form.model.biometrics.BiometricsDataElementUiModelImpl
import org.junit.Rule
import org.junit.Test

class BiometricsDataElementProviderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `shouldRenderRegistrationAsInitialIfValueIsNull`() {

        val model = givenABiometricsUiModel(null, BiometricsDataElementStatus.NOT_DONE)

        composeTestRule.setContent {
            ProvideBiometricsDataElement(
                fieldUiModel = model,
                resources = ResourceManager(LocalContext.current, ColorUtils())
            )

        }
        composeTestRule.onNodeWithText("GET BIOMETRICS").assertIsDisplayed()
        composeTestRule.onNodeWithText("RETAKE").assertDoesNotExist()
    }

    @Test
    fun `shouldRenderAsFailedIfValueIsFailedGuid`() {

        val model = givenABiometricsUiModel(
            "de3aab75-0817-47da-9335-926005ac4bac",
            BiometricsDataElementStatus.FAILURE
        )

        composeTestRule.setContent {
            ProvideBiometricsDataElement(
                fieldUiModel = model,
                resources = ResourceManager(LocalContext.current, ColorUtils())
            )

        }
        composeTestRule.onNodeWithContentDescription("Failed").assertIsDisplayed()
        composeTestRule.onNodeWithText("RETAKE").assertIsDisplayed()
    }

    @Test
    fun `shouldRenderAsSuccessIfValueIsValid`() {

        val model = givenABiometricsUiModel(
            "de3aab75-0817-47da-9335-926005ac4bac",
            BiometricsDataElementStatus.SUCCESS
        )

        composeTestRule.setContent {
            ProvideBiometricsDataElement(
                fieldUiModel = model,
                resources = ResourceManager(LocalContext.current, ColorUtils())
            )

        }

        composeTestRule.onNodeWithContentDescription("Success").assertIsDisplayed()
        composeTestRule.onNodeWithText("RETAKE").assertDoesNotExist()
    }

    private fun givenABiometricsUiModel(
        value: String?,
        status: BiometricsDataElementStatus
    ): BiometricsDataElementUiModelImpl {
        return BiometricsDataElementUiModelImpl(
            uid = "FieldUIModelUid",
            layoutId = 1,
            value = value,
            null,
            status,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }
}