# PYZ

[![Version](https://img.shields.io/jetbrains/plugin/v/18215-pyz.svg)](https://plugins.jetbrains.com/plugin/18215-pyz)

<!-- Plugin description -->
A collection of little helpers that improve your daily work with Spryker.

## Features
- Extend Spryker core classes, interfaces and xml files on project level (right-click -> Extend in PYZ / CTRL-ALT-E)
- Extend Spryker core class methods on project level (right-click on method name inside Spryker core class -> Extend in PYZ / CTRL-ALT-E)
- Open Spryker files on github.com (right-click -> View on GitHub / CTRL-ALT-G)
- Goto-handling for Zed stub calls and their gateway controller actions (URLs are clickable)  
- Resolve usages of gateway controller actions in Zed stub calls
- Navigate from OMS XML files to included sub-processes and the definitions of commands/conditions 
- Navigate from Twig files to included, embedded and extended files and widgets
- Navigate from transfer object classes and instantiations to XML definitions (and vice versa)
- Navigate from codeception.yml to helper classes

## How to use
- To extend files on project level, use the context menu item "Extend in PYZ". It will appear for files located in one of the Spryker vendor directories only.
- "View on GitHub" will try to parse your composer.lock file and open your currently installed version in the browser.
- Files can be extended in batches, just select multiple files at once.
- You can configure the PYZ directory and the base-namespace in Settings->Tools->PYZ Plugin.
- In OMS XML files you can click command and condition names (e.g. "Oms/SendOrderConfirmation") to get to the definition in either OmsDependencyProvider or any OmsDependencyInjector. You can also click the name of a subprocess to open the file (e.g. "DummySubprocess/DummyRefund01.xml").
- In Twig files you can click the names of included files (e.g. molecules, atoms, widgets etc...) to jump directly to the file. For widgets it will try to find a twig file as well as the php file for you to choose from.
- For transfer objects PYZ tries to resolve definitions and extensions in *.transfer.xml files. To see a list of definitions and extensions:
  - Click a transfer object's class name (e.g. in "class ItemTransfer" or in "new ItemTransfer()") or
  - Click a transfer object's name in any *.transfer.xml file.
- All features can be switched on and off in Settings->Tools->PYZ Plugin
<!-- Plugin description end -->

## About
This plugin was created during the _Turbine Kreuzberg Breakout Week_ as a learning project by a bunch of PHP developers. It is far from perfect. You are welcome to contribute! Feel free to create a pull request, or leave an issue if something does not work.

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
