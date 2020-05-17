/**
  * Test ESP32 board bluetooth
  */

#include <BluetoothSerial.h>

BluetoothSerial BT;

void setup(void)
{
	Serial.begin(115200);
	BT.begin("TFminiPlus");
}

void loop(void)
{
	Serial.println("Hello");
	BT.println("Hello");
	delay(1000);
}
