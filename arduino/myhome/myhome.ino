#include <SoftwareSerial.h>

SoftwareSerial wifi(3, 2);

#define portsLength 3

struct Port {
  byte id;
  bool isOut;
  bool isSwitch;
  byte value;
};

struct Order {
  byte port;
  byte value;
};

Port ports[portsLength] = {
  Port {LED_BUILTIN, true, true, 0}, 
  Port {A1, false, false, 0}, 
  Port {14, true, true, 1}
};
Order o;
bool success;
byte i;
byte newRead;

byte intLength(byte n) {
  if (n < 10) return 1;
  else if (n < 100) return 2;
  else if (n < 1000) return 3;
  else return 4;
}

void writePorts() {
  for (i = 0; i < portsLength; i++) {
    wifi.print(i);
    wifi.print(',');
    wifi.print(ports[i].isOut);
    wifi.print(',');
    wifi.print(ports[i].isSwitch);
    wifi.print(',');
    wifi.print(ports[i].value);
    if (i < portsLength - 1) wifi.print(';');
  }
  wifi.print("\n\r");
}

bool readOrder() {
  char p[2];
  char v[3];

  char c;
  for (i = 0; true; i++) {
    while (!wifi.available());
    c = wifi.read();
    if (isdigit(c)) p[i] = c;
    else {
      p[i] = '\0';
      break;
    }
  }
  for (i = 0; true; i++) {
    while (!wifi.available());
    c = wifi.read();
    if (isdigit(c)) v[i] = c;
    else {
      v[i] = '\0';
      break;
    }
  }
  o.port = atoi(p);
  o.value = atoi(v);
  return true;
}

void setup() {
  Serial.begin(9600);
  while (!Serial);

  wifi.begin(9600);
  for (i = 0; i < portsLength; i++) {
    if (ports[i].isSwitch)
      if (ports[i].isOut)
        pinMode(ports[i].id, OUTPUT);
      else
        pinMode(ports[i].id, INPUT);
  }
  Serial.println("Opening socket...");
  wifi.print("AT+CIPSTART=\"TCP\",\"192.168.43.81\",9000\r\n");
  while (!wifi.find("OK"));
  Serial.println("connection OK");
}


bool waitingResponse = false;
bool firstTime = true;

void loop() {
  if (waitingResponse) {
    if (wifi.available() && wifi.read() != '>') {
      writePorts();
      waitingResponse = false;
    } else return;
  }

  bool changed = false;

  for (i = 0; i < portsLength; i++) {
    if (!ports[i].isOut) {
      if (ports[i].isSwitch)
        newRead = digitalRead(ports[i].id);
      else
        newRead = analogRead(ports[i].id);

      if (newRead != ports[i].value) {
        Serial.println("different read!");
        changed = true;
        ports[i].value = newRead;
      }
    }
    else {
      if (ports[i].isSwitch)
        digitalWrite(ports[i].id, ports[i].value);
      else
        analogWrite(ports[i].id, ports[i].value);
    }
  }



  if (wifi.available() && wifi.find("+IPD,") && wifi.find(":")) {
    bool success = readOrder();
    if (ports[o.port].isOut && ports[o.port].value != o.value) {
      changed = true;
      ports[o.port].value = o.value;
    }
  }

  if (changed || firstTime) {
    byte length;
    for (i = 0; i < portsLength; i++) {
      length += intLength(i) + intLength(ports[i].value);
    }
    length += 5 * portsLength + portsLength; //portsLength - 1 punti e virgola
    wifi.print(F("AT+CIPSEND="));
    wifi.print(length);
    wifi.print(F("\r\n"));
    waitingResponse = true; 
    changed = false;
    firstTime = false;
  }
}
