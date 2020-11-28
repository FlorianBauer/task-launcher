# task-launcher

A SiLA 2 service which is able to start programs and processes on the installed host computer. The 
main purpose of this service is to start executables, run scripts, macros and other automation tasks
within a SiLA 2 feature and allow remote clients to use those.


### Build the Project

First, clone the repository.
```bash
git clone --recurse-submodules https://github.com/FlorianBauer/task-launcher.git
```

To build this project, JDK 8 or higher and a moderately current Maven installation is required.
Enter the project directory and use the following command to start the build process:

```bash
cd path/to/task-launcher/
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


### Arguments

| Short Flag |Long Flag | Default Value | Description |
|----|--------------------|-------|---------------------------------------------------------------|
| -h | --help             | N/A   | Show the help message                                         |
| -p | --port             | 50052 | Specify the port to use                                       |
| -n | --networkInterface | None  | Specify the network interface to use for SiLA Discovery       |
| -c | --config           | None  | Specify the file name to use to read/store server information |
| -l | --listNetworks     | N/A   | List names of available network interfaces                    |
| -e | --encryption       | yes   | Specify if the server must encrypt communication or not       |
| -v | --version          | N/A   | Show the current version                                      |


### Security

Since this SiLA 2 service is able to execute any kind of program on the host computer, it is highly 
recommended to use this feature only in a restricted network. Restraining the file access to 
certain Users and setting up appropriate firewall restrictions is also advised.


### Set up server auto-start on Windows 10

1. Open the Windows File Explorer and type `shell:startup` into the address bar.
2. Create a new text file in the folder called `task-launcher.txt`.
3. Open the file and insert the following content with the concrete network interface and file paths:
```
start java -jar "C:\path\to\launcher-server\target\launcher-server-exec.jar" -n eth01 -c "C:\path\to\store\task-launcher.conf"
```
4. Save and close the text file.
5. Change the suffix of the file by renaming `task-launcher.txt` into `task-launcher.bat`.

