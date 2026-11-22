package app.morphe.patches.dialer

import app.morphe.patcher.patch.resourcePatch
import app.morphe.patcher.patch.resourceXml
import app.morphe.patches.dialer.DialerConstants.COMPATIBILITY_GOOGLE_DIALER

/**
 * Clones package to com.google.android.dialer.amoled for non-rooted users.
 * Note: On devices where Google Phone is pre-installed as a system app,
 * cloning may cause crashes due to system telephony requirements.
 * For rooted devices, leave this OFF and install via Root Mount.
 */
@Suppress("unused")
val googleDialerClonePatch = resourcePatch(
    name = "Clone Dialer Package",
    description = "Changes package to com.google.android.dialer.amoled for non-rooted install. " +
                  "Leave OFF on rooted devices to mount over stock dialer without crashing.",
    default = false
) {
    compatibleWith(COMPATIBILITY_GOOGLE_DIALER)

    execute {
        val packageName = "com.google.android.dialer"
        val newPackageName = "com.google.android.dialer.amoled"

        resourceXml("AndroidManifest.xml").use { manifest ->
            manifest.root.setAttribute("package", newPackageName)

            // Update all custom permissions
            val usesPermissions = manifest.elements("uses-permission").toList()
            manifest.elements("permission").forEach { perm ->
                val oldName = perm.attribute("android:name").orEmpty()
                if (oldName.startsWith("$packageName.")) {
                    val newName = oldName.replaceFirst(packageName, newPackageName)
                    perm.setAttribute("android:name", newName)

                    usesPermissions.firstOrNull { it.attribute("android:name") == oldName }
                        ?.setAttribute("android:name", newName)
                }
            }

            // Update all provider authorities
            manifest.elements("provider").forEach { provider ->
                val authorities = provider.attribute("android:authorities").orEmpty().split(';')
                val newAuthorities = authorities.map { auth ->
                    if (auth.startsWith("$packageName.")) {
                        auth.replaceFirst(packageName, newPackageName)
                    } else if (auth.isNotEmpty() && !auth.endsWith(".amoled")) {
                        "$auth.amoled"
                    } else {
                        auth
                    }
                }
                provider.setAttribute("android:authorities", newAuthorities.joinToString(";"))
            }

            // Update proprietary library requirement to optional
            manifest.elements("uses-library").forEach { lib ->
                if (lib.attribute("android:name") == "com.google.android.dialer.support") {
                    lib.setAttribute("android:required", "false")
                }
            }
        }

        println("AndroidManifest.xml successfully cloned to $newPackageName")
    }
}
