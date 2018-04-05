# Runway Redeclaration Project

Built for Software Engineering Group Project.

### Specification

Task specification can be found [here](https://secure.ecs.soton.ac.uk/noteswiki/w/Runway_Re-declaration_Tool_2017)

### Depends upon:
* [Maven](https://maven.apache.org/)
* [launch4J](http://launch4j.sourceforge.net/)
* [sqlite-JDBC](https://bitbucket.org/xerial/sqlite-jdbc)
* [Reflections (lib)](https://github.com/ronmamo/reflections)
* [GCloud Java NIO](https://github.com/GoogleCloudPlatform/google-cloud-java/tree/master/google-cloud-contrib/google-cloud-nio)

### Build Script

The project can be built within IntelliJ, by selecting either the development or User build profile under 'Maven projects', or built from the command line with:

    `mvn clean compile package -P $ProfileName`

Where $ProfileName is replaced with one of the below, depending on your use case:
* Development.Build.exe - A console enabled exe file, designed for late-stage development.
* Development.Build.jar - Similar to Development.Build.exe, but lacking the launch4J executable Wrapper.
* User.Build.exe - A user ready, console disabled, executable wrapped exe file.
