#include <ArduinoJson.h>
#include <SPI.h>
#include "RF24.h"
#include "nRF24L01.h"
#include <SoftwareSerial.h>


// --- Esp8266 için kullanılan pin tanımlamaları -----
#define ESP_DATA_LED 5
#define ESP_CONNECTION_LED 4
#define ESP_RX 3
#define ESP_TX 2
// --- Esp8266 için kullanılan pin tanımlamaları -----


// --- nRF24L01 için kullanılan pin tanımlamaları -----
#define RF_DATA_LED 6 
#define RF_CE 9
#define RF_CSN 10
// --- nRF24L01 için kullanılan pin tanımlamaları -----

// --- Thingspeak server bilgiler -----
#define API "ZE2AKHGCQ7RY15AA"
#define HOST "api.thingspeak.com"
#define PORT 80
// --- Thingspeak server bilgiler -----


// ---- Thingspeak üzerindeki alanlara karşılık gelen değişkenler --------
#define SOCKET_VALUE_FIELD "field1"
#define SENSOR_VALUE_FIELD "field2"
#define LED_VALUE_FIELD "field3"
// ---- Thingspeak üzerindeki alanlara karşılık gelen değişkenler --------

#define BLINK_DELAY 50

SoftwareSerial espSerial(ESP_TX, ESP_RX);


// Projemizde kullanılan veri yapısı
struct Data {

  int socketValue;
  int sensorValue;
  int ledValue;

};

struct Data dataSend, dataGet;

//- Prototypes ------

void initEsp(); // Esp8266 ayarlaması yapar
void setupWifiConnection(); // Esp8266 Wi-Fi kurulumu yapar
bool connectedEsp(); // Esp8266 ile Arduino arasındaki bağlantıyı kontrol eder
bool sendEspCommand(String command, char* response, int count); // Arduino üzerinden Esp8266'ya AT komutları yollar
void blinkLed(int ledPin, long t); // Belirtilen led üzerinde uyarı ışığı yakıp söndürür
String wifiConnectionCommand(String wifiName, String password); // Verilen Wi-Fi adı ve şifresi kullanarak AT komut ifadesi döndürür
bool sendEspData(struct Data); // Esp8266 üzerinden Thingspeak üzerine verileri yollar
void getEspData(struct Data*); // Esp8266 kullanarak Thingspeak üzerinden verileri çeker


// -----------------------------

void setup() {
  Serial.println("--- setup() ---");

  Serial.begin(9600);

  // Esp ve nRF24L01 için bilgi ledleri ayarlanıyor.
  pinMode(ESP_CONNECTION_LED, OUTPUT);
  pinMode(ESP_DATA_LED, OUTPUT);
  pinMode(RF_DATA_LED, OUTPUT);

  initEsp(); // Esp ayarları yapılıyor
  initRf(); // nRF24L01 ayarları yapılıyor

}


void loop() {

  // Esp8266 ile bağlantı kontrol ediliyor ve bağlantı mevcut ise veri çekme ve yollama işlemi yapılıyor
  if (connectedEsp()) {

    getEspData(&dataGet); // Thingspeak üzerinde verileri dataGet değişkenine yüklüyoruz
    sendRfData(&dataGet); // dataGet değişkeni üzerindeki bilgiler diğer devrelere iletiliyor

  } else {


    // Esp8266 ile bağlantı olmadığı için bilgi ledleri kapalı konumda
    digitalWrite(ESP_CONNECTION_LED, LOW);
    digitalWrite(ESP_DATA_LED, LOW);

    // Esp8266 ile bağlantı sağlanamadığı zaman bağlantı sağlanmaya çalışıyor.
    setupWifiConnection();

  }



}



void blinkLed(int ledPin, long t) {

  digitalWrite(ledPin, HIGH);
  delay(t);
  digitalWrite(ledPin, LOW);
  delay(t);

}
