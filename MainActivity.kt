package com.example.testar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.testar.ui.theme.TestarTheme
import org.eclipse.paho.client.mqttv3.*


class MainActivity : ComponentActivity() {
    private val mqttServerUri = "tcp://x.x.x.x:1883"
    private val mqttTopic = "topic/test"
    private lateinit var mqttClientManager: MqttClientManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val messageState = remember { mutableStateOf("") }
                    MqttView(messageState)
                    mqttClientManager = MqttClientManager(mqttServerUri, mqttTopic) { message ->
                        messageState.value = message
                    }

                    mqttClientManager.publish("Nova mensagem")
                }

            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MqttView(messageState: MutableState<String>) {
        Column {
            TopAppBar(title = { Text(text = "MQTT message") })
            Surface {
                Column {
                    Text(text = "MESSSAGE:")
                    Text(text = messageState.value)
                }
            }
        }
    }
}






