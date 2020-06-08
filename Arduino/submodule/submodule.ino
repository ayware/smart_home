#include  <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"

#define CSN_PIN 10
#define CE_PIN 9
#define NRF_DATA_LED 6
#define RELAY_PIN 7
#define POT_PIN A0

#define BLINK_DELAY 50


RF24 alici(CE_PIN, CSN_PIN);
const byte address[6] = "00001";

struct Data {

  int socketValue;
  int ledValue;
  int potValue;

};

void initNrf();

struct Data dataGet;

void setup() {

  Serial.begin(9600);

  pinMode(NRF_DATA_LED, OUTPUT);
  pinMode(RELAY_PIN, OUTPUT);


  initNrf();

}

void loop() {

  if (alici.available()) {

    alici.read(&dataGet, sizeof(dataGet));

    blinkLed(NRF_DATA_LED, BLINK_DELAY);

    if (dataGet.socketValue == 1)
      digitalWrite(RELAY_PIN, HIGH);
    else
      digitalWrite(RELAY_PIN, LOW);


  } else {

    digitalWrite(NRF_DATA_LED, LOW);
  }

}






void blinkLed(int ledPin, long t) {

  digitalWrite(ledPin, HIGH);
  delay(t);
  digitalWrite(ledPin, LOW);
  delay(t);

}
