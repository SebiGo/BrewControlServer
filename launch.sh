#!/bin/bash
# just a handy script to launch the jar file on the Raspberry Pi remotely. 
mvn package
scp target/brewcontrol-0.1.0-executable.jar pi@raspberrypi:~
ssh pi@raspberrypi 'sudo killall java'
ssh pi@raspberrypi 'sudo java -jar ~/brewcontrol-0.1.0-executable.jar gpio'

