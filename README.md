# task-launcher

A SiLA 2 service which is able to start programs and processes on the installed host computer. The 
main purpose of this service is to start executables, run scripts, macros and other automation tasks
within a SiLA 2 feature and allow remote clients to use those.


### Build the Project

To build this project, a moderately current JDK and Maven installation is required.
Enter the project directory and use the following command to start the build process:

```bash
mvn clean install -DskipTests
```

After an successful built, the resulting `*.jar`-file is located in the `./launcher-server/target/` 
directory as `launcher-server-exec.jar`.


### Usage

Run the SiLA 2 service: `java -jar launcher-server-exec.jar`.
List the available network interfaces: `java -jar launcher-server-exec.jar -l yes`
Enable network discovery (e.g. on localhost): `java -jar launcher-server-exec.jar -n lo`

SiLA 2 conform, dynamic clients to inspect the available service(s) can be found here:
* [SiLA Browser](https://unitelabs.ch/technology/plug-and-play/sila-browser/)
* [sila-orchestrator](https://github.com/FlorianBauer/sila-orchestrator)


### Security

Since this SiLA 2 service is able to execute any kind of program on the host computer, it is highly 
recommended to use this feature only in a restricted network. Restraining the file access to 
certain Users and setting up appropriate firewall restrictions is also advised.
