# PYZ

[![Version](https://img.shields.io/jetbrains/plugin/v/18215-pyz.svg)](https://plugins.jetbrains.com/plugin/18215-pyz)

<!-- Plugin description -->
## PYZ - a PhpStorm Plugin for Spryker Developers
- Extend Spryker core classes, interfaces and xml files on project level (right-click -> Extend in PYZ / CTRL-ALT-E)
- Open Spryker files on github.com (right-click -> View on GitHub / CTRL-ALT-G)
- Goto-handling for Zed stub calls and their gateway controller actions (URLs are clickable)  
- Resolve usages of gateway controller actions in Zed stub calls
- Navigate from OMS XML files to included sub-processes and the definitions of commands/conditions 

## About
This plugin was created during the _Turbine Kreuzberg Breakout Week_ as a learning project by a bunch of PHP developers. It is far from perfect. You are welcome to contribute! Feel free to create a pull request, or leave an issue if something does not work.  

## How to use
- To extend files on project level, use the context menu item "Extend in PYZ". It will appear for files located in one of the Spryker vendor directories only.
- "View on GitHub" will try to parse your composer.lock file and open your currently installed version in the browser.
- Files can be extended in batches, just select multiple files at once.
- You can configure the PYZ directory and the base-namespace in Settings->Tools->PYZ Plugin.
- In OMS XML files you can click command and condition names (e.g. "Oms/SendOrderConfirmation") to get to the definition in either OmsDependencyProvider or any OmsDependencyInjector. You can also click the name of a subprocess to open the file (e.g. "DummySubprocess/DummyRefund01.xml").
<!-- Plugin description end -->

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
