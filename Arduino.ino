#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <DHT.h>
#include <WiFiClientSecure.h>
#include <WiFiManager.h>

// --- CẤU HÌNH CHÂN ---
#define AOUT_PIN A0 
#define PUMP D3
#define DHTPIN D4      
#define DHTTYPE DHT11
#define TRIG_PIN D1
#define ECHO_PIN D2

DHT dht(DHTPIN, DHTTYPE);
WiFiClientSecure espClient;
PubSubClient client(espClient);

// --- MQTT CONFIG ---
const char* mqttServer = "7882f49ec5a24abc9c49b6c8332f73e4.s1.eu.hivemq.cloud";
const int mqttPort = 8883;                    
const char* mqttClientID = "ESP8266_SmartGarden_V3";   
const char* mqttUser = "hayson";       
const char* mqttPassword = "Alo123,./"; 

// --- BIẾN TOÀN CỤC & CẤU HÌNH MẶC ĐỊNH ---
unsigned long lastPublishTime = 0;
int systemMode = 0;          // 0: Manual, 1: Auto Soil
bool isPumpRunning = false;  // Trạng thái thực của bơm
String warningMsg = "OK";    // Tin nhắn cảnh báo gửi lên App

// Các biến cấu hình (Sẽ được update từ App)
int soilThreshold = 30;      // Ngưỡng độ ẩm đất để bơm (%)
int tankHeight = 100;        // Khoảng cách từ cảm biến đến đáy (cm) - Mức 0%
int waterOffset = 4;         // Khoảng cách từ cảm biến đến mặt nước lúc đầy (cm) - Mức 100%
int minWaterPercent = 10;    // Dưới mức này KHÔNG CHO BƠM (%)

// Biến đo đạc
int soilPercent = 0;
int waterPercent = 0;
float temp = 0;
float humi = 0;

void setup() {
  Serial.begin(115200);
  pinMode(PUMP, OUTPUT);
  digitalWrite(PUMP, HIGH); // Tắt bơm (Relay kích Low thì High là tắt)
  
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);

  dht.begin();
  setup_wifi();
  
  espClient.setInsecure();
  client.setServer(mqttServer, mqttPort);
  client.setCallback(callback);
}

void setup_wifi() {
  WiFi.mode(WIFI_STA);
  WiFiManager vm;
  if(!vm.autoConnect("IOT_GARDEN_MASTER")) {
      ESP.restart();
  } 
  Serial.println("WiFi Connected!");
}

// --- HÀM ĐO KHOẢNG CÁCH & TÍNH % NƯỚC ---
void measureWaterLevel() {
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);

  long duration = pulseIn(ECHO_PIN, HIGH, 30000); 
  if (duration == 0) {
    waterPercent = -1; // Lỗi cảm biến
    return;
  }
  
  int distance = duration * 0.034 / 2; 

  // Công thức tính % nước chuẩn xác có tính khoảng hở (Offset)
  // distance càng gần tankHeight -> nước càng cạn (0%)
  // distance càng gần waterOffset -> nước càng đầy (100%)
  
  if (distance >= tankHeight) waterPercent = 0;
  else if (distance <= waterOffset) waterPercent = 100;
  else {
    // Map ngược: distance nhỏ -> % lớn
    waterPercent = map(distance, tankHeight, waterOffset, 0, 100);
  }
  waterPercent = constrain(waterPercent, 0, 100);
}

// --- HÀM ĐIỀU KHIỂN BƠM AN TOÀN (CỐT LÕI) ---
void setPumpState(bool state) {
  // state = true (Muốn BẬT), state = false (Muốn TẮT)
  
  if (state == true) {
    // Kiểm tra an toàn trước khi bật
    if (waterPercent >= 0 && waterPercent < minWaterPercent) {
      digitalWrite(PUMP, HIGH); // Cưỡng chế tắt
      isPumpRunning = false;
      warningMsg = "LOW_WATER"; // Cờ báo lỗi
      Serial.println("CẢNH BÁO: Hết nước!");
    } else {
      digitalWrite(PUMP, LOW); // Relay kích mức thấp để BẬT
      isPumpRunning = true;
      warningMsg = "OK";
      Serial.println("PUMP ON");
    }
  } else {
    digitalWrite(PUMP, HIGH); // Tắt
    isPumpRunning = false;
    Serial.println("PUMP OFF"));
  }
}

// --- XỬ LÝ LỆNH TỪ APP ---
void callback(char* topic, byte* payload, unsigned int length) {
  String message = "";
  for (int i = 0; i < length; i++) message += (char)payload[i];
  String strTopic = String(topic);
  
  Serial.println("Msg: " + strTopic + " -> " + message);

  // 1. Điều khiển Bơm (Chỉ dùng cho Manual Mode)
  if (strTopic == "pump/control") {
    if (systemMode == 0) {
      if (message == "on") setPumpState(true);
      else if (message == "off") setPumpState(false);
    }
  }
  // 2. Các cài đặt (App gửi xuống)
  else if (strTopic == "settings/mode") systemMode = message.toInt();
  else if (strTopic == "settings/soil_threshold") soilThreshold = message.toInt();
  else if (strTopic == "settings/tank_height") tankHeight = message.toInt();
  else if (strTopic == "settings/water_offset") waterOffset = message.toInt();
  else if (strTopic == "settings/min_water") minWaterPercent = message.toInt();
  
  // Cập nhật lại ngay trạng thái bơm nếu settings thay đổi ảnh hưởng an toàn
  if (isPumpRunning) setPumpState(true); 
}

void reconnect_mqtt() {
  while (!client.connected()) {
    if (client.connect(mqttClientID, mqttUser, mqttPassword)) {
      client.subscribe("pump/control");
      client.subscribe("settings/#"); // Đăng ký tất cả topic bắt đầu bằng settings/
    } else {
      delay(5000);
    }
  }
}

void loop() {
  if (!client.connected()) reconnect_mqtt();
  client.loop(); 
  
  unsigned long now = millis();
  if (now - lastPublishTime >= 2000) { // Cập nhật mỗi 2 giây
    lastPublishTime = now;

    // 1. Đọc cảm biến
    int rawSoil = analogRead(AOUT_PIN);
    soilPercent = map(rawSoil, 800, 370, 0, 100);
    soilPercent = constrain(soilPercent, 0, 100);
    
    temp = dht.readTemperature();
    humi = dht.readHumidity();
    
    measureWaterLevel(); // Cập nhật waterPercent

    // 2. Logic Tự động (Chỉ chạy khi Mode = 1)
    if (systemMode == 1) {
      if (soilPercent < soilThreshold) setPumpState(true);
      else setPumpState(false);
    }
    
    // 3. Logic Bảo vệ liên tục (Dù mode nào cũng kiểm tra)
    // Nếu bơm đang chạy mà nước tụt đột ngột -> NGẮT NGAY
    if (isPumpRunning && waterPercent < minWaterPercent) {
       setPumpState(false); // Hàm này sẽ tự update warningMsg = "LOW_WATER"
    }

    // 4. Gửi dữ liệu JSON lên App
    // JSON bao gồm cả trạng thái bơm thực tế và cảnh báo
    String json = "{";
    json += "\"temp\":" + String(isnan(temp)?0:temp) + ",";
    json += "\"humi\":" + String(isnan(humi)?0:humi) + ",";
    json += "\"soil\":" + String(soilPercent) + ",";
    json += "\"water\":" + String(waterPercent) + ",";
    json += "\"pumpState\":\"" + String(isPumpRunning ? "ON" : "OFF") + "\",";
    json += "\"warning\":\"" + warningMsg + "\""; 
    json += "}";
    
    client.publish("sensor/data", json.c_str());
  }
}