.ignore [![Build Status](https://travis-ci.org/hsz/idea-gitignore.svg?branch=travis)](https://travis-ci.org/hsz/idea-gitignore) [![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=SJAU4XWQ584QL) <a href="http://blockchain.info/address/1BUbqKrUBmGGSnMybzGCsJyAWJbh4CcwE1"><img src="https://www.gnu.org/software/octave/images/donate-bitcoin.png" width="100" height="21"/></a>
==================


Introduction
------------

**.ignore** is a plugin for:
 
- `.gitignore` (GIT),
- `.hgignore` (Mercurial),
- `.npmignore` (NPM),
- `.dockerignore` (Docker)
- `.chefignore` (Chef)
- `.cvsignore` (CVS)
- `.bzrignore` (Bazaar)
- `.boringignore` (Darcs)
- `.mtn-ignore` (Monotone)

files in your project. It supports following JetBrains IDEs:

- Android Studio
- AppCode
- CLion
- IntelliJ IDEA
- PhpStorm
- PyCharm
- RubyMine
- WebStorm
- 0xDBE

*Compiled with Java 1.6*


Features
--------

- Files syntax highlight
- Coloring ignored files in the Project View
- Gitignore templates filtering and selecting in rules generator by name and content
- User custom templates
- Show ignored files by specified Gitignore file (right click on `.gitignore` file)
- Create file in currently selected directory
- Generate Gitignore rules basing on [GitHub's templates collection][github-gitignore]
- Add selected file/directory to Gitignore rules from popup menu
- Suggesting `.gitignore` file creation for new project
- Entries inspection (duplicated, covered, unused, incorrect syntax, relative entries) with fix actions
- Comments and brackets support
- Navigation to entries in Project view
- Renaming entries from Gitignore file


Installation
------------

- Using IDE built-in plugin system:
  - <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for ".gitignore support"</kbd> > <kbd>Install Plugin</kbd>
- Manually:
  - Download the [latest release][latest-release] and install it manually using <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd>
  
Restart IDE.


Usage
-----

1. Generate new file and templates usage

   To generate new ignore file, just click on <kbd>File</kbd> > <kbd>New</kbd> or use <kbd>Alt</kbd> + <kbd>Insert</kbd> shortcut and select `.ignore file` element.

   ![Generate new file](http://gitignore.hsz.mobi/usage-1.gif)

2. Support for typing new rules, linking rules with matched files

   ![Support for typing new rules](http://gitignore.hsz.mobi/usage-2.gif)

3. Code inspections

   Code inspections covers few cases:

   - duplicated entries (checks if entry is defined more than once)
   - covered entries - entry is covered by more general one
   - unused entries
   - incorrect syntax (regexp rules)
   - relative entries

   ![Code inspections](http://gitignore.hsz.mobi/usage-3.gif)


Changelog
---------

Version 1.0.2

- CLion VerifyError hotfix ([#89](https://github.com/hsz/idea-gitignore/issues/89))

Version 1.0.1

- Allow enable/disable coloring ignored files ([#87](https://github.com/hsz/idea-gitignore/issues/87))
- Cover inspection respects negated entries
- Wrong ignore indicator with nested entries fix ([#85](https://github.com/hsz/idea-gitignore/issues/85))
- ConcurrentModificationException fix ([#84](https://github.com/hsz/idea-gitignore/issues/84))
- `.mtn-ignore` support (Monotone version control)

Version 1.0

- **Ignored files coloring** in the Project View (customizable style)
- **Regex support** (used with Mercurial and Darcs)
- **Syntax switching** support for Mercurial (with suggestion)
- Incorrect entry syntax inspection
- `.boringignore` support (Darcs version control)

Version 0.9

- `.chefignore` support (Chef automation tool)
- `.cvsignore` support (CVS version control)
- `.bzrignore` support (Bazaar version control)
- Grouped actions for creating new file
- Ant build file fix

Version 0.8.1

- Plugin renamed to `.ignore`
- Idea 12 support (all products since build 123) ([#71](https://github.com/hsz/idea-gitignore/issues/71))
- Inspections are back again
- Few minor fixes

Version 0.8

- Support for .hgignore .npmignore and .dockerignore files ([#57](https://github.com/hsz/idea-gitignore/issues/57), [#58](https://github.com/hsz/idea-gitignore/issues/58))
- Relative entry inspection with quick fix
- Retina-ready icons ([#63](https://github.com/hsz/idea-gitignore/issues/63), [#65](https://github.com/hsz/idea-gitignore/issues/65))
- Fixed Add template... on External Libraries ([#68](https://github.com/hsz/idea-gitignore/issues/68))
- Fixed template presentation error ([#67](https://github.com/hsz/idea-gitignore/issues/67))
- Fixed minor bugs ([#64](https://github.com/hsz/idea-gitignore/issues/64), [#49](https://github.com/hsz/idea-gitignore/issues/49))

Version 0.7

- User templates - you can create your own templates in <kbd>Settings > Gitignore > User templates</kbd>
- Fixed JDOM error ([#62](https://github.com/hsz/idea-gitignore/issues/62)) - *thanks to [@danpfe](https://github.com/danpfe)*
- Fixed donation URL

Version 0.6.1

- Global settings section
- Optional missing gitignore file check ([#18](https://github.com/hsz/idea-gitignore/issues/18))
- Fixed donation notification ([#59](https://github.com/hsz/idea-gitignore/issues/59))

Version 0.6

- Reimplemented templates generator (tree structure, checkboxes allow to add many templates at once)
- Find template by its content
- "Add template..." option in context menu
- Fixed no search input by default ([#27](https://github.com/hsz/idea-gitignore/issues/27))
- Fixed CommonDataKeys NoClassDefFoundError ([#51](https://github.com/hsz/idea-gitignore/issues/51))
- Fixed "Cannot create .gitignore, file already exists." ([#55](https://github.com/hsz/idea-gitignore/issues/55))

Version 0.5.4

- Better entries completion and references
- Unused entry inspection style changed to grayed ([#47](https://github.com/hsz/idea-gitignore/issues/47))
- Fixed AddTemplateAction NPE ([#48](https://github.com/hsz/idea-gitignore/issues/48))
- Select Destination dialog problem fixed in Android Studio ([#36](https://github.com/hsz/idea-gitignore/issues/36))
- Handling excluded directories in inspection ([#47](https://github.com/hsz/idea-gitignore/issues/47))
- Inspection warning messages clarification

Version 0.5.3

- Added "Add template..." action to the "Generate" context menu under <kbd>Alt</kbd> + <kbd>Insert</kbd> shortcut
- Travis integration
- Donation button - if you find my plugin helpful, just donate it
- Fixed wrong line separators errors ([#41](https://github.com/hsz/idea-gitignore/issues/41))

Version 0.5.2

- Fixed few errors ([#35](https://github.com/hsz/idea-gitignore/issues/35), [#36](https://github.com/hsz/idea-gitignore/issues/36), [#37](https://github.com/hsz/idea-gitignore/issues/37))
- Fixed "Show ignored files" action ([#38](https://github.com/hsz/idea-gitignore/issues/38))

Version 0.5.1

- Reference resolving fixes (me and [@zolotov](https://github.com/zolotov)) ([#26](https://github.com/hsz/idea-gitignore/issues/26), [#33](https://github.com/hsz/idea-gitignore/issues/33))
- Build script for Windows ([@bedla](https://github.com/bedla))
- NPE in Utils ([#30](https://github.com/hsz/idea-gitignore/issues/30), [#32](https://github.com/hsz/idea-gitignore/issues/32), [#34](https://github.com/hsz/idea-gitignore/issues/34))

Version 0.5

*Many thanks to [@zolotov](https://github.com/zolotov) for his great support.*

- Comments support with <kbd>Ctrl</kbd> + <kbd>/</kbd> shortcut
- Brackets support
- Generator dialog enhancement ([@zolotov](https://github.com/zolotov))
- Duplicate entry inspection
- Cover entry inspection (checks if entry includes another one)
- Unused entry inspection
- Remove entry quick fix ([@zolotov](https://github.com/zolotov))
- Entry reference navigation with <kbd>Ctrl</kbd> + <kbd>click</kbd> ([@zolotov](https://github.com/zolotov))
- Rename entry refactoring ([@zolotov](https://github.com/zolotov))
- Color schemes update (introduced bracket, value, slash; removed file, directory)
- Directory line marker
- Multi-gitignore files support for adding from context menu
- Prevent adding duplicate entries ([#17](https://github.com/hsz/idea-gitignore/issues/17))
- *and many more...*

Version 0.4
- Show ignored files by specified Gitignore file (right click on `.gitignore` file)
- Add selected file/directory to Gitignore rules from popup menu
- Negation syntax style changed (also fixes [#15](https://github.com/hsz/idea-gitignore/issues/15))
- Fixed problem with negation (AssertionError) ([#10](https://github.com/hsz/idea-gitignore/issues/10))
- Fixed problem with template's wrong line separator ([#13](https://github.com/hsz/idea-gitignore/issues/13))
- Fixed NoSuchMethodError if older SDK does not support notification panel's icon ([#14](https://github.com/hsz/idea-gitignore/issues/14))
- Rewritten BNF and JFlex rules

Version 0.3.3
- Gitignore file creating suggestion if missing
- Fixed problem with negation (AssertionError) ([#10](https://github.com/hsz/idea-gitignore/issues/10))
- Replaced icon ([#9](https://github.com/hsz/idea-gitignore/issues/9))
- New file entry moved to the end of list ([#9](https://github.com/hsz/idea-gitignore/issues/9))

Version 0.3.2
- Fixed problem with Java 1.6 and JList - NoSuchMethodError ([#7](https://github.com/hsz/idea-gitignore/issues/7))

Version 0.3.1
- Fixed problem with Java 1.6 ([#2](https://github.com/hsz/idea-gitignore/issues/2), [#4](https://github.com/hsz/idea-gitignore/issues/4))
- Fixed fetching templates list ([#5](https://github.com/hsz/idea-gitignore/issues/5))

Version 0.3
- Content generator based on [GitHub's templates collection][github-gitignore]

Version 0.2.2
- Custom color schemes for *Default* and *Darcula* themes

Version 0.2.1
- Color Settings Page sample (<kbd>Settings</kbd> > <kbd>Editor</kbd> > <kbd>Color & Fonts</kbd> > <kbd>Gitignore</kbd>)
- Syntax highlighting fix

Version 0.2
- Full syntax highlighting (comment, section, header, file, directory, negation)

Version 0.1
- Initial version
- `.gitignore` file support
- Basic syntax highlighting


Contribution
------------

Check [`CONTRIBUTING.md`](./CONTRIBUTING.md) file.

### Compiling the source code

- Clone `idea-ignore` project from https://github.com/hsz/idea-gitignore.git
- [Configure IntelliJ IDEA Plugin SDK][idea-sdk-configuration]
- Install required plugins:
  - Plugin DevKit *(bundled)*
  - [Grammar-Kit][grammar-kit-plugin]
  - [PsiViewer][psiviewer-plugin]
  - [JFlex Support][jflex-support-plugin]
- Create *New Project* as a *IntelliJ Platform Plugin* and set *Project location* to the **idea-gitignore** sources
  - In <kbd>Project settings</kbd> > <kbd>Modules</kbd> section mark:
    - `gen` as *Sources*
    - `resources` as *Resources*
    - `src` as *Sources*
    - `tests` as *Test Sources*
    - `.idea` as *Excluded*
    - `out` as *Excluded*
- Add new *Run/Debug configuration*
  - <kbd>+</kbd> <kbd>Add new configuration</kbd> > <kbd>Plugin</kbd>
  - Add `-Didea.is.internal=true` to *VM Options* (it will allow you run internal actions like `View PSI structure` action)
  - Remove `-XX:MaxPermSize=250m` from *VM Options*
- Generate PSI classes
  - Go to [`Gitignore.bnf`][bnf-file] file and **Generate Parser Code**
    - <kbd>Tools<kbd> > <kbd>Generate Parser Code</kbd> (<kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>G</kbd>)
  - Go to [`Gitignore.flex`][flex-file] file and **Run JFlex Generator**
    - <kbd>Tools</kbd> > <kbd>Run JFlex Generator</kbd> (<kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>G</kbd>)
    - For the first time it will download `JFlex.jar` and `idea-flex.skeleton` files - save them in the root project directory
- Set *Java Compiler* to **1.6**
  - Go to <kbd>Settings<kbd> > <kbd>Compiler</kbd> > <kbd>Java Compiler</kbd> and set *Project bytecode version* to **1.6**
- In *Ant Build* add [`build.xml`][build-xml] file and mark **generate-templates-list** task as <kbd>Execute on</kbd> > <kbd>Before compilation</kbd>

Developed By
------------

[**@hsz** Jakub Chrzanowski][hsz]


**Contributors**

- [**@zolotov** Alexander Zolotov](https://github.com/zolotov)
- [**@bedla** Ivo Šmíd](https://github.com/bedla)
- [**@danpfe**](https://github.com/danpfe)


License
-------

Copyright (c) 2015 hsz Jakub Chrzanowski. See the [LICENSE](./LICENSE) file for license rights and limitations (MIT).

    
[github-gitignore]:       https://github.com/github/gitignore
[idea-sdk-configuration]: http://confluence.jetbrains.com/display/IntelliJIDEA/Prerequisites
[grammar-kit-plugin]:     http://plugins.jetbrains.com/plugin/6606
[psiviewer-plugin]:       http://plugins.jetbrains.com/plugin/227
[jflex-support-plugin]:   http://plugins.jetbrains.com/plugin/263
[bnf-file]:               ./resources/bnf/Ignore.bnf
[flex-file]:              ./src/mobi/hsz/idea/gitignore/lexer/Ignore.flex
[build-xml]:              ./build.xml
[hsz]:                    http://hsz.mobi
[latest-release]:         https://github.com/hsz/idea-gitignore/releases/latest
