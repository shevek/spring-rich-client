This module provides an archetype for creating a simple Spring Rich Client
application. All the basic pieces are provided and the resulting application
is runnable.

NOTE: This archetype relies on new features in the archetype core, so you
must be using at least maven-archetype-core-1.0-alpha-4 or later.


To create a new project using this archetype, use the following command
(with appropriate substitutions):

mvn archetype:create \
-DarchetypeGroupId=org.springframework.richclient \
-DarchetypeArtifactId=spring-richchlient-archetype \
-DarchetypeVersion=0.2.0-SNAPSHOT \
-DgroupId=your.group.id -DartifactId=your-artifact-id
