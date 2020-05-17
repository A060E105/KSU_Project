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


SoftwareSerial TFmini(10,11);	// RX|TX
bool flag;


void CleanFlush(SoftwareSerial *pTF)
{
	char ch;
	while(pTF->available() > 0) {
		ch = pTF->read();
	}
}


void ReadTFmini(SoftwareSerial *pTF, TFdata *pTFdata)
{
	int data[9];
	flag = false;
	if (pTF->available() > 0) {
		if (pTF->read() == HEADER) {
			data[0] = HEADER;
			if (pTF->read() == HEADER) {
				data[1] = HEADER;
				for(int i=2; i<9; i++) {
					data[i] = pTF->read();
				}
				int check = 0;
				for(int i=0; i<8; i++) {
					check += data[i];
				}
				if (data[8] == (check & 0xFF)) {
					pTFdata->dist = data[2] + (data[3] << 8);
					pTFdata->strength = data[4] + (data[5] << 8);
					pTFdata->temp = data[6] + (data[7] << 8);
					flag = true;
				} else {
					Serial.println("Check Error.");
					CleanFlush(pTF);
				}
			} else {
				Serial.println("Not find TWO HEADER.");
				CleanFlush(pTF);
			}
		} else {
			Serial.println("Not find ONE HEADER.");
			CleanFlush(pTF);
		}
	}
}

void setup(void)
{
	Serial.begin(115200);
	TFmini.begin(115200);
}

void loop(void)
{
	ReadTFmini(&TFmini, &TF);
	String str;
	str = "dist=";
	str += TF.dist;
	str += ",strength=";
	str += TF.strength;
	str += ",temp=";
	str += TF.temp;
	if (flag)
		Serial.println(str);
}
