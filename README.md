.gitignore support
==================


Introduction
------------

**.gitignore support** is a plugin for `.gitignore` files in your Git project. It supports following JetBrains IDEs:

- Android Studio
- AppCode
- IntelliJ IDEA
- PhpStorm
- PyCharm
- RubyMine
- WebStorm
- 0xDBE

*Compiled with Java 1.6*


Features
--------

- `.gitignore` files syntax highlight
- Show ignored files by specified Gitignore file (right click on `.gitignore` file)
- Create `.gitignore` file in currently selected directory
- Generate Gitignore rules basing on [GitHub's templates collection][github-gitignore]
- Add selected file/directory to Gitignore rules from popup menu
- Suggesting `.gitignore` file creation for new project

*Feature requests:*

- *Better Gitignore templates filtering and selecting in rules generator*
- *Gitignore rules cleanup (duplicates removing, ...)*


Installation
------------

- Using IDE built-in plugin system:
  - <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for ".gitignore support"</kbd> > <kbd>Install Plugin</kbd>
- Manually:
  - Download the [latest release][latest-release] and install it manually using <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd>
  
Restart IDE.


Usage
-----

1. Generate new file

To generate new `.gitignore` file, just click on <kbd>File</kbd> > <kbd>New</kbd> or use <kbd>Alt</kbd> + <kbd>Insert</kbd> shortcut and select `.gitignore file` element.

![Generate new file](http://gitignore.hsz.mobi/usage-01.gif)

Changelog
---------

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
- In *Ant Build* add [`build.xml`][build-xml] file and mark **prepare** task as <kbd>Execute on</kbd> > <kbd>Before compilation</kbd>

Developed By
------------

[**@hsz** Jakub Chrzanowski][hsz]


**Contributors**

[**@zolotov** Alexander Zolotov](https://github.com/zolotov)
[**@bedla** Ivo Šmíd](https://github.com/bedla)


License
-------

Copyright (c) 2014 hsz Jakub Chrzanowski. See the [LICENSE](./LICENSE) file for license rights and limitations (MIT).

    
[github-gitignore]:       https://github.com/github/gitignore
[idea-sdk-configuration]: http://confluence.jetbrains.com/display/IntelliJIDEA/Prerequisites
[grammar-kit-plugin]:     http://plugins.jetbrains.com/plugin/6606
[psiviewer-plugin]:       http://plugins.jetbrains.com/plugin/227
[jflex-support-plugin]:   http://plugins.jetbrains.com/plugin/263
[bnf-file]:               ./resources/bnf/Gitignore.bnf
[flex-file]:              ./src/mobi/hsz/idea/gitignore/lexer/Gitignore.flex
[build-xml]:              ./build.xml
[hsz]:                    http://hsz.mobi
[latest-release]:         https://github.com/hsz/idea-gitignore/releases/latest