
RF24 radio(RF_CE, RF_CSN);

const byte address[6] = {"00001"};

void initRf() {

  radio.begin();
  radio.setPALevel(RF24_PA_LOW);
  radio.openWritingPipe(address);
  //radio.openReadingPipe(1, address);

}

void getRfData(struct Data* data) {

  radio.startListening();

  if (radio.available()) {

    while (radio.available()) {

      radio.read(data, sizeof(data));

    }

  }

}


void sendRfData(struct Data* data) {

  radio.stopListening();
  radio.write(data, sizeof(data));
  blinkLed(RF_DATA_LED, BLINK_DELAY);

}
