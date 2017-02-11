# Change Log

## [v1.7](https://github.com/hsz/idea-gitignore/tree/v1.7) (2017-02-11)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.6...v1.7)

**Implemented enhancements:**

- *Hide ignored files and directories in the project tree view*
- *Indicate that parent contains extra elements if children are hidden*
- Dialog box that allows to untrack ignored files (performs git rm --cached command)
- Stylint (.stylintignore) support [\#279](https://github.com/hsz/idea-gitignore/issues/279)
- Project Tree View coloring refactoring (performance)

**Fixed bugs:**

- *Fixed colors for tracked and ignored files, additional info label is implemented* [\#296](https://github.com/hsz/idea-gitignore/issues/296) [\#295](https://github.com/hsz/idea-gitignore/issues/295) [\#284](https://github.com/hsz/idea-gitignore/issues/284)
- ClassNotFoundException: mobi.hsz.idea.gitignore.FilesIndexCacheProjectComponent [\#297](https://github.com/hsz/idea-gitignore/issues/297)
- Assertion failed: Registering post-startup activity that will never be run [\#290](https://github.com/hsz/idea-gitignore/issues/290)
- Component name collision: UpdateComponent [\#289](https://github.com/hsz/idea-gitignore/issues/289)
- Ignore Files Support pane scrollbar issue [\#286](https://github.com/hsz/idea-gitignore/issues/286)
- Outer ignore file panel now has max height rule [\#257](https://github.com/hsz/idea-gitignore/issues/257)
- Properly coloring of subdirectories [\#255](https://github.com/hsz/idea-gitignore/issues/255)
- Setting "Enable ignoring" does not really work [\#298](https://github.com/hsz/idea-gitignore/issues/238)

## [v1.6](https://github.com/hsz/idea-gitignore/tree/v1.6) (2016-11-23)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.5...v1.6)

**Implemented enhancements:**

- Unignore Files [\#250](https://github.com/hsz/idea-gitignore/issues/250)
- Default icon for .ignore files [\#258](https://github.com/hsz/idea-gitignore/issues/258)
- StyleLint (.stylelintignore) support [\#241](https://github.com/hsz/idea-gitignore/issues/241)

**Fixed bugs:**

- Method OuterIgnoreLoaderComponent.getOuterFiles must not return null [\#240](https://github.com/hsz/idea-gitignore/issues/240)
- Coloring problem at project startup [\#246](https://github.com/hsz/idea-gitignore/issues/246)
- Fixed freeze report in ExternalExec [\#256](https://github.com/hsz/idea-gitignore/issues/256)
- ConcurrentModificationException in IgnoreReferenceSet [\#269](https://github.com/hsz/idea-gitignore/issues/269)
- Stop indexing excluded files/directories [\#273](https://github.com/hsz/idea-gitignore/issues/273)
- NoSuchMethodError: com.intellij.util.containers.ContainerUtil.getFirstItem [\#263](https://github.com/hsz/idea-gitignore/issues/263)
- CacheMap.getParentStatus must not return null [\#242](https://github.com/hsz/idea-gitignore/issues/242)

## [v1.5](https://github.com/hsz/idea-gitignore/tree/v1.5) (2016-06-13)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.4.1...v1.5)

**Implemented enhancements:**

- Create user templates with existing ignore entries
- Favorite templates feature [\#236](https://github.com/hsz/idea-gitignore/issues/236)
- Generate without comments and empty lines [\#229](https://github.com/hsz/idea-gitignore/issues/229)

**Fixed bugs:**

- NoSuchMethodError on ContainerUtil.notNullize [\#232](https://github.com/hsz/idea-gitignore/issues/232)
- Refactoring of the external executable process [\#233](https://github.com/hsz/idea-gitignore/issues/233)

## [v1.4.1](https://github.com/hsz/idea-gitignore/tree/v1.4.1) (2016-05-24)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.3.3...v1.4.1)

**Implemented enhancements:**

- Add Unversioned Files to .gitignore (Git) [\#124](https://github.com/hsz/idea-gitignore/issues/124)
- Add to 'exclude' (Git) [\#133](https://github.com/hsz/idea-gitignore/issues/133)
- Exclude file preview (Git) [\#132](https://github.com/hsz/idea-gitignore/issues/132)
- Allow user to specify position of appended entries [\#216](https://github.com/hsz/idea-gitignore/issues/216)
- JetPack (.jpmignore) support [\#215](https://github.com/hsz/idea-gitignore/issues/215)

**Fixed bugs:**

- NoSuchMethodError in IDEA 2016.2 EAP [\#225](https://github.com/hsz/idea-gitignore/issues/225)
- ConcurrentModificationException [\#221](https://github.com/hsz/idea-gitignore/issues/221)
- ClassCastException: ...FileImpl cannot be cast to IgnoreFile [\#220](https://github.com/hsz/idea-gitignore/issues/220)
- Problem files coloring [\#219](https://github.com/hsz/idea-gitignore/issues/219)
- Subfolders recognition [\#218](https://github.com/hsz/idea-gitignore/issues/218)
- Tricky ignore rules issue [\#214](https://github.com/hsz/idea-gitignore/issues/214)
- "Entry never used" invalid [\#213](https://github.com/hsz/idea-gitignore/issues/213)
- NoSuchMethodError exception [\#211](https://github.com/hsz/idea-gitignore/issues/211)
- Double star pattern /\*\* doesn't gray out files in subdirectories [\#165](https://github.com/hsz/idea-gitignore/issues/165)

## [v1.3.3](https://github.com/hsz/idea-gitignore/tree/v1.3.3) (2016-04-04)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.2...v1.3.3)

**Implemented enhancements:**

- Import / Export templates [\#194](https://github.com/hsz/idea-gitignore/issues/194)
- Generate rules without duplicates [\#164](https://github.com/hsz/idea-gitignore/issues/164)
- Global gitignore section resize [\#187](https://github.com/hsz/idea-gitignore/issues/187)
- .eslintignore support [\#175](https://github.com/hsz/idea-gitignore/issues/175)
- .cfignore support [\#161](https://github.com/hsz/idea-gitignore/issues/161)

**Fixed bugs:**

- CPU performance fixes [\#154](https://github.com/hsz/idea-gitignore/issues/154), [\#204](https://github.com/hsz/idea-gitignore/issues/204)
- Ignored files are no longer being colored correctly [\#174](https://github.com/hsz/idea-gitignore/issues/174)
- Fatal Error on changing project name [\#203](https://github.com/hsz/idea-gitignore/issues/203) [\#193](https://github.com/hsz/idea-gitignore/issues/193) [\#170](https://github.com/hsz/idea-gitignore/issues/170)
- .gitignore\_global causes plugin to crash [\#198](https://github.com/hsz/idea-gitignore/issues/198)
- ClassCastException when editing .gitignore on remote file system [\#196](https://github.com/hsz/idea-gitignore/issues/196)
- ClassCastException on JSP [\#195](https://github.com/hsz/idea-gitignore/issues/195)
- Global gitignore section scrolling [\#186](https://github.com/hsz/idea-gitignore/issues/186)
- "Unclosed character class" warning is wrong [\#166](https://github.com/hsz/idea-gitignore/issues/166)
- IgnoreSettings settings must produce stable state [\#162](https://github.com/hsz/idea-gitignore/issues/162)
- Global ignore error [\#190](https://github.com/hsz/idea-gitignore/issues/190)
- IgnoreParserUtil exception [\#211](https://github.com/hsz/idea-gitignore/issues/211)

## [v1.2](https://github.com/hsz/idea-gitignore/tree/v1.2) (2015-08-13)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.1.4...v1.2)

**Implemented enhancements:**

- Prevent ignore file creation when dialog generator is canceled [\#156](https://github.com/hsz/idea-gitignore/issues/156)
- Add leading slash to the entry when invoking `Add to ignore file` action [\#141](https://github.com/hsz/idea-gitignore/issues/141)
- Floobits (.flooignore) support [\#152](https://github.com/hsz/idea-gitignore/issues/152)
- Add ignore rules at the cursor position [\#153](https://github.com/hsz/idea-gitignore/issues/153)
- Insert `syntax: glob` for non-glob ignore types when creating new file [\#158](https://github.com/hsz/idea-gitignore/issues/158)
- Close ignored files action [\#160](https://github.com/hsz/idea-gitignore/issues/160)

**Fixed bugs:**

- Project base dir should not be null in Utils.isInProject [\#145](https://github.com/hsz/idea-gitignore/issues/145)
- NPE in FossilLanguage.getOuterFile [\#157](https://github.com/hsz/idea-gitignore/issues/157)
- Removed `\0` from the generated content. [\#155](https://github.com/hsz/idea-gitignore/issues/155)
- ClassCastException while indexing [\#150](https://github.com/hsz/idea-gitignore/issues/150)
- IllegalAccessError for StringUtil.escapeChar(String, char) in IntelliJ 12.x [\#149](https://github.com/hsz/idea-gitignore/issues/149)
- Migration to JPanel because of the broken IntelliJ API (JBPanel NoClassDefFoundError) [\#146](https://github.com/hsz/idea-gitignore/issues/146)
- IndexOutOfBoundsException [\#144](https://github.com/hsz/idea-gitignore/issues/144)
- InvalidVirtualFileAccessException [\#107](https://github.com/hsz/idea-gitignore/issues/107)
- Shorten ignore file path in the context menu [\#148](https://github.com/hsz/idea-gitignore/issues/148)
- Assertion and NPE errors fix in Resources
- Fix for including outer file rules
- Performance fixes

## [v1.1.4](https://github.com/hsz/idea-gitignore/tree/v1.1.4) (2015-06-02)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.1.2...v1.1.4)

**Fixed bugs:**

- NoSuchMethodError ContainerUtil.isEmpty(Ljava/util/List;) [\#140](https://github.com/hsz/idea-gitignore/issues/140)
- CacheMap.getParentStatus must not return null [\#138](https://github.com/hsz/idea-gitignore/issues/138)
- Utils.isUnder - directory must not be null [\#137](https://github.com/hsz/idea-gitignore/issues/137)
- NPE after adding null to the files list [\#130](https://github.com/hsz/idea-gitignore/issues/130)
- Exclude ignored `.ignore` files from parsing [\#125](https://github.com/hsz/idea-gitignore/issues/125)
- Error while opening project - messageBus not initialized [\#123](https://github.com/hsz/idea-gitignore/issues/123)
- Access is allowed from event dispatch thread only [\#122](https://github.com/hsz/idea-gitignore/issues/122)

## [v1.1.2](https://github.com/hsz/idea-gitignore/tree/v1.1.2) (2015-05-11)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.1.1...v1.1.2)

**Implemented enhancements:**

- Disable recursive .ignore check [\#114](https://github.com/hsz/idea-gitignore/issues/114)
- Move plugin settings from Other Settings to Version Control section [\#106](https://github.com/hsz/idea-gitignore/issues/106)
- Nested repositories handling regression [\#105](https://github.com/hsz/idea-gitignore/issues/105)
- Skywinder's [github-changelog-generator](https://github.com/skywinder/github-changelog-generator)

**Fixed bugs:**

- Argument for @NotNull parameter 'directory' of Utils.isUnder must not be null [\#115](https://github.com/hsz/idea-gitignore/issues/115)
- Ignored directory with single asterisk is ignore recursively [\#113](https://github.com/hsz/idea-gitignore/issues/113)
- Unexpected ignore coloring of nested file listed as a top level ignore only [\#112](https://github.com/hsz/idea-gitignore/issues/112)
- Throwable: Directory index is already disposed for Project \(Disposed\) PROJECT\_NAME  [\#109](https://github.com/hsz/idea-gitignore/issues/109)
- Argument for @NotNull parameter 'editor' of EditorFactoryImpl.releaseEditor must not be null [\#108](https://github.com/hsz/idea-gitignore/issues/108)
- Indexing node\_modules every time when open the project [\#104](https://github.com/hsz/idea-gitignore/issues/104)
- Extremely slow behaviour [\#100](https://github.com/hsz/idea-gitignore/issues/100)
- WS10 RC Memory Leak [\#99](https://github.com/hsz/idea-gitignore/issues/99)
- Lags\(several seconds\) while editing .ignore [\#95](https://github.com/hsz/idea-gitignore/issues/95)

## [v1.1.1](https://github.com/hsz/idea-gitignore/tree/v1.1.1) (2015-04-13)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.1...v1.1.1)

**Implemented enhancements:**

- Expensive and often read-lock blocks common IDE actions [\#94](https://github.com/hsz/idea-gitignore/issues/94)
- Disable non-VCS languages ignoring by default
- EmptyFileManager class cast exception fix

## [v1.1](https://github.com/hsz/idea-gitignore/tree/v1.1) (2015-04-12)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.0.2...v1.1)

**Implemented enhancements:**

- Handling outside ignore rules (.gitignore global file)
- Languages settings (configurable list in `New file`, marking as ignored in Project tree)
- `.jshintignore` support (JSHint)
- `ignore-glob` support (Fossil)
- `.tfignore` support (Team Foundation)
- `.p4ignore` support (Perforce)
- Default user template

**Fixed bugs:**

- Fixed performance issues
- Fixed ignored files coloring ([\#85](https://github.com/hsz/idea-gitignore/issues/85), [\#87](https://github.com/hsz/idea-gitignore/issues/87))
- Fixed "Add to null" issue [\#96](https://github.com/hsz/idea-gitignore/issues/96)
- Fixed @NotNull parameter ancestor [\#93](https://github.com/hsz/idea-gitignore/issues/93)
- ConcurrentModificationException [\#92](https://github.com/hsz/idea-gitignore/issues/92)
- "Cover entry" inspection and non-existing folders [\#88](https://github.com/hsz/idea-gitignore/issues/88)

**Merged pull requests:**

- Update README.md [\#90](https://github.com/hsz/idea-gitignore/pull/90) ([mathben](https://github.com/mathben))

## [v1.0.2](https://github.com/hsz/idea-gitignore/tree/v1.0.2) (2015-03-04)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.0.1...v1.0.2)

**Fixed bugs:**

- CLion VerifyError hotfix ([\#89](https://github.com/hsz/idea-gitignore/issues/89))

## [v1.0.1](https://github.com/hsz/idea-gitignore/tree/v1.0.1) (2015-03-03)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.0...v1.0.1)

**Implemented enhancements:**

- `.mtn-ignore` support (Monotone version control)
- Add "file ignored color" [\#79](https://github.com/hsz/idea-gitignore/issues/79)

**Fixed bugs:**

- ConcurrentModificationException fix ([\#84](https://github.com/hsz/idea-gitignore/issues/84))
- Wrong ignore indicator with nested entries fix ([\#85](https://github.com/hsz/idea-gitignore/issues/85))
- Cover inspection respects negated entries
- Allow enable/disable coloring ignored files ([\#87](https://github.com/hsz/idea-gitignore/issues/87))
- Null Exception Intellij 14 [\#86](https://github.com/hsz/idea-gitignore/issues/86)
- .hgignore should never consider 'syntax: glob' as "never used" [\#82](https://github.com/hsz/idea-gitignore/issues/82)

**Closed issues:**

- Monotone version control support [\#83](https://github.com/hsz/idea-gitignore/issues/83)

## [v1.0](https://github.com/hsz/idea-gitignore/tree/v1.0) (2015-03-01)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.9...v1.0)

**Implemented enhancements:**

- **Ignored files coloring** in the Project View (customizable style)
- **Regex support** (used with Mercurial and Darcs)
- **Syntax switching** support for Mercurial (with suggestion)
- Show Ignored Files dialog sorting is weird [\#40](https://github.com/hsz/idea-gitignore/issues/40)
- Incorrect entry syntax inspection
- `.boringignore` support (Darcs version control)

**Fixed bugs:**

- "Show ignored files" is buggy [\#38](https://github.com/hsz/idea-gitignore/issues/38)

**Closed issues:**

- Mercurial regexp support \(.hgignore file\) [\#77](https://github.com/hsz/idea-gitignore/issues/77)
- Darcs support \(.boring file\) [\#76](https://github.com/hsz/idea-gitignore/issues/76)
- Feature request: chefignore support [\#75](https://github.com/hsz/idea-gitignore/issues/75)

## [v0.9](https://github.com/hsz/idea-gitignore/tree/v0.9) (2015-02-19)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.8.1...v0.9)

**Implemented enhancements:**

- `.chefignore` support (Chef automation tool)
- `.cvsignore` support (CVS version control)
- `.bzrignore` support (Bazaar version control)
- Grouped actions for creating new file
- Ant build file fix
- Move "Ignore files support" settings to VCS section [\#70](https://github.com/hsz/idea-gitignore/issues/70)

## [v0.8.1](https://github.com/hsz/idea-gitignore/tree/v0.8.1) (2015-02-04)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.8...v0.8.1)

**Implemented enhancements:**

- Plugin renamed to `.ignore`
- Inspections are back again
- Use official Git icon/logo [\#65](https://github.com/hsz/idea-gitignore/issues/65)
- Retina-ready Icon [\#63](https://github.com/hsz/idea-gitignore/issues/63)

**Fixed bugs:**

- NoSuchMethodError in IntelliJ Idea 12 [\#71](https://github.com/hsz/idea-gitignore/issues/71)
- Few minor fixes

**Closed issues:**

- .dockerignore support [\#58](https://github.com/hsz/idea-gitignore/issues/58)
- .npmignore support [\#57](https://github.com/hsz/idea-gitignore/issues/57)

## [v0.8](https://github.com/hsz/idea-gitignore/tree/v0.8) (2014-12-22)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.7...v0.8)

**Implemented enhancements:**

- Relative entry inspection with quick fix
- IDE \(PhpStorm\) does not recognize path started by dot in gitignore file [\#66](https://github.com/hsz/idea-gitignore/issues/66)

**Fixed bugs:**

- Add template... on External Libraries [\#68](https://github.com/hsz/idea-gitignore/issues/68)
- Fixed template presentation error [\#67](https://github.com/hsz/idea-gitignore/issues/67)
- Exception when closing a project [\#64](https://github.com/hsz/idea-gitignore/issues/64)
- Renaming project root directory cause IllegalArgumentException [\#49](https://github.com/hsz/idea-gitignore/issues/49)

**Closed issues:**

- Custom ignore templates [\#53](https://github.com/hsz/idea-gitignore/issues/53)

**Merged pull requests:**

- .hgignore .npmignore .docker support [\#69](https://github.com/hsz/idea-gitignore/pull/69) ([hsz](https://github.com/hsz))

## [v0.7](https://github.com/hsz/idea-gitignore/tree/v0.7) (2014-11-17)

**Implemented enhancements:**

- User templates - you can create your own templates in <kbd>Settings > Gitignore > User templates</kbd>

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.6.1...v0.7)

**Fixed bugs:**

- Receiving JDOM exception after update to 0.6.1 [\#61](https://github.com/hsz/idea-gitignore/issues/61)
- Fixed donation URL

**Merged pull requests:**

- Set donationShown variable to an empty string to avoid JDOM error. [\#62](https://github.com/hsz/idea-gitignore/pull/62) ([danpfe](https://github.com/danpfe))

## [v0.6.1](https://github.com/hsz/idea-gitignore/tree/v0.6.1) (2014-11-13)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.6...v0.6.1)

**Implemented enhancements:**

- Global settings section

**Closed issues:**

- Make suggestion to add .gitignore file optional [\#18](https://github.com/hsz/idea-gitignore/issues/18)
- Fixed donation notification ([\#59](https://github.com/hsz/idea-gitignore/issues/59))

## [v0.6](https://github.com/hsz/idea-gitignore/tree/v0.6) (2014-11-12)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.5.4...v0.6)

**Implemented enhancements:**

- Reimplemented templates generator (tree structure, checkboxes allow to add many templates at once)
- Find template by its content
- "Add template..." option in context menu

**Fixed bugs:**

- Fixed "Cannot create .gitignore, file already exists." [\#55](https://github.com/hsz/idea-gitignore/issues/55)
- Fixed CommonDataKeys NoClassDefFoundError [\#51](https://github.com/hsz/idea-gitignore/issues/51)
- PHPStorm reports an error. [\#50](https://github.com/hsz/idea-gitignore/issues/50)
- Files/folders marked as ignored in IntelliJ's project are shown as 'never used' in the .gitignore file [\#47](https://github.com/hsz/idea-gitignore/issues/47)
- No search input by default [\#27](https://github.com/hsz/idea-gitignore/issues/27)

**Closed issues:**

- Add more than one template [\#54](https://github.com/hsz/idea-gitignore/issues/54)

## [v0.5.4](https://github.com/hsz/idea-gitignore/tree/v0.5.4) (2014-08-15)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.5.3...v0.5.4)

**Fixed bugs:**

- Better entries completion and references
- Unused entry inspection style changed to grayed ([\#47](https://github.com/hsz/idea-gitignore/issues/47))
- Fixed AddTemplateAction NPE ([\#48](https://github.com/hsz/idea-gitignore/issues/48))
- Select Destination dialog problem fixed in Android Studio ([\#36](https://github.com/hsz/idea-gitignore/issues/36))
- Handling excluded directories in inspection ([\#47](https://github.com/hsz/idea-gitignore/issues/47))
- Inspection warning messages clarification

## [v0.5.3](https://github.com/hsz/idea-gitignore/tree/v0.5.3) (2014-08-11)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.5.2...v0.5.3)

**Implemented enhancements:**

- Added "Add template..." action to the "Generate" context menu under <kbd>Alt</kbd> + <kbd>Insert</kbd> shortcut

**Fixed bugs:**

- update failed for AnAction with ID=Gitignore.IgnoreGroup [\#43](https://github.com/hsz/idea-gitignore/issues/43)
- NPE in mobi.hsz.idea.gitignore.util.Utils.getSuitableGitignoreFiles\(Utils.java:81\) [\#42](https://github.com/hsz/idea-gitignore/issues/42)
- Fixed wrong line separators errors  [\#41](https://github.com/hsz/idea-gitignore/issues/41)
- NoSuchMethodError: FileReferenceHelper.getPsiFileSystemItem [\#39](https://github.com/hsz/idea-gitignore/issues/39)

**Merged pull requests:**

- Travis integration [\#44](https://github.com/hsz/idea-gitignore/pull/44) ([hsz](https://github.com/hsz))

## [v0.5.2](https://github.com/hsz/idea-gitignore/tree/v0.5.2) (2014-07-28)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.5.1...v0.5.2)

**Fixed bugs:**

- Fixed "Show ignored files" action ([\#38](https://github.com/hsz/idea-gitignore/issues/38))
- Fixed few errors ([\#35](https://github.com/hsz/idea-gitignore/issues/35), [\#36](https://github.com/hsz/idea-gitignore/issues/36), [\#37](https://github.com/hsz/idea-gitignore/issues/37))

## [v0.5.1](https://github.com/hsz/idea-gitignore/tree/v0.5.1) (2014-07-27)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.5...v0.5.1)

**Fixed bugs:**

- NoSuchMethod Error  [\#33](https://github.com/hsz/idea-gitignore/issues/33)
- NPE in Utils ([\#30](https://github.com/hsz/idea-gitignore/issues/30), [\#32](https://github.com/hsz/idea-gitignore/issues/32), [\#34](https://github.com/hsz/idea-gitignore/issues/34))

**Closed issues:**

- Files/directories marked as never used [\#26](https://github.com/hsz/idea-gitignore/issues/26)

**Merged pull requests:**

- Support build.xml on Windows [\#31](https://github.com/hsz/idea-gitignore/pull/31) ([bedla](https://github.com/bedla))
- Negation resolving fix [\#29](https://github.com/hsz/idea-gitignore/pull/29) ([zolotov](https://github.com/zolotov))
- Resolving fix [\#28](https://github.com/hsz/idea-gitignore/pull/28) ([zolotov](https://github.com/zolotov))

## [v0.5](https://github.com/hsz/idea-gitignore/tree/v0.5) (2014-07-25)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.4...v0.5)

*Many thanks to [@zolotov](https://github.com/zolotov) for his great support.*

**Implemented enhancements:**

- Comments support with <kbd>Ctrl</kbd> + <kbd>/</kbd> shortcut
- Brackets support
- Duplicate entry inspection
- Cover entry inspection (checks if entry includes another one)
- Unused entry inspection
- Remove entry quick fix ([@zolotov](https://github.com/zolotov))
- Entry reference navigation with <kbd>Ctrl</kbd> + <kbd>click</kbd> ([@zolotov](https://github.com/zolotov))
- Rename entry refactoring ([@zolotov](https://github.com/zolotov))
- Color schemes update (introduced bracket, value, slash; removed file, directory)
- Directory line marker
- Multi-gitignore files support for adding from context menu
- Prevent adding duplicate entries ([\#17](https://github.com/hsz/idea-gitignore/issues/17))
- Respect cursor end-of-line setting [\#12](https://github.com/hsz/idea-gitignore/issues/12)
- Directories classified as files [\#8](https://github.com/hsz/idea-gitignore/issues/8)
- *and many more...*

**Merged pull requests:**

- Cleanup inspections [\#25](https://github.com/hsz/idea-gitignore/pull/25) ([zolotov](https://github.com/zolotov))
- Cleanup [\#24](https://github.com/hsz/idea-gitignore/pull/24) ([zolotov](https://github.com/zolotov))
- Add extra info to README [\#23](https://github.com/hsz/idea-gitignore/pull/23) ([zolotov](https://github.com/zolotov))
- Compile pattern before checking [\#22](https://github.com/hsz/idea-gitignore/pull/22) ([zolotov](https://github.com/zolotov))
- Reimplemented generator dialog [\#21](https://github.com/hsz/idea-gitignore/pull/21) ([zolotov](https://github.com/zolotov))
- Completion/resolving/rename refactoring [\#20](https://github.com/hsz/idea-gitignore/pull/20) ([zolotov](https://github.com/zolotov))
- Glob parser, Cover entry inspection [\#19](https://github.com/hsz/idea-gitignore/pull/19) ([hsz](https://github.com/hsz))

## [v0.4](https://github.com/hsz/idea-gitignore/tree/v0.4) (2014-07-08)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.3.3...v0.4)

**Fixed bugs:**

- Fixed NoSuchMethodError if older SDK does not support notification panel's icon [\#14](https://github.com/hsz/idea-gitignore/issues/14)
- Fixed problem with template's wrong line separator [\#13](https://github.com/hsz/idea-gitignore/issues/13)
- Fixed problem with negation (AssertionError) ([\#10](https://github.com/hsz/idea-gitignore/issues/10))

**Closed issues:**

- Negation syntax style changed [\#15](https://github.com/hsz/idea-gitignore/issues/15)

**Implemented enhancements:**

- Show ignored files by specified Gitignore file (right click on `.gitignore` file) [\#16](https://github.com/hsz/idea-gitignore/pull/16)
- Add selected file/directory to Gitignore rules from popup menu
- Rewritten BNF and JFlex rule

## [v0.3.3](https://github.com/hsz/idea-gitignore/tree/v0.3.3) (2014-07-03)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.3.1...v0.3.3)

**Fixed bugs:**

- Fixed problem with negation (AssertionError) [\#10](https://github.com/hsz/idea-gitignore/issues/10)
- Fixed problem with Java 1.6 and JList - NoSuchMethodError [\#7](https://github.com/hsz/idea-gitignore/issues/7)
- New file entry moved to the end of list

**Closed issues:**

- Replaced icon [\#9](https://github.com/hsz/idea-gitignore/issues/9)

**Merged pull requests:**

- Missing gitignore notification [\#11](https://github.com/hsz/idea-gitignore/pull/11) ([hsz](https://github.com/hsz))
- Files/directories completion in editor [\#6](https://github.com/hsz/idea-gitignore/pull/6) ([hsz](https://github.com/hsz))

## [v0.3.1](https://github.com/hsz/idea-gitignore/tree/v0.3.1) (2014-06-26)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.3...v0.3.1)

**Fixed bugs:**

- Fixed problem with Java 1.6 ([\#2](https://github.com/hsz/idea-gitignore/issues/2), [\#4](https://github.com/hsz/idea-gitignore/issues/4))
- NullPointerException after creating .gitignore file [\#5](https://github.com/hsz/idea-gitignore/issues/5)

## [v0.3](https://github.com/hsz/idea-gitignore/tree/v0.3) (2014-06-25)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v0.2.2...v0.3)

- Content generator based on [GitHub's templates collection](https://github.com/github/gitignore)

## [v0.2.2](https://github.com/hsz/idea-gitignore/tree/v0.2.2) (2014-06-24)

- Custom color schemes for *Default* and *Darcula* themes

## v0.2.1

- Syntax highlight fix
- Color Settings Page sample (<kbd>Settings</kbd> > <kbd>Editor</kbd> > <kbd>Color & Fonts</kbd> > <kbd>Gitignore</kbd>)

## v0.2

- Syntax highlight (comment, section, header, file, directory, negation)

## v0.1

- Initial version
- `.gitignore` file support
- Basic syntax highlighting


\* *This Change Log was automatically generated by [github_changelog_generator](https://github.com/skywinder/Github-Changelog-Generator)*

License
-------

Copyright (c) 2017 hsz Jakub Chrzanowski. See the [LICENSE](./LICENSE) file for license rights and limitations (MIT).
