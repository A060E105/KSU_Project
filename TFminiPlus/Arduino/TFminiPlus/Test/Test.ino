/**
  * Test program
  */

#include <SoftwareSerial.h>

#define HEADER 0x59

struct TFdata {
	int dist;
	int strength;
	int temp;
}TF;

bool flag = false;

SoftwareSerial BT(10,11);	// RX|TX


void BTSend(char *str)
{
	int i = 0;
	while (str[i] != '\0') {
		BT.write(str[i++]);
	}
}


void ReadTFmini(void)
{
	int data[9];
	if (Serial.available()) {
		if (Serial.read() == HEADER) {
			data[0] = HEADER;
			if (Serial.read() == HEADER) {
				data[1] = HEADER;
				for (int i=2; i<9; i++) {
					data[i] = Serial.read();
					delay(1);
				}

				int check = data[0] + data[1] + data[2] + data[3] + data[4] + data[5] + data[6] + data[7];

				if (data[8] == (check & 0xFF)) {
					TF.dist = data[2] + (data[3] << 4);
					TF.strength = data[4] + (data[5] << 4);
					TF.temp = data[6] + (data[7] << 4);
					flag = true;
					BTSend("True");
				} else {
					TF.dist = 0;
					TF.strength = 0;
					TF.temp = 0;
					flag = false;
					BTSend("check error");
				}
			} else {
				flag = false;
				BTSend("Not find 0x59");
			}
		} else {
			flag = false;
			BTSend("Not find one 0x59");
		}
	}
}


String resultString()
{
	String str;
	str = "s";
	str += TF.dist;
	str += ",";
	str += TF.strength;
	str += ",";
	str += TF.temp;
	str += "e";

	return str;
}

void setup(void)
{
	Serial.begin(115200);
	BT.begin(9600);
	BTSend("Hello");
}

void loop(void)
{
	ReadTFmini();

	if (true) {
		String str = resultString();
		char charbuf[20];
		str.toCharArray(charbuf,20);
		BTSend(charbuf);
	}
}
