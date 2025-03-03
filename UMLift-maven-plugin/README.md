# UMLift - Maven Plugin

This is a plugin for Apache Maven. It provides facilities and tools for Informal Model-Driven Development (
MDD).

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Installation & Setup](#installation--setup)
- [Usage](#usage)
- [License](#license)
- [Contact](#contact)

## Overview

This plugin offers multiple `Maven Goals` that provide MDD functionality - more info in [Features](#features).

## Features

These are the provided Maven Goals:

- `generate-emf-from-umlet` - transforms provided UMLet Diagram file - `.uxf`, to EMF Ecore and GenModel resources.
- `generate-code-from-umlet` - does transformation and generates Java code using EMF.
- `generate-code-from-ecore` - generates Java code directly from provided Ecore file.

### Table of all available parameters:<br>

#### Goal: `generate-emf-from-umlet`

| Parameter        | Required | Description                              | Default Value          |
|------------------|:--------:|------------------------------------------|------------------------|
| modelDir         |    ❌     | Output Dir for Generated EMF Models      | `resources/`           |
| umletFile        |    ✅     | UMLet File to be transformed             |                        |
| projectName      |    ❌     | Used in the generated Ecore Model        | `exampleProjectName`   |
| NsPrefix         |    ❌     | Namespace Prefix - for Ecore Model       | `exampleProjectPrefix` |
| NsURI            |    ❌     | Namespace URI - for Ecore Model          | `exampleProjectURI`    |
| ecoreFileName    |    ❌     | File name of the generated Ecore File    | `ecore`                |
| genModelFileName |    ❌     | File name of the generated GenModel File | `genmodel`             |

#### Goal: `generate-code-from-umlet`, also same as above

| Parameter | Required | Description                           | Default Value |
|-----------|:--------:|---------------------------------------|---------------|
| outputDir |    ❌     | Output Dir for Generated Java Project | `src-gen/`    |

#### Goal: `generate-code-from-ecore`

| Parameter | Required | Description                             | Default Value |
|-----------|:--------:|-----------------------------------------|---------------|
| outputDir |    ❌     | Output Dir for Generated Java Project   | `src-gen/`    |
| ecoreFile |    ✅     | Ecore File to be used in the generation |               |

## Installation & Setup

### Get the Plugin from Maven Central

WIP

### Install it manually

Simply run `mvn clean install` in the root of the project, and the plugin should be available for use.

Add the Plugin to Your Project's `pom.xml`:

```xml

<build>
    <plugins>
        <plugin>
            <groupId>cz.cuni.mff</groupId>
            <artifactId>UMLift-maven-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
        </plugin>
    </plugins>
</build>
```

## Usage

### Invoke manually from CLI

Example for `generate-emf-from-umlet` goal:

```bash
  mvn cz.cuni.mff:UMLift-maven-plugin:1.0-SNAPSHOT:generate-emf-from-umlet 
      -DumletFile=/path/to/umlet/file.uxf -DprojectName=NewProject
```

### Configuring the Plugin in `pom.xml`

Example for `generate-code-from-umlet` goal:

```xml

<build>
    <plugins>
        <plugin>
            <groupId>cz.cuni.mff</groupId>
            <artifactId>UMLift-maven-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate-code-from-umlet</goal>
                    </goals>
                    <configuration>
                        <outputDir>src-gen</outputDir>
                        <modelDir>resources</modelDir>
                        <umletFile>/path/to/umlet/file.uxf</umletFile>
                        <projectName>NewProject</projectName>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## License

Distributed under the GNU General Public License v3.0. See [LICENSE](./../LICENSE.txt) for more information.

## Contact

Rastislav Vojtuš - [email me]()