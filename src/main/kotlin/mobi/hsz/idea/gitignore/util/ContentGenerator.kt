package mobi.hsz.idea.gitignore.util

class ContentGenerator private constructor() {

    companion object {
        fun generate(currentContent: String, content: Set<String>, ignoreDuplicates: Boolean, ignoreComments: Boolean) =
            content.joinToString(Constants.NEWLINE) { entry ->
                entry.split(Constants.NEWLINE).toMutableList().run {
                    iterator().takeIf { ignoreDuplicates }?.let { iterator ->
                        val currentLines = currentContent.split(Constants.NEWLINE).filter {
                            it.isNotEmpty() && !it.startsWith(Constants.HASH)
                        }.toMutableList()

                        while (iterator.hasNext()) {
                            val line = iterator.next().trim { it <= ' ' }
                            if (line.isEmpty() || line.startsWith(Constants.HASH)) {
                                continue
                            }

                            if (currentLines.contains(line)) {
                                iterator.remove()
                            } else {
                                currentLines.add(line)
                            }
                        }
                    }

                    iterator().takeIf { ignoreComments }?.let { iterator ->
                        while (iterator.hasNext()) {
                            val line = iterator.next().trim { it <= ' ' }
                            if (line.isEmpty() || line.startsWith(Constants.HASH)) {
                                iterator.remove()
                            }
                        }
                    }

                    joinToString(Constants.NEWLINE).replace("\r", "").let { content ->
                        content + Constants.NEWLINE.takeIf { it.isNotEmpty() }
                    }
                }
            }
    }
}
