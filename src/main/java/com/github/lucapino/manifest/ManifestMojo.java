/*
 * Copyright 2013 Luca Tagliani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lucapino.manifest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.resolution.VersionRangeRequest;
import org.sonatype.aether.resolution.VersionRangeResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.version.Version;

/**
 * Goal which generate a version list.
 *
 * @goal list
 *
 */
public class ManifestMojo extends AbstractMojo {
    /**
     * Starting version
     *
     * @parameter
     * @required
     */
    private String startingVersion;
    /**
     * Starting version
     *
     * @parameter default-value="false"
     * @required
     */
    private boolean includeSnapshots;
    /**
     * GroupId of project.
     *
     * @parameter default-value="${project.groupId}
     * @required
     */
    private String groupId;
    /**
     * ArtifactId of project.
     *
     * @parameter default-value="${project.artifactId}
     * @required
     */
    private String artifactId;
    /**
     * Name of the property that contains the ordered list of versions
     * requested.
     *
     * @parameter default-value="${project.artifactId}
     * @required
     */
    private String versionListPropertyName;
    /**
     * The Maven project
     *
     * @parameter default-value="${project}"
     * @readonly
     */
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            List<Dependency> dependencies = project.getDependencies();
            for (Dependency dependency : dependencies) {
                dependency.
            }
        } catch (Exception ex) {
            throw new MojoExecutionException("Error in plugin", ex.getCause());
        }
    }
}
