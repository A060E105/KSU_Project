/**
  *	TFmini Plus Test program
  */

#include <SoftwareSerial.h>

#define HEADER 0x59

int dist;		// 距離
int strength;	// 強度
int temp;		// 晶片溫度
int check;		// checksum
int data[9];	// TFmini Plus data

// int count;

SoftwareSerial TFPlus(10,11);	// RX|TX	TFmini Plus
// SoftwareSerial BTSerial(2,3);	// RX|TX	Bluetooth

int freeRam() 	// The function is check SRAM available space
{
  extern int __heap_start, *__brkval; 
  int v; 
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
}

void setup(void)
{
	Serial.begin(9600);			// Serial bard rate
	TFPlus.begin(115200);		// TFmini Plus bard rate
	// BTSerial.begin(38400);		// Bluetooth bard rate
	Serial.println(F("Program is start"));
}

void loop(void)
{
	// Serial.print(count++);
	// Serial.print(". ");
	if (TFPlus.available()) {
		if (TFPlus.read() == HEADER) {		// read data equal 0x59
			data[0] = HEADER;
			if (TFPlus.read() == HEADER) {	// read data equal 0x59
				data[1] = HEADER;
				for (int i=2; i<9; i++) {
					data[i] = TFPlus.read();
				}

				// add data[0] to data[7]
				check = data[0] + data[1] + data[2] + data[3] + data[4] + data[5] + data[6] + data[7];

				if (data[8] == (check & 0xFF)) {
					dist = data[2] + (data[3] << 4);
					strength = data[4] + (data[5] << 4);
					temp = data[6] + (data[7] << 4);
					// output data
					Serial.println(F("================="));
					Serial.print(F("dist = "));
					Serial.println(dist);
					Serial.print(F("strength = "));
					Serial.println(strength);
					Serial.print(F("temp = "));
					Serial.println(temp);
				} else {
					Serial.println(F("Check data is Error."));
				}
			}
		}
	} else {
		Serial.println(F("TFPlus is not available data"));
	}
	Serial.println();
	Serial.print(F("SRAM="));
	Serial.println(freeRam());
	delay(100);
}