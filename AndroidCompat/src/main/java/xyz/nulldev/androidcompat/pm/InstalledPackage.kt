package xyz.nulldev.androidcompat.pm

import android.content.pm.PackageInfo
import android.os.Bundle
import com.googlecode.d2j.dex.Dex2jar
import net.dongliu.apk.parser.ApkFile
import net.dongliu.apk.parser.ApkParsers
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

data class InstalledPackage(val root: File) {
    val apk = File(root, "package.apk")
    val jar = File(root, "translated.jar")

    val info: PackageInfo
        get() = ApkParsers.getMetaInfo(apk).toPackageInfo(apk).also {
            val parsed = ApkFile(apk)
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = parsed.manifestXml.byteInputStream().use(dBuilder::parse)

            it.applicationInfo.metaData = Bundle().apply {
                val appTag = doc.getElementsByTagName("application").item(0)

                appTag?.childNodes?.run {
                    toList().asSequence()
                        .filter { it.nodeType == Node.ELEMENT_NODE }
                        .map { it as Element }
                        .filter { it.tagName == "meta-data" }
                        .forEach {
                            putString(
                                it.attributes.getNamedItem("android:name").nodeValue,
                                it.attributes.getNamedItem("android:value").nodeValue,
                            )
                        }
                }
            }
        }

    fun writeJar() {
        try {
            Dex2jar.from(apk).to(jar.toPath())
        } catch (e: Exception) {
            jar.delete()
        }
    }

    companion object {
        fun NodeList.toList(): List<Node> {
            val out = mutableListOf<Node>()

            for (i in 0 until length)
                out += item(i)

            return out
        }
    }
}
