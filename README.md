# MQTT
MQTT物联网


# 使用Arduino uno r3 + esp6288 + activemq(mqtt) + websocket
### 1.client包是springboot java代码,服务端websocket，与mqtt客户端
### 2.activemq(mqtt)可自行下载安装
### 3.以下是Arduino代码
``` 
#include <WiFiEspClient.h>
#include <WiFiEsp.h>
#include <WiFiEspUdp.h>
#include <PubSubClient.h>
#include "SoftwareSerial.h"

#define WIFI_AP "1008"
#define WIFI_PASSWORD "jw1008.?"

// DHT
#define DHTPIN 4
#define DHTTYPE DHT11

char MqttServer[] = "192.168.1.104";

// 初始化以太网客户端对象 -- WiFiEspClient.h
WiFiEspClient espClient;

// 初始化MQTT库PubSubClient.h的对象
PubSubClient client(espClient);

SoftwareSerial soft(10, 11); // RX, TX

int status = WL_IDLE_STATUS;
unsigned long lastSend;

void setup() {

  Serial.begin(115200);
  
  InitWiFi();                                // 连接WiFi
  client.setServer( MqttServer, 1883 );      // 连接WiFi之后，连接MQTT服务器
  
  lastSend = 0;
}

void loop() {
  status = WiFi.status();
  if ( status != WL_CONNECTED) {
    while ( status != WL_CONNECTED) {
      Serial.print("[loop()]Attempting to connect to WPA SSID: ");
      Serial.println(WIFI_AP);
      // 连接WiFi热点
      status = WiFi.begin(WIFI_AP, WIFI_PASSWORD);
      delay(500);
    }
    Serial.println("[loop()]Connected to AP");
  }

  if ( !client.connected() ) {
    reconnect();
  }

  if ( millis() - lastSend > 1000 ) { // 用于定时1秒钟发送一次数据
    getAndSendTemperatureAndHumidityData(); // 获取温湿度数据发送到MQTT服务器上去
    lastSend = millis();
  }

  client.loop();
}

/*
 * 
 * 读取温湿度数据，然后发送到MQTT服务器上去
 * 
 */
void getAndSendTemperatureAndHumidityData()
{
  Serial.println("Collecting temperature data.");


  // 构建一个 JSON 格式的payload的字符串
  String payload = "{";
  payload += "\"temperature\":"; payload += ",";
  payload += "\"humidity\":";
  payload += "}";

  // Send payload
  char attributes[100];
  payload.toCharArray( attributes, 100 );
  
  // boolean publish(const char* topic, const char* payload);
  
  client.publish( "myTopic", attributes );
  Serial.print("[publish]-->>");
  Serial.println( attributes );
}

void InitWiFi()
{
  // 初始化软串口，软串口连接ESP模块
  soft.begin(115200);
  // 初始化ESP模块
  WiFi.init(&soft);
  // 检测WiFi模块在不在，宏定义：WL_NO_SHIELD = 255,WL_IDLE_STATUS = 0,
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    while (true);
  }

  Serial.println("[InitWiFi]Connecting to AP ...");
  // 尝试连接WiFi网络
  while ( status != WL_CONNECTED) {
    Serial.print("[InitWiFi]Attempting to connect to WPA SSID: ");
    Serial.println(WIFI_AP);
    // Connect to WPA/WPA2 network
    status = WiFi.begin(WIFI_AP, WIFI_PASSWORD);
    delay(500);
  }
  Serial.println("[InitWiFi]Connected to AP");
}

/**
 * 
 * MQTT客户端断线重连函数
 * 
 */

void reconnect() {
  // 一直循环直到连接上MQTT服务器
  while (!client.connected()) {
    Serial.print("[reconnect]Connecting to MQTT Server ...");
    // 尝试连接connect是个重载函数 (clientId, username, password)
    if ( client.connect("clientId","admin","admin") ) {
      Serial.println( "[DONE]" );
    } else {
      Serial.print( "[FAILED] [ mqtt connect error code = " );
      Serial.print( client.state() );
      Serial.println( " : retrying in 5 seconds]" );// Wait 5 seconds before retrying
      delay( 5000 );
    }
  }
}
``` 

