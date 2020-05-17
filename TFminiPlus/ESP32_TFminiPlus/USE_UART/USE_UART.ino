/**
  * Useing UART1/UART2
  * Now have one TFmini Plus
  */

#include <HardwareSerial.h>

#define HEADER 0x59

// HardwareSerial L_TFmini(1);			// Left TFmini Plus
HardwareSerial R_TFmini(2);			// Right TFmini Plus

struct TFdata{
	int dist;
	int strength;
	int temp;
}R_TF;

// Clean Serial buffer
void CleanFlush(HardwareSerial *TF)
{
	char ch;
	while(TF->available() > 0) {
		ch = TF->read();
	}
}

// Read TFmini Plus data
void ReadTFmini(HardwareSerial *pTFmini, TFdata *pTFdata)
{
	int data[9]; 			// data buffer
	if (pTFmini->available() > 0) {
		if (pTFmini->read() == HEADER) {
			data[0] = HEADER;
			if (pTFmini->read() == HEADER) {
				data[1] = HEADER;
				for(int i=2; i<9; i++) {
					data[i] = pTFmini->read();
				}

				// checksum
				int check = data[0] + data[1] + data[2] + data[3] + data[4] + data[5] + data[6] + data[7];

				if (data[8] == (check & 0xFF)) {			// checksum equal data[8]
					pTFdata->dist = data[2] + (data[3] << 8);
					pTFdata->strength = data[4] + (data[5] << 8);
					pTFdata->temp = data[6] + (data[7] << 8);
				} else {
					CleanFlush(pTFmini);			// Clean Serial buffer
				}
			} else {
				CleanFlush(pTFmini);
			}
		} else {
			CleanFlush(pTFmini);
		}
	}
}

void setup(void)
{
	Serial.begin(115200);
	// L_TFmini.begin(115200, SERIAL_8N1, 4, 2);		// baud rate, parity, RX|TX
	R_TFmini.begin(115200, SERIAL_8N1, 16, 17);
	Serial.println("Start program.");
}


void loop(void)
{
	;;
}
