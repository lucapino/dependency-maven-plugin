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
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

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
            Set<Artifact> artifacts = project.getDependencyArtifacts();
            for (Artifact artifact : artifacts) {
                if (artifactIds.contains(artifact.getArtifactId())) {
                    JarFile jarFile = new JarFile(artifact.getFile());
                    Manifest manifest = jarFile.getManifest();
                    // write it to temp library
                    File tempFile = File.createTempFile("MANIFEST", null);
                    manifest.write(new FileOutputStream(tempFile));
                    String content = FileUtils.fileRead(tempFile, "UTF-8");
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
