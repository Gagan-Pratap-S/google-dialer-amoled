package app.morphe.patches.dialer

import app.morphe.patcher.patch.listDecodableFiles
import app.morphe.patcher.patch.resourcePatch
import app.morphe.patcher.patch.resourceXml
import app.morphe.patcher.xml.ResourceXml
import app.morphe.patches.dialer.DialerConstants.COMPATIBILITY_GOOGLE_DIALER

/**
 * AMOLED Dark Theme & Silhouette Avatar Fix for Google Phone (Google Dialer).
 */
@Suppress("unused")
val googleDialerThemePatch = resourcePatch(
    name = "AMOLED Dark Theme",
    description = "Converts Google Phone dark theme from grey into pure pitch-black (#000000), " +
                  "enhances dialpad readability with pure white text, and fixes unsaved contact avatar outlines.",
    default = true
) {
    compatibleWith(COMPATIBILITY_GOOGLE_DIALER)

    execute {
        println("Applying Google Dialer AMOLED modifications...")

        val nightColorReplacements = mapOf(
            "settingslib_materialColorSurfaceContainer" to "#000000",
            "settingslib_materialColorSurfaceContainerHigh" to "#0a0a0a",
            "settingslib_materialColorSurfaceContainerHighest" to "#141414",
            "settingslib_materialColorSurfaceVariant" to "#1f1f1f",
            "bam_center_fragment_background" to "#000000",
            "survey_surface_color" to "#000000",
            "survey_surface_color_elevation_2" to "#050505",
            "survey_surface_color_elevation_3" to "#0a0a0a",
            "survey_alert_dialog_bg_color" to "#000000",
            "google_marterial_dialog_background_color" to "#000000",
            "settingslib_dialog_background" to "#000000",
            "dialpad_icon_text_enabled" to "#ffffffff",
            "dialpad_icon_text_disabled" to "#80ffffff",
            "empty_content_view_message" to "#ffffffff",
            "settingslib_materialColorOnSurface" to "#ffffffff",
            "settingslib_materialColorOnSurfaceVariant" to "#ffffffff"
        )

        val outlineReplacements = mapOf(
            "settingslib_materialColorOutline" to "@android:color/system_outline_dark",
            "gm3_sys_color_dynamic_dark_outline" to "@android:color/system_outline_dark",
            "m3_sys_color_dynamic_dark_outline" to "@android:color/system_outline_dark",
            "sud_system_outline_dark" to "@android:color/system_outline_dark",
            "mc3_outline" to "@android:color/system_outline_dark"
        )

        // 1. Transform all colors in res/values-night*/colors.xml
        val nightFiles = listDecodableFiles("res/values-night*/colors.xml")
        println("Found night color files: $nightFiles")
        nightFiles.forEach { path ->
            resourceXml(path).use { xml ->
                xml.transformColors { name, _ ->
                    nightColorReplacements[name]
                }
            }
        }

        // 2. Transform silhouette outline colors in v34 files
        val v34ColorFiles = listDecodableFiles("res/values*34/colors.xml")
        println("Found v34 color files: $v34ColorFiles")
        v34ColorFiles.forEach { path ->
            resourceXml(path).use { xml ->
                xml.transformColors { name, _ ->
                    outlineReplacements[name]
                }
            }
        }

        // 3. Transform silhouette outline styles in v34 styles
        val v34StyleFiles = listDecodableFiles("res/values*34/styles.xml")
        println("Found v34 style files: $v34StyleFiles")
        v34StyleFiles.forEach { path ->
            resourceXml(path).use { xml ->
                xml.transformStyleItems { _, itemName, _ ->
                    outlineReplacements[itemName]
                }
            }
        }

        println("Google Dialer AMOLED theme successfully applied!")
    }
}

/**
 * Transforms the value of the color elements in a values file.
 */
private fun ResourceXml.transformColors(transform: (name: String, value: String) -> String?) {
    root.children.forEach { color ->
        val name = color.attribute("name") ?: return@forEach
        val value = transform(name, color.text) ?: return@forEach
        if (value != color.text) color.text = value
    }
}

/**
 * Transforms the value of the items of the styles in a styles file.
 */
private fun ResourceXml.transformStyleItems(
    transform: (styleName: String, itemName: String, value: String) -> String?
) {
    root.children("style").forEach { style ->
        val styleName = style.attribute("name") ?: return@forEach
        style.children("item").forEach { item ->
            val itemName = item.attribute("name") ?: return@forEach
            val value = transform(styleName, itemName, item.text) ?: return@forEach
            if (value != item.text) item.text = value
        }
    }
}
