package presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (!useDarkTheme) {
        lightColors
    } else {
        darkColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = {
            ExtendedTheme(useDarkTheme = useDarkTheme, content = content)
        },
        typography = typography,
        shapes = shapes
    )
}

@Composable
fun ExtendedTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val extendedColors = ExtendedColors(
        successContainer = if (useDarkTheme) md_theme_dark_success else md_theme_light_success,
        onSuccessContainer = if (useDarkTheme) md_theme_dark_onSuccess else md_theme_light_onSuccess,
        lineChartColor1 = if (useDarkTheme) md_theme_dark_line_color_1 else md_theme_light_line_color_1,
        lineChartColor2 = if (useDarkTheme) md_theme_dark_line_color_2 else md_theme_light_line_color_2,
        lineChartGripColor = if (useDarkTheme) md_theme_dark_grid_color else md_theme_light_grid_color,
        lineChartAxisColor = if (useDarkTheme) md_theme_dark_axis_line_chart_color else md_theme_light_axis_line_chart_color
    )
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            content = content,
        )
    }
}

object ExtendedTheme {
    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}