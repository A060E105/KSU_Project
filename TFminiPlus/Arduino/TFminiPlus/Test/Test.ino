/**
  * Test EEPROM
  */

#include <EEPROM.h>

void set_EEPROM_data(int startaddr, String str)
{
	int _size = str.length();
	int i;
	for (i=0; i<_size; i++) {
		EEPROM.write(i,str[i]);
	}
	EEPROM.write(i,'\0');
	EEPROM.end();
}

String get_EEPROM_data(void)
{
	int i = 0;
	char str[20];
	while((EEPROM.read(i) != '\0') && i < 20) {
		str[i] = EEPROM.read(i);
		i++;
	}
	str[i] = '\0';

	return String(str);
}

String get_BT_name(void)
{
	unsigned int i = 0;
	unsigned int j = 0;
	String name;
	String data = get_EEPROM_data();
	while(data[i] != ',')
		i++;
	i++;
	while(data[i] != '\0') {
		name[j] = data[i];
		i++;
		j++;
	}

	return name;
}

int get_dist_value(void)
{
	unsigned int i = 0;
	String value;
	String data = get_EEPROM_data();
	while(data[i] != ',') {
		value = data[i];
	}

	return value.toInt();
}

void setup(void)
{
	Serial.begin(115200);
	Serial.println("Program Start.");
	Serial.print("EEPROM data : ");
	Serial.println(get_EEPROM_data());
}

void loop(void)
{
	int cmd;
	String str;
	if (Serial.available() > 0) {
		cmd = Serial.read();
		switch(cmd) {
			case '0':
				str = Serial.readString();
				Serial.println("Set Bluetooth name");
				Serial.print("new name : ");
				Serial.print(str);
				set_EEPROM_data(0,str);
				break;
			case '1':
				str = Serial.readString();
				Serial.println("Set distance value");
				break;
			case '2':
				Serial.println(get_dist_value());
				break;
			case '3':
				Serial.println(get_BT_name());
				break;
			default:
				Serial.println("Error cmd");
				str = Serial.readString();
		}
	}
}
