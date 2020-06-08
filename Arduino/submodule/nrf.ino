


void initNrf(){

  alici.begin();
  alici.openReadingPipe(1,address);
  
  alici.startListening();
  
}
