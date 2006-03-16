The Spring Framework Rich Client Project (aka spring-rcp), release 1.0 
-----------------------------------------------------
http://www.springframework.org

1. INTRODUCTION

The Spring Rich Client Project (RCP) is a sub-project of The Spring Framework.  
Spring-RCP's mission is to provide an elegant way to build highly-configurable, 
GUI-standards-following rich-client applications faster by leveraging the Spring 
Framework, and a rich library of UI factories and support classes.  Initial 
focus is on providing support for Swing applications but a goal of Spring-RCP is 
to be view agnostic as much as possible.

The Spring Framework is a layered Java/J2EE application framework, based on code 
published in "Expert One-on-One J2EE Design and Development" by Rod Johnson 
(Wrox, 2002). Spring includes:

* Powerful JavaBeans-based configuration management, applying 
Inversion-of-Control principles. This makes wiring up applications quick and 
easy. No more singletons littered throughout your codebase, no more arbitrary 
properties files: one consistent and elegant approach everywhere. This core bean 
factory can be used in any environment, from applets to J2EE containers.

* Generic abstraction layer for transaction management, allowing for pluggable 
transaction managers, and making it easy to demarcate transactions without 
dealing with low-level issues. Generic strategies for JTA and a single JDBC 
DataSource are included. In contrast to plain JTA or EJB CMT, Spring's 
transaction support is not tied to J2EE environments.

* JDBC abstraction layer that offers a meaningful exception hierarchy (no more 
pulling vendor codes out of SQLException), simplifies error handling, and 
greatly reduces the amount of code you'll need to write. You'll never need to 
write another finally block to use JDBC again. The JDBC-oriented exceptions 
comply to Spring's generic DAO exception hierarchy.

* Integration with Hibernate, JDO, and iBATIS SQL Maps: in terms of resource 
holders, DAO implementation support, and transaction strategies. First-class 
Hibernate support with lots of IoC convenience features, addressing many typical 
Hibernate integration issues. All of these comply to Spring's generic 
transaction and DAO exception hierarchies.

* AOP functionality, fully integrated into Spring configuration management. You 
can AOP-enable any object managed by Spring, adding aspects such as declarative 
transaction management. With Spring, you can have declarative transaction 
management without EJB... even without JTA, if you're using a single database in 
Tomcat or another web container without JTA support.

* Flexible MVC web application framework, built on core Spring functionality. 
This framework is highly configurable via strategy interfaces, and accommodates 
multiple view technologies like JSP, Velocity, Tiles, iText, and POI. Note that 
a Spring middle tier can easily be combined with a web tier based on any other 
web MVC framework, like Struts, WebWork, or Tapestry.

You can use all of Spring's functionality in any J2EE server, and most of it 
also in non-managed environments. A central focus of Spring is to allow for 
reusable business and data access objects that are not tied to specific J2EE 
services. Such objects can be reused across J2EE environments (web or EJB), 
standalone applications, test environments, etc without any hassle.

Spring has a layered architecture. All its functionality builds on lower levels. 
So you can e.g. use the JavaBeans configuration management without using the MVC 
framework or AOP support. But if you use the web MVC framework or AOP support, 
you'll find they build on the configuration framework, so you can apply your 
knowledge about it immediately.

2. RELEASE INFO

NOTE: This information is outdated, see http://spring-rich-c.sf.net

Sprint-RCP requires J2SE 1.4 and the core SpringFramework.  Integration is 
provided with Log4J 1.2,CGLIB 1.0, Jakarta Commons Attributes, and Jakarta 
Commons Lang 2.0.

Release contents:
* "src" contains the Java source files for the framework
* "test" contains the Java source files for Spring's test suite
* "dist" contains various Spring distribution JAR files
* "lib" contains all third-party libraries needed for running the samples and/or 
  building the framework
* "docs" contains general documentation and API javadocs
* "samples" contains demo applications and skeletons

The "lib" directory is just included in the "-with-dependencies" download. Make 
sure to download this full distribution ZIP file if you want to run the sample 
applications and/or build the framework yourself. Ant build scripts for the 
framework and the samples are provided. The standard samples can be built with 
the included Ant runtime by invoking the corresponding "build.bat" files (see 
samples subdirectories).

Latest info is available at the public website: http://www.springframework.org
Project info at the SourceForge site: 
http://sourceforge.net/projects/springframework

The Spring Framework is released under the terms of the Apache Software License 
(see license.txt).
All libraries included in the "-with-dependencies" download are subject to their 
respective licenses.
This product includes software developed by the Apache Software Foundation 
(http://www.apache.org).
This product includes software developed by Clinton Begin 
(http://www.ibatis.com).

3. DISTRIBUTION JAR FILES

The "dist" directory contains the following distinct JAR files for use in 
applications. Both module-specific JAR files and a JAR file with all of Spring 
are provided. The following list specifies the respective contents and 
third-party dependencies. Libraries in brackets are optional, i.e. just 
necessary for certain functionality.

* "spring-richclient" (~380 KB)
- Contents: The rich-client project
- Dependencies: Commons Logging, (Log4J)

* "spring-rcp-resources" (~128 KB)
- Contents: Rich set of images and icons to support rich-clients
- Dependencies: None.

Note: The above lists of third-party libraries assume J2SE 1.4 as foundation.

4. WHERE TO START?

Documentation can be found in the "docs" directory:
* the Spring reference documentation
* various configuration and integration tutorials
* various Spring-related articles

Documented sample applications and skeletons can be found in "samples":
* "petclinic"

"Expert One-on-One J2EE Design and Development" discusses many of Spring's 
design ideas in detail. Note: The code examples in the book refer to the 
original framework version that came with the book. Thus, they need to be 
adapted for the current Spring release.
