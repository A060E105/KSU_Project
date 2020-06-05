/**
  * Test EEPROM
  */

#include <EEPROM.h>

void writeString(int startaddr, String str)
{
	int _size = str.length();
	int i;
	for (i=0; i<_size; i++) {
		EEPROM.write(i,str[i]);
	}
	EEPROM.write(i,'\0');
	EEPROM.end();
}

String readString(void)
{
	int i = 0;
	char str[20];
	while(EEPROM.read(i) != '\0') {
		str[i] = EEPROM.read(i);
		i++;
	}
	str[i] = '\0';

	return String(str);
}

void setup(void)
{
	Serial.begin(115200);
	Serial.println("Program Start.");
	EEPROM.begin(20);
	Serial.println(readString());
}

void loop(void)
{
	String str;
	if (Serial.available() > 0) {
		str = Serial.readString();
		Serial.print(str);
		writeString(0,str);
	}

}
