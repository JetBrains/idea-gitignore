.ignore v2.0.4
===================

[ignore.hsz.mobi][website]

[![Gitter][badge-gitter-img]][badge-gitter] [![Build Status][badge-travis-img]][badge-travis] [![Donate][badge-paypal-img]][badge-paypal] [![Donate][badge-bitcoin-img]][badge-bitcoin]

[![Version](http://phpstorm.espend.de/badge/7495/version)](https://plugins.jetbrains.com/plugin/7495)
[![Downloads](http://phpstorm.espend.de/badge/7495/downloads)](https://plugins.jetbrains.com/plugin/7495)
[![Downloads last month](http://phpstorm.espend.de/badge/7495/last-month)](https://plugins.jetbrains.com/plugin/7495)

Introduction
------------

**.ignore** is a plugin for:
 
- `.gitignore` (GIT)
- `.hgignore` (Mercurial)
- `.npmignore` (NPM)
- `.dockerignore` (Docker)
- `.chefignore` (Chef)
- `.cvsignore` (CVS)
- `.bzrignore` (Bazaar)
- `.boringignore` (Darcs)
- `.mtn-ignore` (Monotone)
- `ignore-glob` (Fossil)
- `.jshintignore` (JSHint)
- `.tfignore` (Team Foundation)
- `.p4ignore` (Perforce)
- `.flooignore` (Floobits)
- `.eslintignore` (ESLint)
- `.cfignore` (Cloud Foundry)
- `.jpmignore` (Jetpack)
- `.stylelintignore` (StyleLint)
- `.stylintignore` (Stylint)
- `.swagger-codegen-ignore` (Swagger Codegen)
- `.helmignore` (Kubernetes Helm)

files in your project. It supports following JetBrains IDEs:

- Android Studio
- AppCode
- CLion
- IntelliJ IDEA
- PhpStorm
- PyCharm
- RubyMine
- WebStorm
- DataGrip

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
- Renaming entries from ignore file
- Close opened ignored files action
- Custom user templates with import/export features

Installation
------------

- Using IDE built-in plugin system:
  - <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for ".ignore"</kbd> > <kbd>Install Plugin</kbd>
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
- NotNull error when changing directory name [\##391](https://github.com/hsz/idea-gitignore/issues/#391)
- Fixed missing ESLint parserDefinition [\##394](https://github.com/hsz/idea-gitignore/issues/#394)
- Wrap `git rm` command with quotes [\##339](https://github.com/hsz/idea-gitignore/issues/#339)
- Argument for @NotNull parameter 'fragment' must not be null [\##345](https://github.com/hsz/idea-gitignore/issues/#345)

[Full Changelog History](./CHANGELOG.md)


Contribution
------------

Check [`CONTRIBUTING.md`](./CONTRIBUTING.md) file.

### Compiling the source code

Since the project has been migrated to the Gradle and [Gradle IntelliJ plugin][gradle-intellij-plugin],
the build process is much simpler. The only thing to build the plugin is to run:

    gradle build
    
All required dependencies like Grammar-Kit, JFlex are downloaded in the background and triggered properly
during the build process. You can also test the plugin easily with running:

    gradle runIdea
    
All of the gradle tasks can be connected to the IntelliJ debugger, so the development process is very easy.


Developed By
------------

[**@hsz** Jakub Chrzanowski][hsz]


**Contributors**

- [**@zolotov** Alexander Zolotov](https://github.com/zolotov)
- [**@76200** Bartłomiej Czyż](https://github.com/76200)
- [**@bedla** Ivo Šmíd](https://github.com/bedla)
- [**@danpfe**](https://github.com/danpfe)
- [**@maximilianonajle** Maximiliano Najle](https://github.com/maximilianonajle)
- [**@jimschubert** Jim Schubert](https://github.com/jimschubert)
- [**@sergei-ivanov** Sergei Ivanov](https://github.com/sergei-ivanov)


Tools
-----

I'm using Yourkit to locate and fix performance issues of BashSupport. YourKit, LLC kindly provided a
free open-source license of the [YourKit Java Profiler](https://www.yourkit.com/java/profiler/).

![YourKit Java Profiler Logo](https://www.yourkit.com/images/yklogo.png "YourKit Java Profiler Logo")


License
-------

Copyright (c) 2017 hsz Jakub Chrzanowski. See the [LICENSE](./LICENSE) file for license rights and limitations (MIT).

    
[github-gitignore]:       https://github.com/github/gitignore
[gradle-intellij-plugin]: https://github.com/JetBrains/gradle-intellij-plugin
[hsz]:                    http://hsz.mobi
[website]:                http://ignore.hsz.mobi
[latest-release]:         https://github.com/hsz/idea-gitignore/releases/latest


[badge-gitter-img]:       https://badges.gitter.im/hsz/idea-gitignore.svg
[badge-gitter]:           https://gitter.im/hsz/idea-gitignore
[badge-travis-img]:       https://travis-ci.org/hsz/idea-gitignore.svg
[badge-travis]:           https://travis-ci.org/hsz/idea-gitignore
[badge-coveralls-img]:    https://coveralls.io/repos/github/hsz/idea-gitignore/badge.svg?branch=master
[badge-coveralls]:        https://coveralls.io/github/hsz/idea-gitignore?branch=master
[badge-paypal-img]:       https://img.shields.io/badge/donate-paypal-yellow.svg
[badge-paypal]:           https://www.paypal.me/hsz
[badge-bitcoin-img]:      https://img.shields.io/badge/donate-bitcoin-yellow.svg
[badge-bitcoin]:          https://blockchain.info/address/1BUbqKrUBmGGSnMybzGCsJyAWJbh4CcwE1
