#!/bin/bash
# just a handy script to launch the jar file on the Raspberry Pi remotely. 
mvn package
scp target/brewcontrol-0.1.1-SNAPSHOT-executable.jar pi@raspberrypi:~/brewcontrol.jar
scp src/script/brewcontrol pi@raspberrypi:brewcontrol
ssh pi@raspberrypi 'sudo rm /etc/init.d/brewcontrol'
ssh pi@raspberrypi 'sudo mv ~/brewcontrol /etc/init.d/brewcontrol'
ssh pi@raspberrypi 'sudo chown root:root /etc/init.d/brewcontrol'
ssh pi@raspberrypi 'sudo chmod 755 /etc/init.d/brewcontrol'
ssh pi@raspberrypi 'sudo service brewcontrol restart'
ssh pi@raspberrypi 'tail -f brewcontrol.log'
