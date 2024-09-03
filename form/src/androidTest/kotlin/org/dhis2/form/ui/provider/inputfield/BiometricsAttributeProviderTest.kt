package org.dhis2.form.ui.provider.inputfield

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.dhis2.form.model.biometrics.BiometricsAttributeUiModelImpl
import org.junit.Rule
import org.junit.Test

class BiometricsAttributeProviderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `shouldRenderAsInitialIfValueIsNull`() {

        val model = givenABiometricsUiModel(null)

        composeTestRule.setContent {
            ProvideBiometricsAttribute(
                fieldUiModel = model
            )

        }
        composeTestRule.onNodeWithText("GET BIOMETRICS").assertIsDisplayed()
        composeTestRule.onNodeWithText("RETAKE").assertDoesNotExist()
    }

    @Test
    fun `shouldRenderAsFailedIfValueIsFailedGuid`() {

        val model = givenABiometricsUiModel("$$$\$BIOMETRICS_FAILED$$$$")

        composeTestRule.setContent {
            ProvideBiometricsAttribute(
                fieldUiModel = model
            )

        }
        composeTestRule.onNodeWithText("BIOMETRICS DECLINED").assertIsDisplayed()
        composeTestRule.onNodeWithText("RETAKE").assertIsDisplayed()
    }

    @Test
    fun `shouldRenderAsSuccessIfValueIsValid`() {

        val model = givenABiometricsUiModel("de3aab75-0817-47da-9335-926005ac4bac")

        composeTestRule.setContent {
            ProvideBiometricsAttribute(
                fieldUiModel = model
            )

        }
        composeTestRule.onNodeWithText("BIOMETRICS COMPLETED").assertIsDisplayed()
        composeTestRule.onNodeWithText("RETAKE").assertIsDisplayed()
    }

    private fun givenABiometricsUiModel(value: String?): BiometricsAttributeUiModelImpl {
        return BiometricsAttributeUiModelImpl(
            uid = "FieldUIModelUid",
            layoutId = 1,
            value = value,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }
}