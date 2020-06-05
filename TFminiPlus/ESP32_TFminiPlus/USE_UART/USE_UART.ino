/**
  * Useing UART1/UART2
  * Now have one TFmini Plus
  */

#include <ArduinoJson.h>
#include <HardwareSerial.h>
#include <BluetoothSerial.h>

#define HEADER 0x59

StaticJsonDocument<200> jsondata;

BluetoothSerial BT;			// Bluetooth 

HardwareSerial L_TFmini(1);			// Left TFmini Plus
HardwareSerial R_TFmini(2);			// Right TFmini Plus

struct TFdata{
	const String name;
	int dist;
	int strength;
	float temp;
}R_TFdata={"R"},L_TFdata={"L"};

// Clean Serial buffer
void CleanFlush(HardwareSerial *pTF)
{
	char ch;
	while(pTF->available() > 0) {
		ch = pTF->read();
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
					pTFdata->temp = ((data[6] + (data[7] << 8)) / 100);
				} else {
					Serial.println("Check Error.");
					CleanFlush(pTFmini);			// Clean Serial buffer
				}
			} else {
				Serial.println("Not find two HEADER.");
				CleanFlush(pTFmini);
			}
		} else {
			Serial.println("Not find one HEADER.");
			CleanFlush(pTFmini);
		}
	}
}

String resultString(TFdata *pTFdata)
{
	String str = "";

	str = "sensor=";
	str += pTFdata->name;
	str += ",dist=";
	str += pTFdata->dist;
	str += ",strength=";
	str += pTFdata->strength;
	str += ",temp=";
	str += pTFdata->temp;

	return str;
}

void sendJSON(TFdata *pL_TFdata, TFdata *pR_TFdata)
{
	// left sensor data
	jsondata[pL_TFdata->name]["dist"] = pL_TFdata->dist;
	jsondata[pL_TFdata->name]["strength"] = pL_TFdata->strength;
	jsondata[pL_TFdata->name]["temp"] = pL_TFdata->temp;
	// right sensor data
	jsondata[pR_TFdata->name]["dist"] = pR_TFdata->dist;
	jsondata[pR_TFdata->name]["strength"] = pR_TFdata->strength;
	jsondata[pR_TFdata->name]["temp"] = pR_TFdata->temp;

	serializeJson(jsondata, BT);		// send json data to Bluetooth
	serializeJson(jsondata, Serial);	// send json data to Serial
}

void setup(void)
{
	Serial.begin(115200);
	BT.begin("TFminiPlus");							// set Bluetooth name
	L_TFmini.begin(115200, SERIAL_8N1, 4, 2);		// baud rate, parity, RX|TX ; Left TFmini Plus sensor.
	R_TFmini.begin(115200, SERIAL_8N1, 16, 17);		// right TFmini plus sensor.
	Serial.println("Start program.");
}


void loop(void)
{
	ReadTFmini(&R_TFmini, &R_TFdata);		// Read Right TFmini plus data
	ReadTFmini(&L_TFmini, &L_TFdata);		// Read Left TFmini plus data
	sendJSON(&L_TFdata, &R_TFdata);			// data to json and send
}
