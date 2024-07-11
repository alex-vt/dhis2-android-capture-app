package org.dhis2.usescases.biometrics.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import org.dhis2.form.R

class SearchHelperFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SearchHelperContent()
            }
        }
    }
}

@Composable
fun SearchHelperContent() {
    Surface(color = Color.White) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SearchWithBiometricsButton(onClick = { })
                Spacer(modifier = Modifier.height(16.dp))
                SearchWithAttributesButton(onClick = { })
                Spacer(modifier = Modifier.height(64.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("New patient?")
                    TextButton(onClick = { /* Handle new patient click */ }) {
                        Text(
                            "Register",
                            color = Color(0xFF0281cb),
                            style = TextStyle(
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun SearchWithBiometricsButton(
    onClick: () -> Unit = { },
) {
    val colorStops = arrayOf(
        0.0f to Color(0xFF009cb6),
        0.4f to Color(0xFF00b4d1),
        0.8f to Color(0xFF009cb6)
    )

    val modifier = Modifier
        .wrapContentWidth()
        .height(65.dp)
        .padding(horizontal = 32.dp, vertical = 8.dp)

    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        contentPadding = PaddingValues(),
        onClick = { onClick() },
    ) {
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(colorStops = colorStops))
                .then(modifier),
            contentAlignment = Alignment.Center,

            ) {
            Row() {
                Icon(
                    painter = painterResource(R.drawable.ic_bio_fingerprint),
                    contentDescription = stringResource(R.string.biometrics_get),
                    tint = Color.Unspecified,
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSize))
                Text(text = "Search with biometrics", color = Color.White)
            }
        }
    }
}

@Composable
fun SearchWithAttributesButton(
    onClick: () -> Unit = { },
) {
    OutlinedButton(modifier = Modifier.width(270.dp).height(50.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFF0281cb)
        ),
        onClick = { /* Handle second button click */ }) {
        Text("Search with attributes", color = Color(0xFF0281cb))
    }
}


@Preview
@Composable
fun PreviewSearchHelperContent() {
    SearchHelperContent()
}