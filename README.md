.ignore 3.1.0
=============

[![Gitter][badge-gitter-img]][badge-gitter] [![Build Status][badge-travis-img]][badge-travis]

[![Donate][badge-paypal-img]][badge-paypal]
[![Backers on Open Collective](https://opencollective.com/ignore/backers/badge.svg)](#backers)
[![Sponsors on Open Collective](https://opencollective.com/ignore/sponsors/badge.svg)](#sponsors)

[![Version](http://phpstorm.espend.de/badge/7495/version)][plugin-website]
[![Downloads](http://phpstorm.espend.de/badge/7495/downloads)][plugin-website]
[![Downloads last month](http://phpstorm.espend.de/badge/7495/last-month)][plugin-website]

Introduction
------------

**.ignore** is a plugin for:
 
- `.gitignore` (Git)
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
- `.upignore` (Up)
- `.prettierignore` (Prettier)
- `.ebignore` (ElasticBeanstalk)
- `.gcloudignore` (Google Cloud)

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


Supported IDEs
--------------

Since `v3.1.0`, .ignore plugin updates will be delivered to the IDE in version of `171+` - so all builds from 2017+.

But hey, no worries! It means .ignore for all IDE version from before - `139-163` - will be freezed at `v3.0.0`.


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

### [v3.1.0](https://github.com/hsz/idea-gitignore/tree/v3.1.0) (2019-05-08)

[Full Changelog](https://github.com/hsz/idea-gitignore/compare/v3.0.0...v3.1.0)

**Implemented enhancements:**

- .gcloudignore (Google Cloud) support [\#577](https://github.com/hsz/idea-gitignore/issues/577)
- .nuxtignore (Nuxt.js) support [\#576](https://github.com/hsz/idea-gitignore/issues/576)
- Fixed deprecation warnings [\#567](https://github.com/hsz/idea-gitignore/issues/567)
- Removed "tracked & ignored" feature [\#547](https://github.com/hsz/idea-gitignore/issues/547) [\#523](https://github.com/hsz/idea-gitignore/issues/523) [\#528](https://github.com/hsz/idea-gitignore/issues/528) [\#486](https://github.com/hsz/idea-gitignore/issues/486)

**Fixed bugs:**

- Adding 2+ templates without new line in between [\#561](https://github.com/hsz/idea-gitignore/issues/561)
- Fixed incorrect relative path resolving for ignored files [\#566](https://github.com/hsz/idea-gitignore/issues/566)
- Fixed "is covered by" false-positive behaviour [\#565](https://github.com/hsz/idea-gitignore/issues/565) [\#228](https://github.com/hsz/idea-gitignore/issues/228)


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
- [**@nicity** Maksim Mosienko](https://github.com/nicity)
- [**@76200** Bartłomiej Czyż](https://github.com/76200)
- [**@bedla** Ivo Šmíd](https://github.com/bedla)
- [**@danpfe**](https://github.com/danpfe)
- [**@maximilianonajle** Maximiliano Najle](https://github.com/maximilianonajle)
- [**@jimschubert** Jim Schubert](https://github.com/jimschubert)
- [**@sergei-ivanov** Sergei Ivanov](https://github.com/sergei-ivanov)


Backers
-------

Support us with a monthly donation and help us continue our activities.

[![Backers](https://opencollective.com/ignore/backers.svg)](https://opencollective.com/ignore#support)


Sponsors
--------

Become a sponsor to get your logo on our README on Github.

[![Sponsors](https://opencollective.com/ignore/sponsors.svg)](https://opencollective.com/ignore#support)


Tools
-----

I'm using Yourkit to locate and fix performance issues of .ignore. YourKit, LLC kindly provided a
free open-source license of the [YourKit Java Profiler](https://www.yourkit.com/java/profiler/).

![YourKit Java Profiler Logo](https://www.yourkit.com/images/yklogo.png "YourKit Java Profiler Logo")


License
-------

Copyright (c) 2018 hsz Jakub Chrzanowski. See the [LICENSE](./LICENSE) file for license rights and limitations (MIT).

    
[github-gitignore]:       https://github.com/github/gitignore
[gradle-intellij-plugin]: https://github.com/JetBrains/gradle-intellij-plugin
[hsz]:                    http://hsz.mobi
[website]:                http://ignore.hsz.mobi
[plugin-website]:         https://plugins.jetbrains.com/plugin/7495
[latest-release]:         https://github.com/hsz/idea-gitignore/releases/latest


[badge-gitter-img]:       https://badges.gitter.im/hsz/idea-gitignore.svg
[badge-gitter]:           https://gitter.im/hsz/idea-gitignore
[badge-travis-img]:       https://travis-ci.org/hsz/idea-gitignore.svg?branch=master
[badge-travis]:           https://travis-ci.org/hsz/idea-gitignore
[badge-coveralls-img]:    https://coveralls.io/repos/github/hsz/idea-gitignore/badge.svg?branch=master
[badge-coveralls]:        https://coveralls.io/github/hsz/idea-gitignore?branch=master
[badge-paypal-img]:       https://img.shields.io/badge/donate-paypal-yellow.svg
[badge-paypal]:           https://www.paypal.me/hsz
