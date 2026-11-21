package app.morphe.patches.dialer

import app.morphe.patcher.patch.resourcePatch
import app.morphe.patches.dialer.DialerConstants.COMPATIBILITY_GOOGLE_DIALER

/**
 * Clones package to com.google.android.dialer.amoled for non-rooted users.
 * Bypasses signature conflicts, duplicate permissions, and provider collisions.
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

        // 2. Rename custom receiver signature permissions to avoid INSTALL_FAILED_DUPLICATE_PERMISSION
        text = text.replace(
            "com.google.android.dialer.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION",
            "com.google.android.dialer.amoled.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
        )

        // 3. Make proprietary dialer support library optional (prevents install failures on non-Pixel devices)
        text = text.replace(
            "<uses-library android:name=\"com.google.android.dialer.support\" android:required=\"true\"/>",
            "<uses-library android:name=\"com.google.android.dialer.support\" android:required=\"false\"/>"
        )

        // 4. Suffix ContentProvider authorities with .amoled to avoid INSTALL_FAILED_CONFLICTING_PROVIDER
        val authRegex = Regex("""android:authorities="([^"]+)"""")
        text = authRegex.replace(text) { matchResult ->
            val auth = matchResult.groupValues[1]
            if (auth.endsWith(".amoled")) {
                matchResult.value
            } else {
                """android:authorities="$auth.amoled""""
            }
        }

        // 5. Update fallback authority meta-data reference
        text = text.replace(
            """android:value="com.google.android.dialer.preferredsimfallback"""",
            """android:value="com.google.android.dialer.preferredsimfallback.amoled""""
        )

        manifestFile.writeText(text, Charsets.UTF_8)
        println("AndroidManifest.xml successfully cloned to com.google.android.dialer.amoled")
    }
}
