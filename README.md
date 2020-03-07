# task-launcher

A SiLA 2 service which is able to start programs and processes on the host computer. The main 
purpose of this service is to start executables, run scripts, macros and other automation tasks
within a SiLA 2 feature.


### Build the Project

To build this project, a moderately current JDK and Maven installation is required.
Before building the actual project, the parent SiLA library has to be build to solve all 
dependencies.

```bash
cd task-launcher/extern/java_sila
mvn clean install -DskipTests
```

After that, the service can be built as usual. The resulting `*.jar`-file is located in the 
`target`-directory as `task-launcher-exec.jar`.


### Usage

Run the SiLA 2 service: `java -jar task-launcher-exec.jar`.
List the available network interfaces: `java -jar task-launcher-exec.jar -l yes`
Enable network discovery (e.g. on localhost): `java -jar task-launcher-exec.jar -n lo`

A SiLA 2 conform browser to inspect the available service(s) can be found here:
https://gitlab.com/SiLA2/sila_base/-/wikis/SiLA-Browser-Quickstart#run-the-sila-2-browser


### Security

Since this SiLA 2 service is able to execute any kind of program on the host computer, it is highly 
recommended to use this feature only in a restricted network. Restraining the file access to 
certain User and setting up appropriate firewall restrictions is also advised.
