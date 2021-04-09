// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package mobi.hsz.idea.gitignore.codeInspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.services.IgnoreMatcher
import mobi.hsz.idea.gitignore.util.Constants
import mobi.hsz.idea.gitignore.util.Glob
import mobi.hsz.idea.gitignore.util.Utils

/**
 * Inspection tool that checks if entries are covered by others.
 */
class IgnoreCoverEntryInspection : LocalInspectionTool() {

    /**
     * Reports problems at file level. Checks if entries are covered by other entries.
     *
     * @param file       current working file to check
     * @param manager    [InspectionManager] to ask for [ProblemDescriptor]'s from
     * @param isOnTheFly true if called during on the fly editor highlighting. Called from Inspect Code action otherwise
     * @return `null` if no problems found or not applicable at file level
     */
    @Suppress("ComplexMethod", "NestedBlockDepth", "ReturnCount")
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        val virtualFile = file.virtualFile
        if (file !is IgnoreFile || !Utils.isInProject(virtualFile, file.getProject())) {
            return null
        }
        val contextDirectory = virtualFile.parent ?: return null
        val problemsHolder = ProblemsHolder(manager, file, isOnTheFly)

        val ignored = mutableSetOf<String>()
        val unignored = mutableSetOf<String>()
        val result = mutableListOf<Pair<IgnoreEntry, IgnoreEntry>>()
        val map = mutableMapOf<IgnoreEntry, Set<String>>()

        val entries = file.findChildrenByClass(IgnoreEntry::class.java)
        val matcher = file.project.service<IgnoreMatcher>()
        val matchedMap = getPathsSet(contextDirectory, entries, matcher)

        entries.forEach entries@{ entry ->
            ProgressManager.checkCanceled()
            val matched = matchedMap[entry] ?: return@entries
            val intersection: Collection<String>

            if (!entry.isNegated) {
                ignored.addAll(matched)
                intersection = unignored.intersect(matched)

                if (unignored.removeAll(intersection)) {
                    return@entries
                }
            } else {
                unignored.addAll(matched)
                intersection = ignored.intersect(matched)

                if (ignored.removeAll(intersection)) {
                    return@entries
                }
            }

            map.keys.forEach recent@{ recent ->
                ProgressManager.checkCanceled()
                val recentValues = map[recent] ?: return@recent
                if (recentValues.isEmpty() || matched.isEmpty()) {
                    return@recent
                }
                if (entry.isNegated == recent.isNegated) {
                    if (recentValues.containsAll(matched)) {
                        result.add(Pair.create(recent, entry))
                    } else if (matched.containsAll(recentValues)) {
                        result.add(Pair.create(entry, recent))
                    }
                } else if (intersection.containsAll(recentValues)) {
                    result.add(Pair.create(entry, recent))
                }
            }
            map[entry] = matched
        }

        result.forEach { pair ->
            problemsHolder.registerProblem(
                pair.second,
                message(pair.first, virtualFile, isOnTheFly),
                IgnoreRemoveEntryFix(pair.second)
            )
        }

        return problemsHolder.resultsArray
    }

    /**
     * Returns the paths list for the given [IgnoreEntry] array in [VirtualFile] context.
     * Stores fetched data in [.cacheMap] to limit the queries to the files tree.
     *
     * @param contextDirectory current context
     * @param entries          to check
     * @return paths list
     */
    private fun getPathsSet(contextDirectory: VirtualFile, entries: Array<IgnoreEntry>, matcher: IgnoreMatcher) =
        mutableMapOf<IgnoreEntry, Set<String>>().apply {
            val found = Glob.findAsPaths(contextDirectory, entries.toList(), matcher, true)
            found.forEach { (key, value) ->
                ProgressManager.checkCanceled()
                this[key] = value
            }
        }

    override fun runForWholeFile() = true

    /**
     * Helper for inspection message generating.
     *
     * @param coveringEntry entry that covers message related
     * @param virtualFile   current working file
     * @param onTheFly      true if called during on the fly editor highlighting. Called from Inspect Code action
     * otherwise
     * @return generated message [String]
     */
    private fun message(coveringEntry: IgnoreEntry, virtualFile: VirtualFile, onTheFly: Boolean): String {
        val document = FileDocumentManager.getInstance().getDocument(virtualFile)

        return if (onTheFly || document == null) {
            IgnoreBundle.message("codeInspection.coverEntry.message", "\'" + coveringEntry.text + "\'")
        } else {
            IgnoreBundle.message(
                "codeInspection.coverEntry.message",
                "<a href=\"" + virtualFile.url + Constants.HASH + coveringEntry.textRange.startOffset + "\">" + coveringEntry.text + "</a>"
            )
        }
    }
}
