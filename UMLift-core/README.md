# UMLift-core

UMLift-core is the foundation of the UMLift project, providing essential functionality such as UMLet to EMF
transformation and artifact generation using Eclipse Modeling Framework (EMF) libraries.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [License](#license)
- [Contact](#contact)
- [Back to Main Page](../README.md)

## Overview

Two main packages:

- [EMF](src/main/java/cz/cuni/mff/umlift/core/emf) - provides code generation using EMF
- [Transformation](src/main/java/cz/cuni/mff/umlift/core/transformation) - provides UMLet diagram transformations

## Features

Transforms UML elements from UMLet diagrams into Ecore elements for use with EMF.

Supports various UML Elements:

- Class
- Abstract Class
- Interface
- Enumeration

Supports various UML Class relations:

- Association
- Inheritance
- Realization
- Aggregation
- Composition

## License

Distributed under the GNU General Public License v3.0. See [LICENSE](./../LICENSE.txt) for more information.

## Contact

Rastislav Vojtuš - [email me]()