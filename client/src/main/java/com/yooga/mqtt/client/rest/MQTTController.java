package com.yooga.mqtt.client.rest;

import com.yooga.mqtt.client.Gateway.MqttGateway;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MQTTController {


    private final MqttGateway mqttGateway;

    public MQTTController(MqttGateway mqttGateway) {
        this.mqttGateway = mqttGateway;
    }

    @GetMapping("/sendMqtt/{data}/{topic}")
    public String sendMqtt(@PathVariable(value = "data") String  data,
                           @PathVariable(value = "topic") String  topic){
        mqttGateway.sendToMqtt(data,topic);
        return "OK";
    }





}
