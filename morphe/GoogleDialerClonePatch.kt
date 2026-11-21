package app.morphe.patches.google.dialer.clone

import app.morphe.patcher.patch.resourcePatch
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Clones package to com.google.android.dialer.amoled for non-rooted users.
 */
@Suppress("unused")
val googleDialerClonePatch = resourcePatch(
    name = "Clone Dialer Package",
    description = "Changes package to com.google.android.dialer.amoled so non-rooted devices can install it without signature conflict.",
    default = false
) {
    compatibleWith("com.google.android.dialer")

    execute {
        val manifestFile = get("AndroidManifest.xml")
        if (!manifestFile.exists()) return@execute

        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(manifestFile)
        val root = doc.documentElement

        // 1. Rename package attribute
        root.setAttribute("package", "com.google.android.dialer.amoled")

        // 2. Suffix ContentProvider authorities
        val providers = doc.getElementsByTagName("provider")
        for (i in 0 until providers.length) {
            val prov = providers.item(i) as Element
            val auth = prov.getAttribute("android:authorities")
            if (auth.isNotEmpty() && !auth.endsWith(".amoled")) {
                prov.setAttribute("android:authorities", "$auth.amoled")
            }
        }

        TransformerFactory.newInstance().newTransformer().transform(DOMSource(doc), StreamResult(manifestFile))
    }
}
