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
- Create `.gitignore` file in currently selected directory
- Generate Gitignore rules basing on [GitHub's templates collection][github-gitignore]

*Feature requests:*

- *Add selected file/directory to the Gitignore rules*
- *Better Gitignore templates filtering and selecting in rules generator*
- *Gitignore rules cleanup (duplicates removing, ...)*
- *Ignored files preview*
- *Mark ignored files in Project tree*
- *Suggesting `.gitignore` file creation for new project*


Installation
------------

- Using IDE built-in plugin system:
  - <kbd>Preferences > Plugins > Browse repositories... > Search for ".gitignore support" > Install Plugin</kbd>
- Manually:
  - Download the [latest release][latest-release] and install it manually using <kbd>Preferences -> Plugins -> Install plugin from disk...</kbd>
  
Restart IDE.


Usage
-----

1. Generate new file

To generate new `.gitignore` file, just click on <kbd>File > New</kbd> or use <kbd>Alt</kbd> + <kbd>Insert</kbd> shortcut and select `.gitignore file` element.

![Generate new file](http://gitignore.hsz.mobi/usage-01.gif)

Changelog
---------

Version 0.3
- Content generator based on [GitHub's templates collection][github-gitignore]

Version 0.2.2
- Custom color schemes for *Default* and *Darcula* themes

Version 0.2.1
- Color Settings Page sample (<kbd>Settings > Editor > Color & Fonts > Gitignore</kbd>)
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
  - In <kbd>Project settings > Modules</kbd> section mark:
    - `gen` as *Sources*
    - `resources` as *Sources*
    - `src` as *Sources*
    - `.idea` as *Excluded*
    - `out` as *Excluded*
- Add new *Run/Debug configuration*
  - <kbd>+</kbd> <kbd>Add new configuration > Plugin</kbd>
  - Remove `-XX:MaxPermSize=250m` from *VM Options*
- Generate PSI classes
  - Go to [`Gitignore.bnf`][bnf-file] file and **Generate Parser Code**
    - <kbd>Tools > Generate Parser Code</kbd> (<kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>G</kbd>)
  - Go to [`Gitignore.flex`][flex-file] file and **Run JFlex Generator**
    - <kbd>Tools > Run JFlex Generator</kbd> (<kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>G</kbd>)
    - For the first time it will download `JFlex.jar` and `idea-flex.skeleton` files - save them in the root project directory
- Set *Java Compiler* to **1.6**
  - Go to <kbd>Settings > Compiler > Java Compiler</kbd> and set *Project bytecode version* to **1.6**


Developed By
------------

[**hsz** Jakub Chrzanowski][hsz]


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
[hsz]:                    http://hsz.mobi
[latest-release]:         https://github.com/hsz/idea-gitignore/releases/latest