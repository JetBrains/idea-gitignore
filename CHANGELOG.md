Change Log
----------

### [v3.2.2](https://github.com/JetBrains/idea-gitignore/tree/v3.2.2) (2019-09-02)

[Full Changelog](https://github.com/JetBrains/idea-gitignore/compare/v3.2.0...v3.2.2)

- Fix dependency on Mercurial Integration [\#592](https://github.com/hsz/idea-gitignore/issues/592) 
- Fix index compatibility issue [\#593](https://github.com/hsz/idea-gitignore/issues/593) 

### [v3.2.0](https://github.com/JetBrains/idea-gitignore/tree/v3.2.0) (2019-08-01)

[Full Changelog](https://github.com/JetBrains/idea-gitignore/compare/v3.1.2...v3.2.0)

**Compatibility with 2019.2**

IntelliJ release 2019.2 brings out of the box support for .gitignore and .hgignore files, so some functionality has moved from the plugin to the platform. 
The following features are now provided by the platform:
- Ignored status highlighting for .gitignore and .hgingore
- Completion in .gitignore and .hgignore
- Add to ignore actions

The plugin still provides other features for .gitignore and .hgignore, and the full set of features for other supported ignore files.

### [v3.1.2](https://github.com/hsz/idea-gitignore/tree/v3.1.2) (2019-05-14)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v3.0.0...v3.1.2)

**Implemented enhancements:**

- .gcloudignore (Google Cloud) support [\#577](https://github.com/hsz/idea-gitignore/issues/577)
- .nuxtignore (Nuxt.js) support [\#576](https://github.com/hsz/idea-gitignore/issues/576)
- Fixed deprecation warnings [\#567](https://github.com/hsz/idea-gitignore/issues/567)
- Removed "tracked & ignored" feature [\#547](https://github.com/hsz/idea-gitignore/issues/547) [\#523](https://github.com/hsz/idea-gitignore/issues/523) [\#528](https://github.com/hsz/idea-gitignore/issues/528) [\#486](https://github.com/hsz/idea-gitignore/issues/486)
- Display external .git/info/exclude files in "Outer ignore rules" side editor
- Handle ~/.config/git/ignore ignore file [\#574](https://github.com/hsz/idea-gitignore/issues/574)

**Fixed bugs:**

- Adding 2+ templates without new line in between [\#561](https://github.com/hsz/idea-gitignore/issues/561)
- Fixed incorrect relative path resolving for ignored files [\#566](https://github.com/hsz/idea-gitignore/issues/566)
- Fixed "is covered by" false-positive behaviour [\#565](https://github.com/hsz/idea-gitignore/issues/565) [\#228](https://github.com/hsz/idea-gitignore/issues/228)
- Fix for IllegalStateException when ProjectUtil.guessProjectDir called on default project
- Fixed incorrect double star pattern behaviour [\#579](https://github.com/hsz/idea-gitignore/issues/579)
- Update downgrades on 2018.2+ [\#572](https://github.com/hsz/idea-gitignore/issues/572)
- Group with id "GenerateGroup" isn't registered [\#573](https://github.com/hsz/idea-gitignore/issues/573)
- Directories marked as excluded are reported as never used [\#571](https://github.com/hsz/idea-gitignore/issues/571)
- All files are marked as ignored [\#581](https://github.com/hsz/idea-gitignore/issues/581)

### [v3.0.0](https://github.com/hsz/idea-gitignore/tree/v3.0.0) (2018-07-24)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v2.6.2...v3.0.0)

**Implemented enhancements:**

- Separated builds for different IDE versions (139-181+)
- Abandoned support for <139
- Integration with IntelliJ Plugin Verifier

**Fixed bugs:**

- Argument for @NotNull parameter 'settings' of HideIgnoredFilesTreeStructureProvider.modify must not be null [\#551](https://github.com/hsz/idea-gitignore/issues/551)
- Projects leak in ExternalIndexableSetContributor.CACHE [\#553](https://github.com/hsz/idea-gitignore/issues/553)
- Unable to save settings [\#552](https://github.com/hsz/idea-gitignore/issues/552)


### [v2.6.2](https://github.com/hsz/idea-gitignore/tree/v2.6.2) (2018-05-22)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v2.6.1...v2.6.2)

**Fixed bugs:**

- Entries using wildcard never considered as used [\#543](https://github.com/hsz/idea-gitignore/issues/543)
- IndexId ClassNotFoundException [\#534](https://github.com/hsz/idea-gitignore/issues/534)
- "Already disposed: Project" in UntrackFilesDialog [\#539](https://github.com/hsz/idea-gitignore/issues/539)
- Drop project cache from ExternalIndexableSetContributor - thanks to @denofevil [\#545](https://github.com/hsz/idea-gitignore/pull/545)


### [v2.6.1](https://github.com/hsz/idea-gitignore/tree/v2.6.1) (2018-04-13)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v2.5.0...v2.6.1)

**Implemented enhancements:**

- Multirow tabs for outer rules panel [\#501](https://github.com/hsz/idea-gitignore/issues/501)

**Fixed bugs:**

- The following plugins are incompatible with the current IDE build [\#530](https://github.com/hsz/idea-gitignore/issues/530)
- NullPointerException on File indexing (IgnoreFilesIndex), infinite loop of failing re-indexes - thanks to @nicity ! [\#527](https://github.com/hsz/idea-gitignore/issues/527)

**Closed issues:**

- Have you considered Open Collective? [opencollective.com/ignore](https://opencollective.com/ignore) [\#497](https://github.com/hsz/idea-gitignore/issues/497)


### [v2.5.0](https://github.com/hsz/idea-gitignore/tree/v2.5.0) (2018-03-24)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v2.4.0...v2.5.0)

**Implemented enhancements:**

- `.ebignore` (ElasticBeanstalk) support [\#520](https://github.com/hsz/idea-gitignore/issues/520)

**Fixed bugs:**

- The plugin slows down the IDE a lot [\#525](https://github.com/hsz/idea-gitignore/issues/525)
- MatcherUtil.cache takes too much memory [\#521](https://github.com/hsz/idea-gitignore/issues/521)
- KotlinNullPointerException in Rider IDE [\#385](https://github.com/hsz/idea-gitignore/issues/385) [\#522](https://github.com/hsz/idea-gitignore/issues/522)
- Already disposed in VirtualFilePointerImpl (Utils.getExcludedRoots) [\#524](https://github.com/hsz/idea-gitignore/issues/524)
- AssertionError in VirtualFilePointerContainerImpl [\#503](https://github.com/hsz/idea-gitignore/issues/503)


### [v2.4.0](https://github.com/hsz/idea-gitignore/tree/v2.4.0) (2018-01-11)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v2.3.2...v2.4.0)

**Implemented enhancements:**

- Better access to Notifications settings in "Appearance & Behaviour > Notifications" [\#506](https://github.com/hsz/idea-gitignore/issues/506)
- "Add to ignore file" action in "Version Control > Unversioned Files" view [\#509](https://github.com/hsz/idea-gitignore/issues/509)

**Fixed bugs:**

- Opening multiple projects in a new window makes IDEA plug-in unresponsive [\#510](https://github.com/hsz/idea-gitignore/issues/510)


### [v2.3.2](https://github.com/hsz/idea-gitignore/tree/v2.3.2) (2017-11-17)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v2.3.0...v2.3.2)

**Fixed bugs:**

- IDE Fatal Error: Accessing 'IgnoreFilesIndex' during processing 'FilenameIndex' [\#480](https://github.com/hsz/idea-gitignore/issues/480)
- ConcurrentModificationException in IgnoreSettings.notifyOnChange [\#480](https://github.com/hsz/idea-gitignore/issues/480)
- Missing/Wrong Key IGNORE.UNUSED_ENTRY in colour scheme [\#494](https://github.com/hsz/idea-gitignore/issues/494)
- It's prohibited to access index during event dispatching [\#493](https://github.com/hsz/idea-gitignore/issues/493)


## [v2.3.0](https://github.com/hsz/idea-gitignore/tree/v2.3.0) (2017-11-02)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v2.2.2...v2.3.0)

**Fixed bugs:**

- Performance optimizations [\#490](https://github.com/hsz/idea-gitignore/issues/490)
- Hang related to IgnoreCoverEntryInspection [\#489](https://github.com/hsz/idea-gitignore/issues/489)
- Proper handling of outer files (global gitignore, .git/info/exclude) [\#476](https://github.com/hsz/idea-gitignore/issues/476) [\#453](https://github.com/hsz/idea-gitignore/issues/453)
- Ignored & tracked dialog shows empty files list [\#486](https://github.com/hsz/idea-gitignore/issues/486)
- Expected only one value per-inputId for IgnoreFilesIndex [\#484](https://github.com/hsz/idea-gitignore/issues/484)
- Plugin is incompatible with the new IDE build [\#491](https://github.com/hsz/idea-gitignore/issues/491)
- NPE in MatcherUtil.match [\#485](https://github.com/hsz/idea-gitignore/issues/485)


## [v2.2.2](https://github.com/hsz/idea-gitignore/tree/v2.2.2) (2017-10-11)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v2.2.1...v2.2.2)

**Implemented enhancements:**

- Exclude .ignore langages from the "New Scratch" action [\#448](https://github.com/hsz/idea-gitignore/issues/448)
- Better discovery of ignored & tracked files [\#455](https://github.com/hsz/idea-gitignore/issues/455)

**Fixed bugs:**

- Mark UP and Prettier as not a VCS languages [\#483](https://github.com/hsz/idea-gitignore/issues/483)
- 'directory' for Utils.isUnder must not be null in IgnoreManager [\#482](https://github.com/hsz/idea-gitignore/issues/482) [\#477](https://github.com/hsz/idea-gitignore/issues/477)


## [v2.2.1](https://github.com/hsz/idea-gitignore/tree/v2.2.1) (2017-09-14)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v2.2.0...v2.2.1)

**Fixed bugs:**

- Accessing 'IgnoreFilesIndex' during processing [\#473](https://github.com/hsz/idea-gitignore/issues/473)
- Write-unsafe context [\#471](https://github.com/hsz/idea-gitignore/issues/471)
- Error on opening .gitignore file [\#470](https://github.com/hsz/idea-gitignore/issues/470)
- File colouring not working [\#462](https://github.com/hsz/idea-gitignore/issues/462)


## [v2.2.0](https://github.com/hsz/idea-gitignore/tree/v2.2.0) (2017-09-06)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v2.1.1...v2.2.1)

**Implemented enhancements:**

- **Migration to the native IDE indexing**
- EAP repository channel
- Prettier (.prettierignore) support [\#466](https://github.com/hsz/idea-gitignore/issues/466)

**Fixed bugs:**

- File colouring not working [\#462](https://github.com/hsz/idea-gitignore/issues/462)
- ~/.gitignore_global is not handled [\#453](https://github.com/hsz/idea-gitignore/issues/453)
- NPE in MatcherUtil.match [\#457](https://github.com/hsz/idea-gitignore/issues/457)
- 'Outer ignore rules' shows previous project's exclude files [\#460](https://github.com/hsz/idea-gitignore/issues/460)
- ExpiringMap causes ArrayIndexOutOfBoundsException [\#461](https://github.com/hsz/idea-gitignore/issues/461)
- FileTypeManager.associate run in write-unsafe context [\#471](https://github.com/hsz/idea-gitignore/issues/471)


## [v2.1.1](https://github.com/hsz/idea-gitignore/tree/v2.1.1) (2017-08-23)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v2.0.4...v2.1.1)

**Implemented enhancements:**

- **Migration to the native IDE indexing**
- EAP repository channel
- Up (.upignore) support [\#445](https://github.com/hsz/idea-gitignore/issues/445)

**Fixed bugs:**

- Performance improvements [\#415](https://github.com/hsz/idea-gitignore/issues/415)
- ConcurrentModificationException in THashIterator [\#413](https://github.com/hsz/idea-gitignore/issues/413)
- Read access is allowed from event dispatch thread or inside read-action only [\#419](https://github.com/hsz/idea-gitignore/issues/419)
- UnsupportedOperationException in IgnoreEditorManagerListener [\#399](https://github.com/hsz/idea-gitignore/issues/399)
- Missing StyleLint parserDefinition [\#394](https://github.com/hsz/idea-gitignore/issues/394)
- NoSuchMethodError VcsRepositoryManager.getInstance [\#403](https://github.com/hsz/idea-gitignore/issues/403)
- NoClassDefFoundError VcsRepositoryManager [\#406](https://github.com/hsz/idea-gitignore/issues/406)
- "Outer" ignore rules include extraneous files [\#401](https://github.com/hsz/idea-gitignore/issues/401)


## [v2.0.4](https://github.com/hsz/idea-gitignore/tree/v2.0.4) (2017-07-31)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.7.6...v2.0.4)

**Implemented enhancements:**

- **Migration to the native IDE indexing**
- Git submodules `info/exclude` files support [\#287](https://github.com/hsz/idea-gitignore/issues/287)
- Swagger Codegen (.swagger-codegen-ignore) support (thanks to @jimschubert)
- Kubernetes Helm (.helmignore) support (thanks to @sergei-ivanov)
- File type for .dockerignore should not be named 'Docker' [\#375](https://github.com/hsz/idea-gitignore/issues/375)

**Fixed bugs:**

- *Files are not marked at startup* [\#379](https://github.com/hsz/idea-gitignore/issues/379)
- *Handle multiple submodules in project (reset parent rules)* [\#383](https://github.com/hsz/idea-gitignore/issues/383)
- Tracking of ignored files showing false positives [\#310](https://github.com/hsz/idea-gitignore/issues/310)
- Correct handle of leading slash on directory name [\#340](https://github.com/hsz/idea-gitignore/issues/340)
- Fixed handling excluding (!) entries [\#350](https://github.com/hsz/idea-gitignore/issues/350) [\#344](https://github.com/hsz/idea-gitignore/issues/344) [\#361](https://github.com/hsz/idea-gitignore/issues/361) [\#364](https://github.com/hsz/idea-gitignore/issues/364)
- Fixed nested ignoring [\#346](https://github.com/hsz/idea-gitignore/issues/346)
- Fixed "You are editing..." message display conditions [\#351](https://github.com/hsz/idea-gitignore/issues/351) [\#352](https://github.com/hsz/idea-gitignore/issues/352) [\#356](https://github.com/hsz/idea-gitignore/issues/356)
- It's prohibited to access index during event dispatching [\#358](https://github.com/hsz/idea-gitignore/issues/358) [\#355](https://github.com/hsz/idea-gitignore/issues/355) [\#369](https://github.com/hsz/idea-gitignore/issues/369)
- Already disposed: com.intellij.util.messages.impl.MessageBusImpl [\#360](https://github.com/hsz/idea-gitignore/issues/360)
- NotNull error when changing directory name [\#391](https://github.com/hsz/idea-gitignore/issues/391)
- Fixed missing ESLint parserDefinition [\#394](https://github.com/hsz/idea-gitignore/issues/394)
- Wrap `git rm` command with quotes [\#339](https://github.com/hsz/idea-gitignore/issues/339)
- Argument for @NotNull parameter 'fragment' must not be null [\#345](https://github.com/hsz/idea-gitignore/issues/345)


## [v1.7.6](https://github.com/hsz/idea-gitignore/tree/v1.7.6) (2017-02-23)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.7.5...v1.7.6)

**Implemented enhancements:**

- Preventing or Notification for editing ignored files [\#319](https://github.com/hsz/idea-gitignore/issues/319)
- Present untrack git commands to the user in UntrackFilesDialog before execution

**Fixed bugs:**

- *NullPointerException on PhpStorm startup* [\#315](https://github.com/hsz/idea-gitignore/issues/315)
- NoSuchMethodError: ContainerUtil.createConcurrentList() [\#320](https://github.com/hsz/idea-gitignore/issues/320)


## [v1.7.5](https://github.com/hsz/idea-gitignore/tree/v1.7.5) (2017-02-14)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v1.6...v1.7.5)

**Implemented enhancements:**

- *Hide ignored files and directories in the project tree view*
- *Indicate that parent contains extra elements if children are hidden*
- Dialog box that allows to untrack ignored files (performs git rm --cached command)
- Untrack files dialog invoked automatically + from Project view context menu
- Stylint (.stylintignore) support [\#279](https://github.com/hsz/idea-gitignore/issues/279)
- Project Tree View coloring refactoring (performance)

**Fixed bugs:**

- *Fixed colors for tracked and ignored files, additional info label is implemented* [\#296](https://github.com/hsz/idea-gitignore/issues/296) [\#295](https://github.com/hsz/idea-gitignore/issues/295) [\#284](https://github.com/hsz/idea-gitignore/issues/284)
- *IllegalArgumentException on IDEA startup* [\#302](https://github.com/hsz/idea-gitignore/issues/302)
- File of UntrackFilesDialog.createDirectoryNodes must not be null [\#307](https://github.com/hsz/idea-gitignore/issues/307) [\#309](https://github.com/hsz/idea-gitignore/issues/309)
- NoSuchFieldError: GRAYED_SMALL_ATTRIBUTES [\#305](https://github.com/hsz/idea-gitignore/issues/305)
- Ignored entries coloring [\#304](https://github.com/hsz/idea-gitignore/issues/304) [\#301](https://github.com/hsz/idea-gitignore/issues/301)
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



License
-------

Copyright (c) 2018 hsz Jakub Chrzanowski. See the [LICENSE](./LICENSE) file for license rights and limitations (MIT).
