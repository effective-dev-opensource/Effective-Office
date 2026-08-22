/*
 * Minimal XML DOM for Android Vector Drawables, in pure Kotlin: javax.xml has no Kotlin/Native
 * target. See [XmlVectorParser] for the parser that consumes it.
 */
package band.effective.office.tablet.core.ui.res.vectorxml

internal interface Node {
    val textContent: String?
    val nodeName: String
    val localName: String
    val childNodes: NodeList
    val namespaceURI: String

    fun lookupPrefix(namespaceURI: String): String
}

internal interface Element : Node {
    fun getAttributeNS(nameSpaceURI: String, localName: String): String
    fun getAttribute(name: String): String
}

internal interface NodeList {
    fun item(i: Int): Node
    val length: Int
}

private class ElementImpl(
    override val localName: String,
    override val nodeName: String,
    override val namespaceURI: String,
    val prefixMap: Map<String, String>,
    val attributes: Map<String, String>,
) : Element {
    override var textContent: String? = null
    val children = mutableListOf<Node>()
    override val childNodes: NodeList
        get() = object : NodeList {
            override fun item(i: Int): Node = children[i]
            override val length: Int get() = children.size
        }

    override fun getAttributeNS(nameSpaceURI: String, localName: String): String {
        val prefix = prefixMap[nameSpaceURI]
        val attrKey = if (prefix == null) localName else "$prefix:$localName"
        return getAttribute(attrKey)
    }

    override fun getAttribute(name: String): String = attributes[name] ?: ""

    override fun lookupPrefix(namespaceURI: String): String = prefixMap[namespaceURI] ?: ""
}

/** Parses [xml] into a DOM tree and returns its root [Element]. */
internal fun parse(xml: String): Element {
    var root: ElementImpl? = null
    val nodeStack = mutableListOf<ElementImpl>()
    val curPrefixMap = mutableMapOf<String, String>()

    val xmlNoComments = xml.replace(Regex("<!--[\\s\\S]*?-->"), "")

    val tagRegex = Regex("<(/|\\?)?([a-zA-Z0-9_:-]+)([^>]*)>")
    val attrRegex = Regex("([a-zA-Z0-9_:-]+)\\s*=\\s*([\"'])(.*?)\\2", RegexOption.DOT_MATCHES_ALL)

    var lastMatchEnd = 0

    tagRegex.findAll(xmlNoComments).forEach { matchResult ->
        val textBetween = xmlNoComments.substring(lastMatchEnd, matchResult.range.first).trim()
        if (textBetween.isNotEmpty() && nodeStack.isNotEmpty()) {
            nodeStack.last().textContent = (nodeStack.last().textContent ?: "") + textBetween
        }
        lastMatchEnd = matchResult.range.last + 1

        val tagType = matchResult.groupValues[1]
        val tagName = matchResult.groupValues[2]
        val attrString = matchResult.groupValues[3]

        if (tagType == "?") return@forEach

        val isEndTag = tagType == "/"

        if (isEndTag) {
            if (nodeStack.isNotEmpty()) {
                val node = nodeStack.removeLast()
                if (node.nodeName != tagName) {
                    throw Exception("Mismatched tags: expected ${node.nodeName}, found $tagName")
                }
            }
        } else {
            val attributes = mutableMapOf<String, String>()
            attrRegex.findAll(attrString).forEach { attrMatch ->
                val attrName = attrMatch.groupValues[1]
                val attrValue = attrMatch.groupValues[3]
                attributes[attrName] = attrValue

                if (attrName.startsWith("xmlns:")) {
                    curPrefixMap[attrValue] = attrName.substringAfter("xmlns:")
                }
            }

            val localName = tagName.substringAfter(":")
            val namespacePrefix = if (tagName.contains(":")) tagName.substringBefore(":") else ""
            val namespaceURI = curPrefixMap.entries.firstOrNull { it.value == namespacePrefix }?.key ?: ""

            val node = ElementImpl(
                localName = localName,
                nodeName = tagName,
                namespaceURI = namespaceURI,
                prefixMap = curPrefixMap.toMap(),
                attributes = attributes,
            )

            if (root == null) root = node
            if (nodeStack.isNotEmpty()) {
                nodeStack.last().children.add(node)
            }

            if (!attrString.trim().endsWith("/")) {
                nodeStack.add(node)
            }
        }
    }

    return root ?: throw Exception("No root element found")
}
