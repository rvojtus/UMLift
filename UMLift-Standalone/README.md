# UMLift - Standalone

UMLift - Standalone is a self-contained application that integrates UMLet's GUI with the core functionality of
UMLift-core to support Informal Model-Driven Development (IMDD). It runs independently of any IDE and can be executed
directly via the command line (CLI).

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Installation & Setup](#installation--setup)
- [Usage](#usage)
- [License](#license)
- [Contact](#contact)
- [Back to Main Page](../README.md)

## Overview

This application is essentially UMLet Standalone with UMLift-core integrated, providing extended functionality for
Informal Model-Driven Development (IMDD).

## Features

- Integrated UMLet 15.1 GUI for easy UML diagram modeling
- Custom EMF Palette with all essential elements for modeling
- `UMLift` MenuBar Item
    - Provides a `Generate Code` button to generate artifacts from the current Diagram
    - Includes configuration options
- Automatic File Explorer Opening
    - After a successful transformation & generation, the folder containing the generated artifacts automatically opens
      in the OS-specific file explorer

## Installation & Setup

Follow these steps to install and run the program from the command line:

#### 1. **Build the project**

Run the following command in the root of the project:

```shell
mvn clean install
```

#### 2. Run the program

After building, you can run the program using one of the following methods:

- From the root directory:

```shell
mvn exec:java -pl UMLift-Standalone
```

- From inside the `UMLift-Standalone` directory:

```shell
mvn exec:java
```

## Usage

## License

Distributed under the GNU General Public License v3.0. See [LICENSE](./../LICENSE.txt) for more information.

## Contact

Rastislav Vojtuš - [email me]()