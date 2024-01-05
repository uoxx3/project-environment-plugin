package uoxx3.project.environment;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import uoxx3.project.environment.extension.ProjectEnvironmentExtension;
import uoxx3.project.environment.internal.environment.SimpleProjectEnvironmentExtension;

public class PluginEntry implements Plugin<Project> {

    /**
     * Apply this plugin to the given target object.
     *
     * @param project The target object
     */
    @Override
    public void apply(@NotNull Project project) {
        ProjectEnvironmentExtension environment = project.getExtensions()
                .create(ProjectEnvironmentExtension.class,
                        "projectEnv",
                        SimpleProjectEnvironmentExtension.class);

        // Load project environment files
        environment.load(project);
    }

}
