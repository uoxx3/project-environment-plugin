# Gradle Environment Variables Plugin

## Current version

`project-environment-version` = `1.0.0`

## Description

This plugin automatically loads the environment variable files (`*.env`) as well as the system environment variables.
Access to these environment variables is done in the same way as with the native gradle
extension `org.gradle.api.plugins.ExtraPropertiesExtension`:

```kotlin
// Kotlin DSL
val element = projectEnv["ENV_NAME"] // -> Optional<String>
val elementWithDefaultValue = projectEnv["ENV_OPT_NAME", "Default Value"] // -> java.lang.String
```

```groovy
// Groovy DSL
var element = projectEnv.get("ENV_NAME") // -> Optional<String>
var elementWithDefaultValue = projectEnv.get("ENV_OPT_NAME", "Default Value") // -> java.lang.String
```

## How to use

To use the plugin you need to add the plugin to Gradle:

```kotlin
// Kotlin DSL
plugins {
    id("io.github.uoxx3.project-environment") version "[project-environment-version]"
}
```

```groovy
// Groovy DSL
plugins {
    id 'io.github.uoxx3.project-environment' version '[project-environment-version]'
}
```