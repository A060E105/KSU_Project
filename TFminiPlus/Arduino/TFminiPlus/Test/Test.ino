/**
  * Test EEPROM
  */

#include <EEPROM.h>

enum CMD {SET_EEPROM_DATA, GET_DISTANCE_VALUE, GET_BT_NAME, SET_DIST_VALUE, SET_BT_NAME, GET_EEPROM_DATA};

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
	char name[20];
	String data = get_EEPROM_data();
	while(data[i] != ',')
		i++;
	i++;
	while(data[i] != '\0') {
		name[j] = data[i];
		i++;
		j++;
	}
	name[j] = '\0';

	return String(name);
}

int get_dist_value(void)
{
	unsigned int i = 0;
	char value[5];
	String data = get_EEPROM_data();
	while(data[i] != ',') {
		value[i] = data[i];
		i++;
	}
	value[i] = '\0';


	return atoi(value);
}

void set_dist_value(String value)
{
	String name = get_BT_name();
	String str = value + "," + name;
	Serial.println(str);
	set_EEPROM_data(0,str);
}

void set_BT_name(String name)
{
	int value = get_dist_value();
	String str = String(value) + "," + name;
	Serial.println(str);
	set_EEPROM_data(0,str);
}

void setup(void)
{
	Serial.begin(115200);
	Serial.println("Program Start.");
}

void loop(void)
{
	int cmd;
	String str;
	if (Serial.available() > 0) {
		cmd = Serial.read() - '0';
		switch(cmd) {
			case SET_EEPROM_DATA:
				str = Serial.readString();
				Serial.println("Set Bluetooth name");
				Serial.print("new name : ");
				Serial.print(str);
				set_EEPROM_data(0,str);
				break;
			case SET_DIST_VALUE:
				str = Serial.readString();
				Serial.println("Set distance value");
				set_dist_value(str);
				break;
			case SET_BT_NAME:
				str = Serial.readString();
				Serial.println("Set BlueTooth name");
				set_BT_name(str);
				break;
			case GET_DISTANCE_VALUE:
				str = Serial.readString();
				Serial.println("Get distance value");
				Serial.println(get_dist_value());
				break;
			case GET_BT_NAME:
				str = Serial.readString();
				Serial.println("Get BlueTooth name");
				Serial.println(get_BT_name());
				break;
			case GET_EEPROM_DATA:
				str = Serial.readString();
				Serial.println("Get EEPROM data");
				Serial.println(get_EEPROM_data());
				break;
			default:
				str = Serial.readString();
				Serial.println("Error cmd");
		}
	}
}
