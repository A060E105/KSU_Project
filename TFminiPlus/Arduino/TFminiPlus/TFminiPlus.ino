/**
  *	This program code is NodeMCU board
  */

#include <SoftwareSerial.h>

#define HEADER 0x59

SoftwareSerial TF(10,11);		// RX|TX
// SoftwareSerial BT(5,6);			// RX|TX

struct TF {
	int dist;
	int strength;
	int temp;
}TFmini;

void TFSerialFlush()
{
	while(TF.available() > 0) {
		char ch = TF.read();
	}
}

// Send character array to Bluetooth
/*
void Send(char* str)
{
	int i=0;
	while(str[i] != '\0') {
		BT.write(str[i]);
	}
}
*/

// Send string to Bluetooth
/*
void BTSend(String str)
{
	char charbuff[str.length() + 1];
	str.toCharArray(charbuff, (str.length() + 1));
	Send(charbuff);
}
 */

void setup(void)
{
	Serial.begin(115200);
	TF.begin(115200);
	// BT.begin(9600);
}

void loop(void)
{
	String str = "";
	int data[9];
	int check;
	if (TF.available() > 0) {
		if (TF.read() == HEADER) {
			data[0] = HEADER;
			if (TF.read() == HEADER) {
				data[1] = HEADER;
				for (int i=2; i<9; i++) {
					data[i] = TF.read();
				}
				check = data[0] + data[1] + data[2] + data[3] + data[4] + data[5] + data[6] + data[7];
				if (data[8] == (check & 0xFF)) {
					TFmini.dist = data[2] + (data[3] << 8);
					TFmini.strength = data[4] + (data[5] << 8);
					TFmini.temp = data[6] + (data[7] << 8);

					str = "dist=";
					str += TFmini.dist;
					str += ",strength=";
					str += TFmini.strength;
					str += ",temp=";
					str += TFmini.temp;

					// BTSend(str);

					Serial.println(str);
				} else {
					Serial.println("Check Error");
					TFSerialFlush();
				}
			} else {
				Serial.println("Not find two HEADER");
				TFSerialFlush();
			}
		} else {
			Serial.println("Not find one HEADER");
			TFSerialFlush();
		}
	}
}
