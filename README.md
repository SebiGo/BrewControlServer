# BrewControlServer
BrewControl standalone java server

## What does this software do?
This software brews beer. Well, it controls the mashing process. You can run this software on a Raspberry Pi and you need to connect a DS18B20 temperature sensor and some relay, preferably a solid state relay. The relay connects to the heating in your mashing kettle.
In this repository, you will find the server, that exposes all necessary rest services to control the mashing process. You can find a suitable client in the [BrewControlClient repository][BrewControlClient]. 

## Installation

###Get it all up and running
There are a few steps to get everything up and running:
1. Prepare hardware
2. Prepare operating system
3. Compile or download the server
4. Launch the server
5. Launch the server automatically (optional)
These steps will be described in the next sections.

#### 1. Prepare hardware

Before you can start using this software, you need to set up the hardware. You  need
* a Raspberry Pi,
* a DS18B20 1-wire Temperature Sensor and a 4.7 kOhm resistor
* some sort of a relay (preferably a Solid State Relay)
* some button

This is how to connect the devices:
* Connect the button to GPIO1 and Ground.
* Connect the Relay to GPIO4 and Ground.
* Connect the DS18B10 to the Raspberry Pi, Pin1 GROUND to 0V/Ground, Pin2 DATA  to GPIO7, Pin3 POWER to 3V3

See [the pi4j website][pi4j] for a pin layout.

#### 2. Prepare operating system
 
Install the current [Raspbian operating][raspbian] system to an SD card and boot the 
RaspberryPi. Please see the [installation instructions on the Raspberry Pi website][raspinstall].

Next ssh into your RaspberryPi and launch raspi-config:
```
ssh pi@raspberrypi
sudo raspi-config
```
Hint: Password is 'raspberry'. 
Expand the file system and quit the tool.

Edit the file */boot/config.txt* and add a line at the end of the file:
```
dtoverlay=w1-gpio 
```

Edit the file */etc/modules* and two lines at the end of the file:
```
w1-gpio
w1-therm
```

Install java to your Raspberry Pi:
```
sudo apt-get install oracle-java8-jdk
```

Reboot your Raspberry Pi
```
sudo reboot
```

#### 3. Compile or download the server

Download the source and cd into that directory to build:
```
git clone git@github.com:SebastianGoodrick/BrewControlServer.git
cd BrewControlServer
mvn package
scp target/brewcontrol-0.1.0-executable.jar pi@raspberrypi:~/brewcontrol.jar
```

#### 4. Launch the server

Execute the jar file by running
 
```
sudo java -jar brewcontrol.jar gpio
```

on the Raspberry Pi. This launches the standalone brewcontrol server. You should get something like this:
```
556 [main] INFO ch.goodrick.brewcontrol.BrewControl - Einmaischen at 57.0?C for 1 min (don't continue automatically).
566 [main] INFO ch.goodrick.brewcontrol.BrewControl - Eiweissrast at 55.0?C for 15 min (continue automatically).
568 [main] INFO ch.goodrick.brewcontrol.BrewControl - Maltoserast at 62.0?C for 50 min (continue automatically).
570 [main] INFO ch.goodrick.brewcontrol.BrewControl - Verzuckerungsrast at 72.0?C for 25 min (don't continue automatically).
573 [main] INFO ch.goodrick.brewcontrol.BrewControl - Abmaischen at 78.0?C for 1 min (don't continue automatically).
9340 [main] INFO org.apache.cxf.endpoint.ServerImpl - Setting the server's publish address to be http://10.31.0.40:8080/
10565 [main] INFO org.eclipse.jetty.util.log - Logging initialized @13536ms
11255 [main] INFO org.eclipse.jetty.server.Server - jetty-9.2.z-SNAPSHOT
11531 [main] WARN org.eclipse.jetty.server.handler.AbstractHandler - No Server set for org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine$1@34e239
11945 [main] INFO org.eclipse.jetty.server.ServerConnector - Started ServerConnector@128bd63{HTTP/1.1}{10.31.0.40:8080}
11954 [main] INFO org.eclipse.jetty.server.Server - Started @14984ms
12054 [main] WARN org.eclipse.jetty.server.handler.ContextHandler - Empty contextPath
12227 [main] INFO org.eclipse.jetty.server.handler.ContextHandler - Started o.e.j.s.h.ContextHandler@3869f4{/,null,AVAILABLE}
12249 [main] INFO ch.goodrick.brewcontrol.sensor.SensorDS18B20 - Found and using sensor DS18B20 with ID: 28-0000067b69f6
```

Please note the line *Setting the server's publish address to be ...*. This is the connection string you need to enter in your client. 

Rather than *gpio* you may also specify:
* **gpio**: This really is what you want. (GPIO-Pins are used.)
* **piface**: This uses the Piface extension board Relay1 and Button1.
* **simulate**:  Simulate doesn't use any physical buttons, sensors or relays, it just simulates these devices.

Please note that brewcontrol requires sudo permission as it uses [pi4j which
uses wiringpi][pi4jsudo], which requires superuser rights.

#### 5. Launch the server automatically
If you want the BrewControlServer to launch automatically, you have to crate a start script.

Create the file */etc/init.d/brewcontrol* (as root) with this content:
```
#!/bin/bash
# /etc/init.d/brewcontrol

### BEGIN INIT INFO
# Provides:          brewcontrol
# Required-Start:    $remote_fs
# Required-Stop:     $remote_fs
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: BrewControl
# Description:       This service is used to brew beer.
### END INIT INFO

PATH=/bin:/usr/bin:/sbin:/usr/sbin
DESC="brewcontrol server"
NAME=brewcontrol
PIDFILE=/var/run/brewcontrol.pid
SCRIPTNAME=/etc/init.d/"$NAME"

test -f $DAEMON || exit 0

. /lib/lsb/init-functions


case "$1" in
        start)  log_daemon_msg "Starting BrewControlServer" "brewcontrol"
                /usr/bin/sudo /usr/bin/nohup /usr/bin/java -jar /home/pi/brewcontrol.jar gpio >> /home/pi/brewcontrol.log 2>&1 &
                echo $! > $PIDFILE
                log_end_msg $?
                ;;
        stop)   log_daemon_msg "Stopping BrewControlserver" "brewcontrol"
                killproc -p $PIDFILE $DAEMON
                RETVAL=$?
                [ $RETVAL -eq 0 ] && [ -e "$PIDFILE" ] && rm -f $PIDFILE
                log_end_msg $RETVAL
                ;;
        restart) log_daemon_msg "Restarting Brewcontrol" "brewcontrol"
                $0 stop
                $0 start
                ;;
        reload|force-reload) log_daemon_msg "Reloading BrewControlServer" "brewcontrol"
                # there is no reload method
                log_end_msg 0
                ;;
        status)
                status_of_proc -p $PIDFILE $DAEMON $NAME && exit 0 || exit $?
                ;;
        *)      log_action_msg "Usage: /etc/init.d/brewcontrol {start|stop|status|restart|reload|force-reload}"
                exit 2
                ;;
esac

exit 0

```
You need to make the script executable and add it to runlevel 2:
```
sudo chmod 755 /etc/init.d/brewcontrol
update-rc.d brewcontrol defaults
```

## Contact, Support, Bugs, Feature requests
Please use [GitHub Issues][issues] for bugs and feature requests.

[raspberry]: http://raspberrypi.org
[pi4j]: http://pi4j.com/pins/model-b-rev2.html
[pi4jsudo]: http://pi4j.com/faq.html#permissions
[issues]: https://github.com/SebastianGoodrick/BrewControlServer/issues
[raspbian]: https://www.raspberrypi.org/downloads/
[raspinstall]: https://www.raspberrypi.org/documentation/installation/installing-images/README.md
[BrewControlClient]: https://github.com/SebastianGoodrick/BrewControlClient