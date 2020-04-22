/**
  * description:
  * 	The program code of entering AT mode.
  */

#include <SoftwareSerial.h>

SoftwareSerial BT(10,11);	// RX|TX

void setup()
{
	Serial.begin(9600);		// Serial baud rate
	BT.begin(38400);		// Bluetooth baud rate
	Serial.println("Entering AT Mode:");
}

void loop()
{
	if (Serial.available()) {
		BT.write(Serial.read());
	}
	if (BT.available()) {
		Serial.write(BT.read());
	}
}
