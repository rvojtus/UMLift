---
author: Rastislav Vojtuš
title: UMLift Developer Documentation
date: 2025-05-06
---

# UMLift Developer Documentation

This document contains the system's architecture, components, and their interactions.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
    - [UMLift Core](#umlift-core)
    - [UMLift Standalone](#umlift-standalone)
    - [UMLift Maven Plugin](#umlift-maven-plugin)
    - [UMLift IntelliJ Plugin](#umlift-intellij-plugin)
- [Java Documentation](#java-documentation)
- [READMEs](#readmes)
- [External Tools & Libraries](#external-tools--libraries)
- [License](#license)
- [Contact](#contact)

## Overview

This project provides support for Informal Model-Driven Development by integrating Eclipse Modeling Framework (EMF) with
UMLet. It enables transformation of informal metamodel specifications, created using UMLet as UML diagrams, into formal
models represented as Ecore and GenModel. From these models, a fully functional Java project can be generated,
supporting model-driven development workflows.

## Features

- UMLet GUI - a way to create UML diagrams
- UMLet to EMF model transformation - creates Ecore and GenModel resources from UMLet diagrams
- Custom palette - `UMLift Elements`, provides all necessary elements for metamodeling
- Java Code Generation - produces corresponding Java classes using EMF
- Jetbrains IntelliJ IDEA support - a plugin that provides all the functionality inside the IDE
- Apache Maven support - a plugin for Maven that provides transformation and artifact generation functionality for Maven
  projects

## Project Structure

UMLift Project is structured according to Maven conventions. It is composed of a top-level parent module - `UMLift`,
having groupID of `cz.cuni.mff`.

There are several components:

- **dependencies** - contains UMLet project JARs
- [**UMLift-core**](#umlift-core) - provides transformation and generation functionality
- [**UMLift-Standalone**](#umlift-standalone) - a standalone version, runnable from CLI
- [**UMLift-IntelliJ-Plugin**](#umlift-intellij-plugin) - provides a plugin for IntelliJ IDEA
- [**UMLift-maven-plugin**](#umlift-maven-plugin) - provides a Maven plugin

Here is a C4 model, made using Structurizr, showcasing the modules and their interactions within the system:

![c4 model level 2](/docs/images/c4/structurizr-1-Container-001.png)

### UMLift-core

UMLift-core is the foundation of the UMLift project, providing essential functionality such as UMLet to EMF
transformation and artifact generation using Eclipse Modeling Framework (EMF) libraries.

The transformation of UMLet diagrams to Ecore models is done in the `Transformation` package, by the
`UMLetToEcoreTransformer` class. The main method of this class is the `EPackage transform(Path inputUMLetFile)` method
that provides the transformation for other parts of the system. All supported UML Class relationships are contained
in the `UMLClassRelations` enumeration.

Code and artifact generation are contained in the `EMF` package. The creation of EMF's GenModels is done using the
`GenModelGenerator` class. The `ModelToCodeGenerator` class provides the code generation functionality, by leveraging
the Eclipse Modeling Framework's libraries.

The `Config` package contains the `GenerationConfig` class, which holds all the necessary parameters to configure the
metamodel transformation and the code and artifact generation.

The `Pom` package provides configuration and generation of Project Object Models.

![c4 model UMLift-core container](/docs/images/c4/structurizr-1-Component-core.png)

### UMLift Standalone

The Standalone module leverages UMLet's standalone module. It launches UMLet GUI and customizes it by adding new
menu bar items to configure and execute code and artifact generation.

The class `UMLiftMainStandalone` contains the entry point - the `main` method, though which the application can be
launched. A configuration dialog component is provided by the `ConfigDialog` class.

We can launch the Standalone application using the following command: `mvn exec:java -pl UMLift-Standalone`.

### UMLift Maven Plugin

The maven plugin provides three Maven Goals, which are specified by the three classes: `EMF2CodeMojo`, `UMLet2CodeMojo`,
and `UMLet2EMFMojo`. Each of these extends the `AbstractMojo` class, provided by the Maven Plugin Development libraries.
They override multiple methods, most notably the `execute` method, which provides the plugin with the functionality.

### UMLift IntelliJ Plugin

The UMLift-IntelliJ-Plugin is the only part of the UMLift system that is not using Maven, but Gradle for the build
system. The Gradle build system is configured using the files located in `UMLift-IntelliJ/src` directory. The plugin's
configuration can be found in the `UMLift-IntelliJ/src/main/resources/META-INF/plugin.xml` file.

The plugin is composed of multiple packages:

- **Actions** : Provides user actions like copy, cut, delete etc. by implementing `AnAction` class. Each action is
  implemented by its respective class.
- **ContextMenu** : Handles the construction and customization of context menus shown within the editor, allowing
  integration of plugin-specific actions, like code generation, directly into the right-click menu.
- **CustomSaving** : Manages customized saving behavior for the UMLet GUI. It propagates save request to the GUI, to
  save modified diagrams.
- **Editor** : Implements File Editor by integrating UMLet GUI and IntelliJ interfaces.
- **FileTypes** : Defines custom file types supported by the plugin, including file extensions (.uxf, .ecore,
  .genmodel), icons, syntax highlighting, and file type registration.
- **GUI** : Contains custom extension and implementation of the UMLet GUI.
- **SchemaProviders** : Provides schemas for the Ecore and GenModel file types.
- **Settings** : Defines configuration options exposed to the user via the IDE’s Settings dialog, including plugin
  preferences, paths, and feature toggles.

To build the plugin, run the following command: `gradle buildPlugin`. The plugin is located in the `build/distributions`
directory.

To run the plugin for development purposes, run the following command: `gradle runIde`.

There is a known issue with this module when it is opened in IntelliJ IDEA, some dependencies are not recognized
properly. To resolve this issue, run the following command: `mvn idea:idea`. This issue is only "cosmetic" and does not
affect the plugin's functionality and compilation.

![c4 model UMLift intellij plugin](/docs/images/c4/structurizr-1-Component-intellij.png)

## Java Documentation

All Java source files are thoroughly documented using Javadoc comments. Each class, method, and significant field
includes descriptive comments to explain its purpose, usage, and behavior. Inline comments are also used where
appropriate to clarify complex logic.

Generating Javadoc:

```shell
mvn javadoc:aggregate
```

or it is generated during the `verify` phase:

```shell
mvn clean verify
```

The generated documents, in the form of a website, can be located in the `target/reports/apidocs` directory.

## READMEs

Here's a list of all available README files:

- [UMLift](/README.md)
- [UMLift Core](/UMLift-core/README.md)
- [UMLift Standalone](/UMLift-Standalone/README.md)
- [UMLift Maven Plugin](/UMLift-maven-plugin/README.md)
- [UMLift IntelliJ Plugin](/UMLift-IntelliJ-Plugin/README.md)

## External Tools & Libraries

This project relies on the following external tools and libraries. These tools are developed and maintained by their
respective organizations; **this project does not own them**, it simply uses them to enable key features.

- [UMLet](https://github.com/umlet/umlet) - A lightweight UML tool for creating UML diagrams.
- [Eclipse Modeling Framework (EMF)](https://projects.eclipse.org/projects/modeling.emf.emf) - A modeling framework and
  code generation facility for building tools and other applications based on a structured data model.
- [Apache Maven](https://maven.apache.org/) - Used for project & dependency management, and for the Mojo Plugin
- [Jetbrains IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html) - Used to develop the
  IntelliJ IDEA plugin.

## License

Distributed under the GNU General Public License v3.0. See [LICENSE](/LICENSE.txt) for more information.

## Contact

Rastislav Vojtuš - [email me]()