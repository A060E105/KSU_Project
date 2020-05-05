# Enter AT Mode

```cpp
#include <SoftwareSerial.h>

SoftwareSerial BTSerial(10.11);		// RX|TX

void setup()
{
	Serial.begin(9600);
	Serial.println("Enter AT commands:");
	BTSerial.begin(38400);		// HC-05 default baudrate
}

void loop()
{
	if (BTSerial.available())
		Serial.write(BTSerial.read());
	if (Serial.available())
		BTSerial.write(Serial.read());
}
```

# AT Commands

#### 查詢用指令
| 指令 | 說明 |
|:----:|:----:|
|AT|測試指令|
|AT+NAME?|查詢名稱|
|AT+UART?|查詢鮑率|
|AT+ADDR?|查詢位址|
|AT+VERSION?|查詢版本|
|AT+ROLE?|查詢主從模式|
|AT+PSWD?|查詢密碼|

| 指令 | 說明 |
|:----:|:----:|
|AT+NAME=名字|設定名稱|
|AT+UART=鮑率,停止位元,同位|設定鮑率|
|AT+ROLE|設定主從|
|AT+RESET|重啟並離開AT模式|
|AT+ORGL|回覆原廠設定|
|AT+PSWD|設定配對密碼|
