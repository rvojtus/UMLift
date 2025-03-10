# UMLift

A project providing support for Informal Model-Driven Development.

## Table of Contents

- [About The Project](#about-the-project)
- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Installation & Setup](#installation--setup)
    - [Standalone](#standalone)
    - [IntelliJ IDEA Plugin](#intellij-idea-plugin)
    - [Maven Plugin](#maven-plugin)
- [Documentation](#documentation)
- [External Tools & Libraries](#external-tools--libraries)
- [License](#license)
- [Contact](#contact)

## About The Project

This project was born out of the need for a lightweight and informal approach to Model-Driven Development (MDD) and
meta-model creation. The current standard, the Eclipse Modeling Framework (EMF), does not support such an informal
design process. Additionally, EMF is tightly integrated with the Eclipse IDE, which can be restrictive.

## Overview

This project provides support for Informal Model-Driven Development by integrating Eclipse Modeling Framework (EMF) with
UMLet. It enables transformation of informal metamodel specifications, created using UMLet as UML diagrams, into formal
models represented as Ecore and GenModel. From these models, a fully functional Java project can be generated,
supporting model-driven development workflows.

## Features

- UMLet GUI - a way to create UML diagrams
- UMLet to EMF model transformation - creates Ecore and GenModel resources from UMLet diagrams
- Adds custom palette - `EMF Palette`, provides all necessary elements for IMDD
- Java Code Generation - produces corresponding Java classes using EMF
- IntelliJ IDEA support - a plugin that provides all the functionality inside the IDE
- Maven support - a plugin for Maven that provides transformation and artifact generation functionality for Maven
  projects

## Project Structure

UMLift Project is structured according to Maven conventions. It is composed of a top-level parent module - `UMLift`,
having groupID of `cz.cuni.mff`. <br>
There are several submodules:

- `umlet-15.1` - contains UMLet project files
- `UMLift-core` - provides transformation and generation functionality
- `UMLift-maven-plugin` - provides Maven plugin
- `UMLift-Standalone` - a standalone version, runnable from CLI

There is also an IntelliJ Plugin that uses Gradle instead of Maven.

## Installation & Setup

### Requirements

- Maven
- Java 21+
- macOS/Linux, should work on Windows - not tested

### Standalone

A standalone application using GUI from the UMLet project, with an added support to transform the currently open UML
Diagram to EMF Models, and to generate code and artifacts from it.<br>
For more information about the installation, setup & usage: [Standalone](UMLift-Standalone/README.md)

### IntelliJ IDEA Plugin

A plugin for JetBrains IntelliJ IDEA. Integrated UMLet GUI to a File Editor, to enable seamless creation and editing of
UMLet Diagram files - `.uxf`. Custom Settings Dialog Panel, to configure and customize the transformation and generation
process. New Context Menu Actions, to enable quick and easy start of the artifact generation & transformation.<br>
For more information about the installation, setup & usage: [IntelliJ Plugin](UMLift-IntelliJ-Plugin/README.md)

### Maven Plugin

A Mojo plugin for Maven. Integrates functionality to be used as a part of a Maven lifecycle. Offers configuration and
various "maven goals", to transform UMLet Diagram files - `.uxf`, to EMF Models - `Ecore` & `GenModel`, and to
consequently generate Java source code & artifacts from those models.<br>
For more information about the installation, setup & usage: [Maven Mojo](UMLift-maven-plugin/README.md)

## Documentation

Here's a list of all available README files:

- [UMLift Core](UMLift-core/README.md)
- [UMLift Standalone](UMLift-Standalone/README.md)
- [UMLift Maven Plugin](UMLift-maven-plugin/README.md)
- [UMLift IntelliJ Plugin](UMLift-IntelliJ-Plugin/README.md)

Generating Javadoc:

```bash
mvn javadoc:javadoc
```

## External Tools & Libraries

This project relies on the following external tools and libraries. These tools are developed and maintained by their
respective organizations; **this project does not own them**, it simply uses them to enable key features.

- [UMLet](https://github.com/umlet/umlet) - A lightweight UML tool for creating UML diagrams.
- [Eclipse Modeling Framework (EMF)](https://projects.eclipse.org/projects/modeling.emf.emf) - A modeling framework and
  code generation facility for building tools and other applications based on a structured data model.
- [Maven](https://maven.apache.org/) - Used for project & dependency management, and for the Mojo Plugin
- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html) - Used to develop the IntelliJ IDEA
  plugin.

## License

Distributed under the GNU General Public License v3.0. See [LICENSE](LICENSE.txt) for more information.

## Contact

Rastislav Vojtuš - [email me]()