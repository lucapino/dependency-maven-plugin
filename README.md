<h1>Maven Dependency Plugin</h1><br>

Maven plugin for altering MANIFEST.MF, ading Trusted-Library and Permissions attribute

[![][Build Status img]][Build Status]
[![][Coverage Status img]][Coverage Status]
[![][Dependency Status img]][Dependency Status]
[![][license img]][license]
[![][Maven Central img]][Maven Central]
[![][Javadocs img]][Javadocs]

Plugin documentation can be found at https://lucapino.github.io/dependency-maven-plugin

*Maven 3.3.&ast; artifact*
```
<plugin>
     <groupId>com.github.lucapino</groupId>
     <artifactId>dependency-maven-plugin</artifactId>
     <version>1.0.1</version>
</plugin>
```

*Maven 3.5.&ast; artifact*
```
<plugin>
     <groupId>com.github.lucapino</groupId>
     <artifactId>dependency-maven-plugin</artifactId>
     <version>2.0.0</version>
</plugin>
```

Available goals:
================
* **update** - updates a manifest, adding Trusted-Library and Permission attributes

Example plugin definition:
==========================
    <plugin>
        <groupId>com.github.lucapino</groupId>
        <artifactId>dependency-maven-plugin</artifactId>
        <version>1.0.1</version>
    </plugin>

[Build Status]:https://travis-ci.org/lucapino/dependency-maven-plugin
[Build Status img]:https://travis-ci.org/lucapino/dependency-maven-plugin.svg?branch=master

[Coverage Status]:https://codecov.io/gh/lucapino/dependency-maven-plugin
[Coverage Status img]:https://codecov.io/gh/lucapino/dependency-maven-plugin/branch/master/graph/badge.svg

[Dependency Status]:https://snyk.io/test/github/lucapino/dependency-maven-plugin
[Dependency Status img]:https://snyk.io/test/github/lucapino/dependency-maven-plugin/badge.svg?style=flat

[license]:LICENSE
[license img]:https://img.shields.io/badge/license-Apache%202-blue.svg

[Maven Central]:https://maven-badges.herokuapp.com/maven-central/com.github.lucapino/dependency-maven-plugin
[Maven Central img]:https://maven-badges.herokuapp.com/maven-central/com.github.lucapino/dependency-maven-plugin/badge.svg

[Javadocs]:http://www.javadoc.io/doc/com.github.lucapino/dependency-maven-plugin
[Javadocs img]:http://javadoc.io/badge/com.github.lucapino/dependency-maven-plugin.svg
