package app.morphe.patches.google.dialer.theme

import app.morphe.patcher.patch.ResourcePatchContext
import app.morphe.patcher.patch.resourcePatch
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * AMOLED Dark Theme & Silhouette Avatar Fix for Google Phone (Google Dialer).
 * Compatible with Morphe Manager / ReVanced Manager ecosystems.
 */
@Suppress("unused")
val googleDialerThemePatch = resourcePatch(
    name = "AMOLED Dark Theme",
    description = "Converts Google Phone dark theme from grey into pure pitch-black (#000000), " +
                  "enhances dialpad readability with pure white text, and fixes unsaved contact avatar outlines.",
    default = true
) {
    compatibleWith("com.google.android.dialer")

    execute {
        println("Applying Google Dialer AMOLED modifications...")

        // 1. Pure Pitch-Black & High-Contrast colors in res/values-night/colors.xml
        patchXmlColors(
            file = get("res/values-night/colors.xml"),
            replacements = mapOf(
                "settingslib_materialColorSurfaceContainer" to "#000000",
                "settingslib_materialColorSurfaceContainerHigh" to "#0a0a0a",
                "settingslib_materialColorSurfaceContainerHighest" to "#141414",
                "settingslib_materialColorSurfaceVariant" to "#1f1f1f",
                "bam_center_fragment_background" to "#000000",
                "survey_surface_color" to "#000000",
                "survey_surface_color_elevation_2" to "#050505",
                "survey_surface_color_elevation_3" to "#0a0a0a",
                "survey_alert_dialog_bg_color" to "#000000",
                // High-contrast white typography for outdoor legibility
                "dialpad_icon_text_enabled" to "#ffffffff",
                "dialpad_icon_text_disabled" to "#80ffffff",
                "empty_content_view_message" to "#ffffffff",
                "settingslib_materialColorOnSurface" to "#ffffffff",
                "settingslib_materialColorOnSurfaceVariant" to "#ffffffff"
            )
        )

        // 2. Dialog backgrounds in res/values-night-v31/colors.xml
        patchXmlColors(
            file = get("res/values-night-v31/colors.xml"),
            replacements = mapOf(
                "google_marterial_dialog_background_color" to "#000000",
                "settingslib_dialog_background" to "#000000",
                "settingslib_materialColorSurfaceContainer" to "#000000",
                "settingslib_materialColorSurfaceContainerHigh" to "#0a0a0a",
                "settingslib_materialColorSurfaceContainerHighest" to "#141414",
                "settingslib_materialColorSurfaceVariant" to "#1f1f1f"
            )
        )

        // 3. Silhouette Outline Avatar Fix in values-night-v34 and values-v34
        val outlineReplacements = mapOf(
            "settingslib_materialColorOutline" to "@android:color/system_outline_dark",
            "gm3_sys_color_dynamic_dark_outline" to "@android:color/system_outline_dark",
            "m3_sys_color_dynamic_dark_outline" to "@android:color/system_outline_dark",
            "sud_system_outline_dark" to "@android:color/system_outline_dark",
            "mc3_outline" to "@android:color/system_outline_dark"
        )

        listOf(
            "res/values-night-v34/colors.xml",
            "res/values-v34/colors.xml",
            "res/values-v34/styles.xml"
        ).forEach { path ->
            patchXmlColors(file = get(path), replacements = outlineReplacements)
        }

        println("Google Dialer AMOLED theme successfully applied!")
    }
}

/**
 * Helper to update XML color and style items safely using DOM parser.
 */
private fun patchXmlColors(file: File, replacements: Map<String, String>) {
    if (!file.exists()) return

    try {
        val docFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = docFactory.newDocumentBuilder()
        val doc = docBuilder.parse(file)
        doc.documentElement.normalize()

        var modified = false

        // Check both <color> and <item> tags
        listOf("color", "item").forEach { tagName ->
            val nodes = doc.getElementsByTagName(tagName)
            for (i in 0 until nodes.length) {
                val element = nodes.item(i) as Element
                val nameAttr = element.getAttribute("name")
                if (replacements.containsKey(nameAttr)) {
                    element.textContent = replacements[nameAttr]
                    modified = true
                }
            }
        }

        if (modified) {
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.transform(DOMSource(doc), StreamResult(file))
        }
    } catch (e: Exception) {
        println("Warning: Could not parse XML ${file.path}: ${e.message}")
    }
}
