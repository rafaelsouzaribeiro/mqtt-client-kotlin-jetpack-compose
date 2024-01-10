package com.example.testar

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.MqttTopic
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.nio.charset.Charset

class MqttClientManager(
    private val serverUri: String,
    private val topic: String,
    private val onMessageReceived: (String) -> Unit
) {
    private lateinit var mqttClient: MqttClient

    init {
        connect()
    }

    private fun connect() {
        try {
            mqttClient = MqttClient(serverUri, MqttClient.generateClientId(), MemoryPersistence())
            val options = MqttConnectOptions()
            options.isAutomaticReconnect = true
            options.isCleanSession = false
            options.userName="root"
            options.password="123mudar".toCharArray()
            mqttClient.connect(options)

            mqttClient.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {}

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val receivedMessage = message?.toString()
                    if (!receivedMessage.isNullOrEmpty()) {
                        val utf8String = receivedMessage.toByteArray(Charset.forName("UTF-8"))
                        val convertedString = String(utf8String, Charset.forName("UTF-8"))
                        onMessageReceived.invoke(convertedString)
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })

            mqttClient.subscribe(topic, 0)

        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(message: String) {
        try {
            val mqttTopic: MqttTopic = mqttClient.getTopic(topic)
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttTopic.publish(mqttMessage)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            mqttClient.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}
