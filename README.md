.ignore 3.0.0
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


Early Access Preview repository channel
------------

If you are interested in updating your IDE with release candidate version of .ignore plugin, you can use EAP repository:

1. In the left-hand pane of the <kbd>Settings</kbd> / <kbd>Preferences dialog</kbd> (<kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>S</kbd>), click Plugins.
2. Click <kbd>Browse repositories</kbd>.
3. In the _Browse Repositories_ dialog, click <kbd>Manage repositories</kbd>.
4. In the _Custom Plugin Repositories_ dialog, click <kbd>+</kbd> and specify the repository URL: `https://plugins.jetbrains.com/plugins/eap/7495`
5. Click <kbd>Check Now</kbd> to make sure that the URL is correct. 


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
