# task-launcher

A SiLA 2 service which is able to start programs and processes on the host computer. The main 
purpose of this service is to start programs, run scripts, macros and other automation tasks within 
the SiLA 2 pipeline.


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

Run the SiLA service: `java -jar task-launcher-exec.jar`.
List the available network interfaces: `java -jar task-launcher-exec.jar -l yes`
Enable network discovery (e.g. on localhost): `java -jar task-launcher-exec.jar -n lo`

A SiLA 2 conform browser to inspect the available service(s) can be found here:
https://gitlab.com/SiLA2/sila_base/-/wikis/SiLA-Browser-Quickstart#run-the-sila-2-browser
