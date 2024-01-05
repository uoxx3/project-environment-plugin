package uoxx3.project.environment.extension;

import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

public interface ProjectEnvironmentExtension {

    /**
     * Gets the keys of the project environment variables
     *
     * @return environment variable names
     */
    @NotNull Enumeration<String> keys();

    /**
     * Gets the values of the project environment variables
     *
     * @return environment variable values
     */
    @NotNull Enumeration<String> values();

    /**
     * Gets the value of the selected environment variable.
     *
     * @param key The name of the environment variable
     * @return The value of the selected environment variable or {@link Optional#empty()} if
     * the environment variable does not exist
     */
    @NotNull Optional<String> get(@NotNull String key);

    /**
     * Gets the value of the selected environment variable.
     *
     * @param key          The name of the environment variable
     * @param defaultValue The default value if the variable does not exist
     * @return The value of the selected environment variable or {@code defaultValue} if
     * the environment variable does not exist
     */
    default @NotNull String get(@NotNull String key, @NotNull String defaultValue) {
        return get(key).orElse(defaultValue);
    }

    /**
     * Change the value or add a new environment variable to the project
     *
     * @param key   The name of the environment variable
     * @param value The variable value
     * @return The last value of the variable. If this does not exist then a {@link Optional#empty()} is obtained
     */
    @NotNull Optional<String> set(@NotNull String key, @NotNull String value);

    /**
     * Change the value or add a new environment variable to the project
     *
     * @param entry Object with the values of the environment variable
     * @return The last value of the variable. If this does not exist then a {@link Optional#empty()} is obtained
     */
    default @NotNull Optional<String> set(@NotNull Map.Entry<String, String> entry) {
        return set(entry.getKey(), entry.getValue());
    }

    /**
     * Gets the environment variables in map entries.
     *
     * @return Collection with all the entries with the environment variables.
     */
    @NotNull Set<Map.Entry<String, String>> entrySet();

    /**
     * Gets the environment variable keys.
     *
     * @return Collection with all the keys from the environment variables.
     */
    @NotNull Set<String> keySet();

    /**
     * Gets the environment variable values.
     *
     * @return Collection with all the values from the environment variables.
     */
    @NotNull Collection<String> valueCollection();

    /**
     * Check if the environment variable exists within the project.
     *
     * @param key The environment variable you want to search for
     * @return {@code true} if the environment variable exists or {@code false} otherwise
     */
    boolean containsKey(@NotNull String key);

    /**
     * Check if the environment variable exists within the project.
     *
     * @param value The environment variable you want to search for
     * @return {@code true} if the environment variable exists or {@code false} otherwise
     */
    boolean containsValue(@NotNull String value);

    /**
     * Gets the total number of items within the project
     *
     * @return Total of environment variables
     */
    int size();

    /**
     * Iterate data functionally
     *
     * @param action Function you want to execute for each iteration
     */
    void foreach(@NotNull BiConsumer<String, String> action);

    /**
     * Load data into the project
     *
     * @param project The project you want to upload
     */
    void load(@NotNull Project project);

}
