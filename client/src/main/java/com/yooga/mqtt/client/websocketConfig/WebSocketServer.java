package com.yooga.mqtt.client.websocketConfig;

import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by yooga on 2018/10/11.
 * ServerEndpoint自定义访问路径
 */
@ServerEndpoint("/websocket")
@Component
public class WebSocketServer {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。可选
    private static int onlineCount = 0;




    public WebSocketServer() throws AWTException {

    }

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。必须
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据。必须
    private Session session;



    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新窗口开始监听,当前在线人数为" + getOnlineCount());
        try {
//           sendMessage("连接成功");
        } catch (Exception e) {
            System.out.println("WebSocket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println("有连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
            //群发消息
            for (WebSocketServer item : webSocketSet) {
                try {
                    item.sendMessage("连接开始");
                } catch (IOException ioe) {
                    System.out.println("连接断开");
                }
            }
        }


    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message) throws IOException {
        System.out.println("推送消息内容:" + message);
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }


}