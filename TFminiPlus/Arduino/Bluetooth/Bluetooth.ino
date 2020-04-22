/**
  * Bluetooth test program
  */

#include <SoftwareSerial.h>

SoftwareSerial BTSerial(10,11);		// RX|TX

void setup()
{
	Serial.begin(9600);
	BTSerial.begin(9600);	// Bluetooth bard rate
}

void loop()
{
	if (Serial.available()) {
		// BTSerial.println(Serial.readString());
    Serial.println(BTSerial.readString());
	}
}
