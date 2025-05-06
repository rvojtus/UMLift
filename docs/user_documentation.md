# UMLift - User Documentation

## Description

This is a user guide for the UMLift project.

## Introduction

**UMLift** is a tool suite designed to support **Informal Model-Driven Development**.  
It allows users to create, edit, and generate artifacts from informal metamodel specifications created with UMLet
diagrams.

UMLift includes:

- **[UMLift Core](/UMLift-core/README.md)** - a core module providing metamodel extraction, code and artifact
  generation, and other related
  functionality.
- **[UMLift Standalone Application](#umlift-standalone)** — a desktop tool integrating UMLet and core functionality for
  code and artifact
  generation.
- **[UMLift IntelliJ IDEA Plugin](#umlift-intellij-idea-plugin)** — integrates UMLift functionality directly into
  IntelliJ IDEA.
- **[UMLift Maven Plugin](#umlift-maven-plugin)** — automate artifact generation as part of your Maven builds.

---

## UMLift Standalone

### Requirements

- Maven
- Java 21+
- macOS, Linux, Windows

### Installation

- Download the latest release from [...]
- Unzip and run the executable (`umlift-standalone.jar`)

### Installation - using maven

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

### Usage

1. Launch **UMLift Standalone**.
2. **Open or create a `.uxf` file** using the menu bar: **File -> Open**.
3. **Design an informal metamodel** using UML-style elements.
4. **Configure the generation** using the menu bar item **UMLift** -> **Options...**
5. Use the menu bar item **UMLift** -> **Generate Code** to generate:
    - Ecore and GenModel
    - Java project based on the created metamodel
    - Maven POM for the generated project
6. You will be presented with the directory of the generated project.

For more information on how to use the diagram editor,
see [the official site of the UMLet project.](https://github.com/umlet/umlet)

#### Configuration

In the picture below is the configuration dialog, which can be used to configure the code & artifacts generation
process. It can be accessed using the menu bar item **UMLift** -> **Options...**

![Standalone Config Dialog](/docs/images/standalone/standalone-default-config-dialog.png)

- `Project name` : name of the generated project
- `NS URI` : namespace URI of the project
- `NS Prefix` : namespace prefix for the project
- `Ecore file name` : name of the generated Ecore file
- `Package name` : name of the generated Java package
- `Gen JDK level` : compliance level of Java for the generated project, i.e. 21.0 sets it to JDK 21
- `GenModel file name` : name of the generated GenModel file
- `Project root directory` : destination directory, where the project will be generated. Default directory is the
  system's
  Documents folder
- `Save` button : saves the configuration. It is persistent so it will load after restarting the program
- `Reset` button : reset the configuration to the default values
- `Cancel` button : cancels the current configuration changes

#### Example of executing the generation

Let's generate code & artifacts for the metamodel depicted in the picture below:

![SimpleClinicSystem example metamodel diagram](/docs/images/SimpleClinicSystemExample.png)

After pressing the **Generate Code** menu item, we will get this generated project structure:

![generated project structure](/docs/images/generatedProjectStructure.png)

The generated Java code is in `src/main/java` and the Ecore and GenModel files are in `resoures/EMFModels` directories.

## UMLift IntelliJ IDEA Plugin

Integrates UMLet GUI into the IntelliJ File Editor. Adds new context menu actions to provide the MDD functionality.
Configuration through IntelliJ's Settings Dialog.

### Installation

- Install via JetBrains Marketplace: **Settings → Plugins → Browse Repositories → Search "UMLift" → Install**.

### Usage

#### Opening UMLet Diagram Files

Simply double-click on any UMLet Diagram file - `.uxf`, or create a new `.uxf` file and a custom File Editor opens:
![IntelliJ UMLet FileEditor](/docs/images/intellij/intellijExampleUMLiftNewProject.png)

#### Generating code & artifacts from UMLet diagrams

1. Open your project in IntelliJ IDEA.
2. Right-click on a `.uxf` file in the Project view.
3. Choose **UMLift → Generate Code from Diagram**. ![IntelliJ submenus](/docs/images/intellij/intelliJSubMenus.png)
4. Created Artifacts are placed in `src/main/java` or a configured output folder.
5. Ecore and GenModel files are created in `src/main/resources/EMFModels` or a configured output folder.

![generated project structure](/docs/images/generatedProjectStructure.png)

The generated Java code is in `src/main/java` and the Ecore and GenModel files are in `resoures/EMFModels` directories.

#### Configuration

In the picture below is the configuration dialog, which can be used to configure the code & artifacts generation
process. It can be accessed using IntelliJ's **Settings** → **Tools** → **UMLift**

![Standalone Config Dialog](/docs/images/intellij/intellijSettingsDialog.png)

- `Project name` : name of the generated project
- `NS URI` : namespace URI of the project
- `NS Prefix` : namespace prefix for the project
- `Ecore file name` : name of the generated Ecore file
- `Package name` : name of the generated Java package
- `Gen JDK level` : compliance level of Java for the generated project, i.e. 21.0 sets it to JDK 21
- `GenModel file name` : name of the generated GenModel file
- `Ecore + GenModel destination` : directory for the generated models, default being `src/main/resources/EMFModels`
- `Generated files output directory` : directory where the generated files will be placed, default being `src/main/java`
- `Generate POM with the Project` : a checkbox whether a Maven POM is created for the generated project

## UMLift Maven Plugin

This plugin offers multiple `Maven Goals` that provide MDD functionality - more info in [Usage](#usage-2).

### Installation

Simply run `mvn clean install` in the root of the project, and the plugin will be installed into the local maven
repository.

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

### Usage

These are the provided Maven Goals:

- `generate-emf-from-umlet` - transforms provided UMLet Diagram file - `.uxf`, to EMF Ecore and GenModel resources.
- `generate-code-from-umlet` - transforms a provided UMLet Diagram file - `.uxf`, and generates Java code.
- `generate-code-from-ecore` - generates Java code directly from a provided Ecore file.

These are all the available parameters for each Maven Goal:

#### Goal: `generate-emf-from-umlet`

| Parameter        | Required | Description                           | Default Value          |
|------------------|:--------:|---------------------------------------|------------------------|
| modelDir         |    ❌     | Output directory for Generated models | `resources/`           |
| umletFile        |    ✅     | UMLet File to be transformed          |                        |
| projectName      |    ❌     | Name of the project                   | `exampleProjectName`   |
| NsPrefix         |    ❌     | Project Namespace Prefix              | `exampleProjectPrefix` |
| NsURI            |    ❌     | Project Namespace URI                 | `exampleProjectURI`    |
| ecoreFileName    |    ❌     | name of the generated Ecore File      | `ecore`                |
| genModelFileName |    ❌     | name of the generated GenModel File   | `genmodel`             |
| basePackageName  |    ❌     | name of the base package              | `org.example`          |
| genJDKLevel      |    ❌     | compliance level of Java              | `JDK210`               |

#### Goal: `generate-code-from-umlet`

| Parameter        | Required | Description                           | Default Value          |
|------------------|:--------:|---------------------------------------|------------------------|
| outputDir        |    ❌     | Output Dir for Generated Java Project | `src-gen/`             |
| modelDir         |    ❌     | Output Dir for Generated EMF Models   | `resources/`           |
| umletFile        |    ✅     | UMLet File to be transformed          |                        |
| projectName      |    ❌     | Used in the generated Ecore Model     | `exampleProjectName`   |
| NsPrefix         |    ❌     | Namespace Prefix - for Ecore Model    | `exampleProjectPrefix` |
| NsURI            |    ❌     | Namespace URI - for Ecore Model       | `exampleProjectURI`    |
| ecoreFileName    |    ❌     | File name of the generated Ecore File | `ecore`                |
| genModelFileName |    ❌     | name of the generated GenModel File   | `genmodel`             |
| basePackageName  |    ❌     | name of the base package              | `org.example`          |
| genJDKLevel      |    ❌     | compliance level of Java              | `JDK210`               |

#### Goal: `generate-code-from-ecore`

| Parameter       | Required | Description                             | Default Value   |
|-----------------|:--------:|-----------------------------------------|-----------------|
| outputDir       |    ❌     | Output Dir for Generated Java Project   | `src/main/java` |
| ecoreFile       |    ✅     | Ecore File to be used in the generation |                 |
| basePackageName |    ❌     | name of the base package                | `org.example`   |
| genJDKLevel     |    ❌     | compliance level of Java                | `JDK210`        |

#### Invoking manually from CLI

Example for `generate-emf-from-umlet` goal:

```bash
mvn cz.cuni.mff:UMLift-maven-plugin:1.0-SNAPSHOT:generate-emf-from-umlet -DumletFile=/path/to/umlet/file.uxf -DprojectName=NewProject
```

#### Configuring the Plugin in `pom.xml`

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
                    <phase>generate-sources</phase>
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

We can specify the `phase` to have the plugin executed in one of maven's build lifecycles. In this example, we have
specified the `generate-sources` phase. It is then implicitly executed during this lifecycle, or we can execute it
ourselves by typing the command `mvn generate-sources`. After the execution, we will see the generated files in their
respective folders.

## External Tools & Libraries

This project relies on the following external tools and libraries. These tools are developed and maintained by their
respective organizations; **this project does not own them**, it simply uses them to enable key features.

- [UMLet](https://github.com/umlet/umlet) - A lightweight UML tool for creating UML diagrams.
- [Eclipse Modeling Framework (EMF)](https://projects.eclipse.org/projects/modeling.emf.emf) - A modeling framework and
  code generation facility for building tools and other applications based on a structured data model.
- [Apache Maven](https://maven.apache.org/) - Used for project & dependency management, and for the Mojo Plugin
- [Jetbrains IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html) - Used to develop the
  IntelliJ IDEA
  plugin.

## License

Distributed under the GNU General Public License v3.0. See [LICENSE](./../LICENSE.txt) for more information.

## Contact

Rastislav Vojtuš - [email me]()