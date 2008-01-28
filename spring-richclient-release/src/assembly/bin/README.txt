Files
=====

The directory lib contains all libraries of which spring-richclient or petclinic depend.
See the documentation's dependencies report to see which module depends on what.

The directory modules contains all spring-richclient modules.
IMPORTANT: you might wonder why some of the packages are so small. This is because some jars are indeed
empty, but used as placeholders. After the initial release (0.1.0) we will split things up into their
appropriate modules. To give you the opportunity to already define the correct dependencies, we included these
jars to act as placeholders. When using the full jar, no difficulties will be experienced with upgrading to a new
spring richclient version as no package renaming will be involved in this step.

The directory full contains all spring-richclient merged into a single jar for convenience
and all the sources zipped in one file.

The directory sample/petclinic/standalone contains all the needed petclinic jars together with a script
to start the sample in standalone mode.

The directory sample/petclinic/webapp contains a war which can be deployed to a J2EE webserver
and contains the webstartable client


Documentation
=============

All documentation is avaible through the project website at
http://spring-rich-c.sourceforge.net/
