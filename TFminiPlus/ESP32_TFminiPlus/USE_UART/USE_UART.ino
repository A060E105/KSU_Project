/**
  * Useing UART1/UART2
  */

#include <ArduinoJson.h>
#include <HardwareSerial.h>
#include <BluetoothSerial.h>
#include "TFdata.h"
#include "esp_system.h"

#define HEADER 0x59
#define BUZZER 5

StaticJsonDocument<200> jsondata;

BluetoothSerial BT;                 // Bluetooth

HardwareSerial L_TFmini(1);         // Left TFmini Plus
HardwareSerial R_TFmini(2);         // Right TFmini Plus

const int wdtTimeout = 3000;        // time in ms to trigger the watchdog
hw_timer_t *timer = NULL;

void IRAM_ATTR resetModule() {
    ets_printf("reboot\n");
    esp_restart();
}

/*
// TFmini plus data struct
typedef struct {
    const String name;
    int dist;
    int strength;
    float temp;
}TFdata;
*/

TFdata R_TFdata={"R"};
TFdata L_TFdata={"L"};

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
    int data[9];            // data buffer
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

                if (data[8] == (check & 0xFF)) {            // checksum equal data[8]
                    pTFdata->dist = data[2] + (data[3] << 8);
                    pTFdata->strength = data[4] + (data[5] << 8);
                    pTFdata->temp = ((data[6] + (data[7] << 8)) / 100);
                } else {
                    Serial.println("Check Error.");
                    CleanFlush(pTFmini);            // Clean Serial buffer
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

// Debug function
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

// data to JSON and send JSON data
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

    serializeJson(jsondata, BT);        // send json data to Bluetooth
    serializeJson(jsondata, Serial);    // send json data to Serial
}

void setBuzzer(bool status)
{
    switch(status) {
        case true:
            // Buzzer is low enable
            digitalWrite(BUZZER, LOW);
            break;
        case false:
            // disable Buzzer 
            digitalWrite(BUZZER, HIGH);
            break;
        default:
        ;;
    }
}


void setup(void)
{
    // Initialization
    Serial.begin(115200);                           // Set Serial baudrate
    BT.begin("TFminiPlus");                         // set Bluetooth name
    L_TFmini.begin(115200, SERIAL_8N1, 4, 2);       // baud rate, parity, RX|TX ; Left TFmini Plus sensor.
    R_TFmini.begin(115200, SERIAL_8N1, 16, 17);     // right TFmini plus sensor.

    // buzzer initialization
    pinMode(BUZZER, OUTPUT);
    digitalWrite(BUZZER, HIGH);     // Buzzer is low enable

    // watchdog setting
    timer = timerBegin(0, 80, true);                    // timer 0, div 80
    timerAttachInterrupt(timer, &resetModule, true);    // attach callback
    timerAlarmWrite(timer, wdtTimeout * 1000, false);   // set time in us
    timerAlarmEnable(timer);                            // enable interrupt

    Serial.println("Start program.");
}


void loop(void)
{
    timerWrite(timer, 0);                   // reset timer (feed watchdog)

    // read bluetooth command
    if (BT.available()) {
        char cmd;
        cmd = BT.read();
        Serial.println(cmd);
        switch(cmd) {
            case '0':
                ReadTFmini(&R_TFmini, &R_TFdata);       // Read Right TFmini plus data
                ReadTFmini(&L_TFmini, &L_TFdata);       // Read Left TFmini plus data
                sendJSON(&L_TFdata, &R_TFdata);         // data to json and send
                setBuzzer(false);                       // close buzzer
                break;
            case '1':
                ReadTFmini(&R_TFmini, &R_TFdata);       // Read Right TFmini plus data
                ReadTFmini(&L_TFmini, &L_TFdata);       // Read Left TFmini plus data
                sendJSON(&L_TFdata, &R_TFdata);         // data to json and send
                setBuzzer(true);                        // open buzzer
                break;
            default:
                ;;
        }
    }

    CleanFlush(&R_TFmini);          // clear serial buffer
    CleanFlush(&L_TFmini);
    delay(10);
}
