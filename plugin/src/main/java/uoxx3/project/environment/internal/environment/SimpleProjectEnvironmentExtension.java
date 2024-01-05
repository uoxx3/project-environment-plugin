package uoxx3.project.environment.internal.environment;

import groovy.lang.GroovyObjectSupport;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import uoxx3.project.environment.extension.ProjectEnvironmentExtension;
import ushiosan.jvm.UError;
import ushiosan.jvm.collections.UArray;
import ushiosan.jvm.collections.UStack;
import ushiosan.jvm.filesystem.UResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleProjectEnvironmentExtension extends GroovyObjectSupport implements ProjectEnvironmentExtension {

    /* -----------------------------------------------------
     * Properties
     * ----------------------------------------------------- */

    private final Map<String, String> environment;

    /* -----------------------------------------------------
     * Constructors
     * ----------------------------------------------------- */

    public SimpleProjectEnvironmentExtension() {
        environment = Collections.synchronizedMap(new HashMap<>());
        // We load the environment variables by default
        loadBaseEnvironment();
    }

    /* -----------------------------------------------------
     * Methods
     * ----------------------------------------------------- */

    /**
     * Gets the keys of the project environment variables
     *
     * @return environment variable names
     */
    @Override
    public @NotNull Enumeration<String> keys() {
        return Collections.enumeration(environment.keySet());
    }

    /**
     * Gets the values of the project environment variables
     *
     * @return environment variable values
     */
    @Override
    public @NotNull Enumeration<String> values() {
        return Collections.enumeration(environment.values());
    }

    /**
     * Gets the value of the selected environment variable.
     *
     * @param key The name of the environment variable
     * @return The value of the selected environment variable or {@link Optional#empty()} if
     * the environment variable does not exist
     */
    @Override
    public @NotNull Optional<String> get(@NotNull String key) {
        String result = containsKey(key) ? environment.get(key) : null;
        return Optional.ofNullable(result);
    }

    /**
     * Change the value or add a new environment variable to the project
     *
     * @param key   The name of the environment variable
     * @param value The variable value
     * @return The last value of the variable. If this does not exist then a {@link Optional#empty()} is obtained
     */
    @Override
    public @NotNull Optional<String> set(@NotNull String key, @NotNull String value) {
        String result = environment.put(key, value);
        return Optional.ofNullable(result);
    }

    /**
     * Gets the environment variables in map entries.
     *
     * @return Collection with all the entries with the environment variables.
     */
    @Override
    public @NotNull Set<Map.Entry<String, String>> entrySet() {
        return environment.entrySet();
    }

    /**
     * Gets the environment variable keys.
     *
     * @return Collection with all the keys from the environment variables.
     */
    @Override
    public @NotNull Set<String> keySet() {
        return environment.keySet();
    }

    /**
     * Gets the environment variable values.
     *
     * @return Collection with all the values from the environment variables.
     */
    @Override
    public @NotNull Collection<String> valueCollection() {
        return environment.values();
    }

    /**
     * Check if the environment variable exists within the project.
     *
     * @param key The environment variable you want to search for
     * @return {@code true} if the environment variable exists or {@code false} otherwise
     */
    @Override
    public boolean containsKey(@NotNull String key) {
        return environment.containsKey(key);
    }

    /**
     * Check if the environment variable exists within the project.
     *
     * @param value The environment variable you want to search for
     * @return {@code true} if the environment variable exists or {@code false} otherwise
     */
    @Override
    public boolean containsValue(@NotNull String value) {
        return environment.containsValue(value);
    }

    /**
     * Gets the total number of items within the project
     *
     * @return Total of environment variables
     */
    @Override
    public int size() {
        return environment.size();
    }

    /**
     * Iterate data functionally
     *
     * @param action Function you want to execute for each iteration
     */
    @Override
    public void foreach(@NotNull BiConsumer<String, String> action) {
        environment.forEach(action);
    }

    /**
     * Load data into the project
     *
     * @param project The project you want to upload
     */
    @Override
    public void load(@NotNull Project project) {
        // We load the data in a tree manner, where the root project is the
        // first to load and the children overwrite the data, if necessary.
        Stack<Project> projectStack = UStack.make(project);
        Project currentProject = project;

        // We iterate all projects
        while (currentProject.getParent() != null) {
            currentProject = currentProject.getParent();
            projectStack.push(currentProject);
        }

        // LIFO iteration
        while (!projectStack.isEmpty()) {
            loadSingleProject(projectStack.pop());
        }
    }

    /* -----------------------------------------------------
     * Internal methods
     * ----------------------------------------------------- */

    /**
     * Loads system environment variables to the current project.
     */
    private void loadBaseEnvironment() {
        System.getenv().forEach(this::set);
    }

    /**
     * Load a simple project to the current environment
     *
     * @param project The project you want to load
     */
    private void loadSingleProject(@NotNull Project project) {
        Path projectDirectory = project.getProjectDir().toPath();
        Predicate<Path>[] predicates = UArray.make(
                Files::isRegularFile,
                UResource.extensionsPathOf(false, "env"));

        try (Stream<Path> stream = UResource.resourceWalk(projectDirectory, false, predicates)) {
            Set<Path> resourceSet = stream.collect(Collectors.toSet());

            // Iterate all files
            for (Path resource : resourceSet) {
                try {
                    loadEnvironmentFile(resource);
                } catch (Exception e) {
                    System.err.printf("Error to load \"%s\": %s", resource, UError.extractTrace(e));
                }
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * Load an environment file to the current project.
     *
     * @param path The environment target file
     * @throws IOException File opening or reading error
     */
    private void loadEnvironmentFile(@NotNull Path path) throws IOException {
        // Extract all properties from files
        try (InputStream fileStream = Files.newInputStream(path)) {
            // Generate new property instance
            Properties environmentResult = new Properties();

            // Load file to properties instance
            environmentResult.load(fileStream);

            // Load properties to current instance
            for (Map.Entry<Object, Object> entry : environmentResult.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();

                // Attach the result
                set(key, value);
            }
        }
    }

}
