


void initNrf(){

  alici.begin();
  alici.setPALevel(RF24_PA_LOW);
  alici.openReadingPipe(1,address);
  alici.startListening();
  
}
