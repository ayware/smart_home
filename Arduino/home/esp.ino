String WIFI_NAME = "Superbox_Yazici";
String WIFI_PASSWORD = "86005YAZICI";

void initEsp() {
  Serial.println("--- initEsp() ---");

  espSerial.begin(115200);    // Esp8266 haberleşmesini başlatıyoruz
  Serial.println("Esp serial begin at 115200.");

  sendEspCommand("AT+RST", (char*)"OK", 3);

}

void setupWifiConnection() {

  Serial.println("--- setupWifiConnection() ---");

  if (connectedEsp()) {

    Serial.println("Wi-Fi STA modu ayarlaniyor...");

    // Wi-Fi modunu STA olarak ayarlıyoruz.
    if (!sendEspCommand("AT+CWMODE=1", (char*)"OK", 2))
    {
      Serial.println("Wi-Fi STA modu ayarlanamadı!");
      return;
    }

    Serial.println("Wi-Fi STA ayarlandi.");

    String connectionCommand = wifiConnectionCommand(WIFI_NAME, WIFI_PASSWORD);

    Serial.println("Wi-Fi baglaniliyor: " + WIFI_NAME + "," + WIFI_PASSWORD);

    // Wi-Fi bağlantısı yapılmaya çalışılıyor
    if (!sendEspCommand(connectionCommand, (char*)"OK", 5))
    {
      Serial.println("Wi-Fi baglanilamadi!");
      return;
    }

    Serial.println("Wi-Fi baglanildi: " + WIFI_NAME + "," + WIFI_PASSWORD);

  }

}


String wifiConnectionCommand(String wifiName, String password)
{
  return "AT+CWJAP=\"" + wifiName + "\",\"" + password + "\"";
}



bool connectedEsp() {
  return sendEspCommand("AT", (char*)"OK", 3);

}



bool sendEspCommand(String command, char* response, int count) {

  Serial.println("--- sendEspCommand(" + command + "," + String(response) + "," + String(count) + ")"  );

  bool sent = false;
  int c = 1;

  while ( (c++) <= count) {

    blinkLed(ESP_CONNECTION_LED, BLINK_DELAY);

    espSerial.println(command);
    if (espSerial.find(response)) {
      sent = true;
      break;
    }

  }

  return sent;

}

bool sendEspData(struct Data data) {

  Serial.println("-- - sendEspData(data) -- -");

  bool sent = false;

  // Thingspeak server üzerinden TCP bağlantısı açıyoruz.
  String tcp = "AT + CIPSTART = \"TCP\",\"" + String(HOST) + "\"," + String(PORT);

  Serial.println("TCP: " + tcp);


  if (!sendEspCommand(tcp, (char*)"CONNECT", 5)) {

    return false;

  }

  // Thingspeak üzerine yollacak bilgiler ayarlanıyor.
  String postRequest = "GET "
                       + String("/update")
                       + "?api_key=" + String(API)
                       + "&" + String(LED_VALUE_FIELD) + "=" + String(data.ledValue)
                       + "&" + String(SOCKET_VALUE_FIELD) + "=" + String(data.socketValue)
                       + "&" + String(SENSOR_VALUE_FIELD) + "=" + String(data.sensorValue)
                       + "\r\n";

  Serial.println("Request: " + postRequest);


  String cip = "AT+CIPSEND=" + String(postRequest.length());


  if (sendEspCommand(cip, (char*)">", 5))
  {


    if (sendEspCommand(postRequest, (char*)"SEND OK", 5))
    {

      sent = true;
      blinkLed(ESP_DATA_LED, BLINK_DELAY);

    }

  }

  return sent;

}


void getEspData(struct Data* data) {

  Serial.println("--- getEspData(data) ---");

  String tcp = "AT+CIPSTART=\"TCP\",\"" + String(HOST) + "\"," + String(PORT);

  Serial.println("TCP: " + tcp);


  if (!sendEspCommand(tcp, (char*)"CONNECT", 5)) {

    return;

  }


  // Thingspeak server üzerinden verileri çekmek için istekte bulunuyoruz.
  String getRequest = "GET "
                      + String("/channels/1026699/feeds.json?results=1")
                      + "\r\n";

  Serial.println("Request: " + getRequest);

  String cip = "AT+CIPSEND=" + String(getRequest.length());


  if (sendEspCommand(cip, (char*)">", 5))
  {

    if (sendEspCommand(getRequest, (char*)"OK", 5))
    {


      int c = 0;
      while (espSerial.available() == 0) {

        if ( (c++) == 10)
          break;
      }

      String dataGot = espSerial.readString();
      dataGot = dataGot.substring(dataGot.indexOf(":") + 1);

      DynamicJsonDocument doc(1024);
      DeserializationError err = deserializeJson(doc, dataGot);

      if (!err) {

        data->socketValue = doc["feeds"][0][SOCKET_VALUE_FIELD];
        data->ledValue = doc["feeds"][0][LED_VALUE_FIELD];
        data->sensorValue = doc["feeds"][0][SENSOR_VALUE_FIELD];

        blinkLed(ESP_DATA_LED, BLINK_DELAY);

      }


    }

  }

}
