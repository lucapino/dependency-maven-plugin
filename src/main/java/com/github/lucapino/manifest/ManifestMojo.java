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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.sonatype.aether.RepositorySystemSession;

/**
 * Goal which generate a version list.
 *
 * @goal update
 *
 */
public class ManifestMojo extends AbstractMojo {

    /**
     * Subset of comma separated artifactIds to modify
     *
     * @parameter
     */
    private String includeArtifactIds;
    /**
     * Destination directory
     *
     * @parameter
     * @required
     */
    private File destDir;

    /**
     * The Maven project
     *
     * @parameter default-value="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * The current repository/network configuration of Maven.
     *
     * @parameter default-value="${repositorySystemSession}"
     * @readonly
     */
    private RepositorySystemSession repoSession;

    /**
     * @component
     */
    protected ProjectDependenciesResolver projectDependenciesResolver;

    public Set<Artifact> getDependencyArtifacts(MavenProject project, RepositorySystemSession repoSession,
            ProjectDependenciesResolver projectDependenciesResolver) throws MojoExecutionException {

        DefaultDependencyResolutionRequest dependencyResolutionRequest = new DefaultDependencyResolutionRequest(project, repoSession);
        DependencyResolutionResult dependencyResolutionResult;

        try {
            dependencyResolutionResult = projectDependenciesResolver.resolve(dependencyResolutionRequest);
        } catch (DependencyResolutionException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }

        Set artifacts = new LinkedHashSet();
        if (dependencyResolutionResult.getDependencyGraph() != null
                && !dependencyResolutionResult.getDependencyGraph().getChildren().isEmpty()) {
            RepositoryUtils.toArtifacts(artifacts, dependencyResolutionResult.getDependencyGraph().getChildren(),
                    Collections.singletonList(project.getArtifact().getId()), null);
        }
        return artifacts;
    }

    @Override
    public void execute() throws MojoExecutionException {
        try {
            // creates an arraylist of string with included artifcatIds
            List<String> artifactIds = new ArrayList<String>();
            if (StringUtils.isNotEmpty(includeArtifactIds)) {
                String[] artifactIdArray = includeArtifactIds.split(",");
                for (String artifactId : artifactIdArray) {
                    artifactIds.add(artifactId);
                }
            }
            Set<Artifact> artifacts = getDependencyArtifacts(project, repoSession, projectDependenciesResolver);
            for (Artifact artifact : artifacts) {
                if (artifactIds.isEmpty() || artifactIds.contains(artifact.getArtifactId())) {
                    getLog().info("Processing artifact " + artifact);
                    JarFile jarFile = new JarFile(artifact.getFile());
                    Manifest manifest = jarFile.getManifest();
                    // write it to temp library
                    File tempFile = File.createTempFile("MANIFEST", null);
                    String content = "";
                    if (manifest == null) {
                        manifest = new Manifest();
                        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
                    }
                    manifest.write(new FileOutputStream(tempFile));
                    content = FileUtils.fileRead(tempFile, "UTF-8");
                    FileUtils.fileWrite(tempFile, "Trusted-Library: true\n" + content);

                    String fileName = jarFile.getName();
                    String fileNameLastPart = fileName.substring(fileName.lastIndexOf(File.separator));
                    File destFile = new File(destDir, fileNameLastPart);

                    JarOutputStream jos = new JarOutputStream(new FileOutputStream(destFile), new Manifest(new FileInputStream(tempFile)));
                    Enumeration<JarEntry> entries = jarFile.entries();

                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (!entry.getName().toLowerCase().endsWith("manifest.mf")) {
                            InputStream is = jarFile.getInputStream(entry);

                            //jos.putNextEntry(entry);
                            //create a new entry to avoid ZipException: invalid entry compressed size
                            jos.putNextEntry(new JarEntry(entry.getName()));
                            byte[] buffer = new byte[4096];
                            int bytesRead = 0;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                jos.write(buffer, 0, bytesRead);
                            }
                            is.close();
                            jos.flush();
                            jos.closeEntry();
                        }
                    }
                    jos.close();
                }
            }
        } catch (Exception ex) {
            throw new MojoExecutionException("Error in plugin", ex.getCause());
        }
    }
}
