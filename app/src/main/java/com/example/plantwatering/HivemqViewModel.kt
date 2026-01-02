package com.example.plantwatering

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import org.json.JSONObject
import java.nio.charset.StandardCharsets


class HivemqViewModel: ViewModel() {

    val host: String = "d19e6a9f4394420ebef984736f1666f0.s1.eu.hivemq.cloud"
    val username: String = "anhngwen"
    val password: String = "Anh2004nv"
    val topic: String = "sensor/data"

    private val _connectionStatus = MutableLiveData<Boolean>(false)
    val connectionStatus: LiveData<Boolean> = _connectionStatus

    private val _pumpStatus = MutableLiveData<Boolean>(false)
    val pumpStatus: LiveData<Boolean> = _pumpStatus

    private val _sensorData = MutableLiveData<SensorData>()
    val sensorData: LiveData<SensorData> = _sensorData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private var client: Mqtt5Client? = null

    init {
        mqttConnect()
        getDataSensor()
    }

    data class SensorData(
        val temperature: Float = 0f,
        val airHumidity: Float = 0f,
        val soilMoisture: Float = 0f,
    )
    private fun mqttConnect() {
        client = Mqtt5Client.builder()
            .identifier("AndroidClient")
            .serverHost(host)
            .serverPort(8883)
            .sslWithDefaultConfig()
            .build()

        try {
            client?.toBlocking()
                ?.connectWith()
                ?.simpleAuth()
                ?.username(username)
                ?.password(password.toByteArray(StandardCharsets.UTF_8))
                ?.applySimpleAuth()
                ?.send()

            _connectionStatus.postValue(true)

            client?.toAsync()?.subscribeWith()
                ?.topicFilter(topic)
                ?.callback { publish ->
                    val message = String(publish.payloadAsBytes, StandardCharsets.UTF_8)
                }
                ?.send()
        } catch (e: Exception) {
            _connectionStatus.postValue(false)
            _errorMessage.postValue("MQTT Connection Failed!")
            e.printStackTrace()
        }
    }

    private fun getDataSensor() {
        try {
            client?.toAsync()?.subscribeWith()
                ?.topicFilter(topic)
                ?.callback { publish ->
                    val message = String(publish.payloadAsBytes, StandardCharsets.UTF_8)
                    val jsonObject = JSONObject(message)
                    val temperature = jsonObject.getDouble("temperature").toFloat()
                    val airHumidity = jsonObject.getDouble("humidity").toFloat()
                    val soilMoisture = jsonObject.getDouble("soil").toFloat()
                    _sensorData.postValue(SensorData(temperature, airHumidity, soilMoisture))
                }
                ?.send()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun publishMessage(topic: String, message: String) {
        try {
            client?.toAsync()?.publishWith()
                ?.topic(topic)
                ?.payload(message.toByteArray(StandardCharsets.UTF_8))
                ?.send()
        } catch (e: Exception) {
            _errorMessage.postValue("Chưa kết nối với Server!")
        }
    }

    private fun mqttDisconnect() {
        client?.toBlocking()?.disconnect()
        _connectionStatus.postValue(false)
    }
}