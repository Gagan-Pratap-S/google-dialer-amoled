package app.morphe.patches.dialer

import app.morphe.patcher.patch.resourcePatch
import app.morphe.patches.dialer.DialerConstants.COMPATIBILITY_GOOGLE_DIALER

/**
 * Clones package to com.google.android.dialer.amoled for non-rooted users.
 * Bypasses signature conflicts, duplicate permissions, and provider collisions across all Dialer versions.
 */
@Suppress("unused")
val googleDialerClonePatch = resourcePatch(
    name = "Clone Dialer Package",
    description = "Changes package to com.google.android.dialer.amoled so non-rooted devices can install it without signature conflict.",
    default = true
) {
    compatibleWith(COMPATIBILITY_GOOGLE_DIALER)

    execute {
        val manifestFile = get("AndroidManifest.xml")
        if (!manifestFile.exists()) return@execute

        var text = manifestFile.readText(Charsets.UTF_8)

        // 1. Rename package attribute
        text = text.replace(
            "package=\"com.google.android.dialer\"",
            "package=\"com.google.android.dialer.amoled\""
        )

        // 2. Universally rename all custom permissions and uses-permissions declared under com.google.android.dialer.*
        // Matches e.g. DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION, TRANSIT_MODE_STATE_CHANGE, etc.
        val permRegex = Regex("""(<(?:uses-)?permission(?:-group)?\b[^>]*\bandroid:name=")com\.google\.android\.dialer\.([^"]+")""")
        text = permRegex.replace(text) { matchResult ->
            "${matchResult.groupValues[1]}com.google.android.dialer.amoled.${matchResult.groupValues[2]}"
        }

        // 3. Rename any component permission enforcement attributes (e.g. android:permission="com.google.android.dialer...")
        text = text.replace(
            """android:permission="com.google.android.dialer.""",
            """android:permission="com.google.android.dialer.amoled."""
        )

        // 4. Direct safety net for known permissions
        text = text.replace(
            "com.google.android.dialer.TRANSIT_MODE_STATE_CHANGE",
            "com.google.android.dialer.amoled.TRANSIT_MODE_STATE_CHANGE"
        )
        text = text.replace(
            "com.google.android.dialer.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION",
            "com.google.android.dialer.amoled.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
        )

        // 5. Make proprietary dialer support library optional (prevents install failures on non-Pixel devices)
        text = text.replace(
            "<uses-library android:name=\"com.google.android.dialer.support\" android:required=\"true\"/>",
            "<uses-library android:name=\"com.google.android.dialer.support\" android:required=\"false\"/>"
        )

        // 6. Suffix ContentProvider authorities with .amoled to avoid INSTALL_FAILED_CONFLICTING_PROVIDER
        val authRegex = Regex("""android:authorities="([^"]+)"""")
        text = authRegex.replace(text) { matchResult ->
            val auth = matchResult.groupValues[1]
            if (auth.endsWith(".amoled")) {
                matchResult.value
            } else {
                """android:authorities="$auth.amoled""""
            }
        }

        // 7. Update fallback authority meta-data reference
        text = text.replace(
            """android:value="com.google.android.dialer.preferredsimfallback"""",
            """android:value="com.google.android.dialer.preferredsimfallback.amoled""""
        )

        manifestFile.writeText(text, Charsets.UTF_8)
        println("AndroidManifest.xml successfully cloned to com.google.android.dialer.amoled (all permissions isolated)")
    }
}
