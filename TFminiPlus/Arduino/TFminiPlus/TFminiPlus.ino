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

SoftwareSerial BTSerial(10,11);	// RX|TX	TFmini Plus


void setup(void)
{
	Serial.begin(115200);		// TFmini Plus bard rate
	BTSerial.begin(9600);		// Bluetooth bard rate
}

void loop(void)
{
	// Serial.print(count++);
	// Serial.print(". ");
	if (Serial.available()) {
		if (Serial.read() == HEADER) {		// read data equal 0x59
			data[0] = HEADER;
			if (Serial.read() == HEADER) {	// read data equal 0x59
				data[1] = HEADER;
				for (int i=2; i<9; i++) {
					data[i] = Serial.read();
				}

				// add data[0] to data[7]
				check = data[0] + data[1] + data[2] + data[3] + data[4] + data[5] + data[6] + data[7];

				if (data[8] == (check & 0xFF)) {
					dist = data[2] + (data[3] << 4);
					strength = data[4] + (data[5] << 4);
					temp = data[6] + (data[7] << 4);
					// output data
					String dataString = "dist:" + dist + ",strength:" + strength + ",temp:" + temp;
					BTSerial.println(dataString);
				} else {
					// Serial.println(F("Check data is Error."));
				}
			}
		}
	} else {
		// Serial.println(F("TFPlus is not available data"));
	}
	delay(100);
}
