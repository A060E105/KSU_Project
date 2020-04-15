#!/usr/bin/python3
#
# TFmini Plus python3 test program
#

import serial

baudrate = 115200
HEADER = 0x59

try:
    ser = serial.Serial("/dev/ttyUSB0", baudrate, timeout=0.5)
    if ser.isOpen():
        print ("serial port already exists")
    else:
        ser.open()
        if ser.read() == HEADER:
            data[0] = HEADER
            if ser.read() == HEADER:
                data[1] = HEADER
                for i in range(2,8):
                    data[i] = ser.read()
                    check = data[0] + data[1] + data[2] + data[3] + data[4] + data[5] + data[6] + data[7]; 
                    if (data[8] == (check & 0xFF)):
                        dist = data[2] + (data[3] << 4)
                        strength = data[4] + (data[5] << 4)
                        temp = data[6] + (data[7] << 4)
                        print ("dist = {0}\nstrength = {1}\ntemp = {2}".format(dist,strength,temp))
except KeyboardInterrupt:
    print ("Key board interrupt.")
finally:
    ser.close()
    print ("Serial close.")
