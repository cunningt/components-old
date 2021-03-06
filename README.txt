README
======
Project instructions for developers below.

Java:
-----
- Tested version = Sun JDK 1.6.0_20
- export JAVA_HOME=/path/to/your/jdk
- export PATH=$JAVA_HOME/bin:$PATH

Maven:
------
- Tested version = 2.2.1
- 'mvn clean install' to build
- 'mvn install -DskipTests=true' to skip tests
- 'mvn javadoc:aggregate' to just run javadoc (see target/site/apidocs/index.html)
- 'mvn site' to generate site docs (see target/site/index.html)

Eclipse:
--------
- Tested version = JBoss Developer Studio 3.0.0

[Using M2Eclipse]
    - File -> Import... -> Maven -> Existing Maven Projects -> [Next >] ->
        [Browse...] ->/path/to/components -> [OK] -> [Finish]

[Using mvn eclipse:eclipse]
- 'mvn eclipse:clean; mvn eclipse:eclipse' (creates .project, .classpath, .settings/)

- Window -> Preferences -> Java -> Build Path -> Classpath Variables -> [New...]
	Name: M2_REPO
	Path: /home/<you>/.m2/repository (<you> == jdoe, for example)

- File -> Import... -> General -> Existing Projects into Workspace -> [Next >] ->
	[Browse...] ->/path/to/components -> [OK] -> [Finish]

