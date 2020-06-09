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

void set_dist_value(int value)
{
	String name = get_BT_name();
	String str = value + "," + name;
	set_EEPROM_data(0,str);
}

void set_BT_name(String name)
{
	int value = get_dist_value();
	String str = value + "," + name;
	set_EEPROM_data(0,str);
}

void setup(void)
{
	Serial.begin(115200);
	// EEPROM.begin(20);
	Serial.println("Program Start.");
	// Serial.print("EEPROM data : ");
	// Serial.println(get_EEPROM_data());
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
				set_dist_value(str.toInt());
				break;
			case '2':
				str = Serial.readString();
				Serial.println("Set BlueTooth name");
				set_BT_name(str);
				break;
			case '3':
				str = Serial.readString();
				Serial.println("Get distance value");
				Serial.println(get_dist_value());
				break;
			case '4':
				str = Serial.readString();
				Serial.println("Get BlueTooth name");
				Serial.println(get_BT_name());
				break;
			case '5':
				str = Serial.readString();
				Serial.println("Get EEPROM data");
				Serial.println(get_EEPROM_data());
				break;
			default:
				Serial.println("Error cmd");
				str = Serial.readString();
		}
	}
}
