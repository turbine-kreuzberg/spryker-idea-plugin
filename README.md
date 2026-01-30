# PYZ

[![Version](https://img.shields.io/jetbrains/plugin/v/18215-pyz.svg)](https://plugins.jetbrains.com/plugin/18215-pyz)

<!-- Plugin description -->
A collection of little helpers that improve your daily work with Spryker.

## Features
- **Extend in PYZ**: Easily extend Spryker core classes, interfaces, XML files, or individual methods on project level.
  - *Usage*: Right-click on a file (in Spryker vendor directories) or method name -> **Extend in PYZ**, or use `CTRL+ALT+E`. Supports batch extension for multiple files.
- **View on GitHub**: Open the currently installed version of a Spryker file on github.com.
  - *Usage*: Right-click -> **View on GitHub**, or use `CTRL+ALT+G`.
- **Navigation & Resolve**:  
  - **Zed Stubs**: Clickable URLs for Zed stub calls and their gateway controller actions. Resolves usages of gateway controller actions.
  - **OMS XML**: Navigate to sub-processes and definitions of commands/conditions (e.g. in `OmsDependencyProvider`).
  - **Twig**: Quick jump to included, embedded, or extended files and widgets.
  - **Transfer Objects**: Jump between transfer object classes/instantiations and their XML definitions.
  - **Codeception**: Navigate from `codeception.yml` to helper classes.
  - **Glossary Keys**: Click on glossary keys in Twig files to jump directly to their definition in `glossary.json`.
  - **Parent Resolution**: Quickly navigate to the parent file for extended files (e.g., Twig templates, DTO XML files) except PHP classes (right click context menu).

## Configuration
All features can be configured and toggled in **Settings -> Tools -> PYZ Plugin**. You can also set your project directory and the base namespace there in case you don't use PYZ.

## About
Created during the *Turbine Kreuzberg Breakout Week 2021*, this plugin serves as a tool to streamline Spryker development. We welcome contributions and feedback. Feel free to open a pull request or report issues on GitHub.
<!-- Plugin description end -->

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
