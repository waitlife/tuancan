package com.example.tuancan.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author xiaoqianyong
 * @description
 * @create 2019-03-12-13:08
 */
@Slf4j
@Component
@ServerEndpoint(value = "/tc/websocket/{userId}")
public class MyWebSocket {
    /**
     *   与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static CopyOnWriteArraySet<MyWebSocket> copyOnWriteArraySet=new CopyOnWriteArraySet<>();


    private  String userClientId;
    /**
     * 打开；连接调用方法
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session){
        this.session=session;
        this.userClientId=userId;
        copyOnWriteArraySet.add(this);

        log.info("有新的连接加入！"+session);
        //发送消息
    }

    /**
     * 连接关闭调用方法
     */
    @OnClose
    public void onClose(){
        copyOnWriteArraySet.remove(this);
        log.info("关闭连接");

    }

    /**
     * 收到客户端发过来的消息
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message,Session session){
        log.info("来自客户端的消息" + message);

        //TODO 从数据库查询未读历史消息并发送
    }


    @OnError
    public void onError(Session session,Throwable error){
        log.info("发生错误：");
        error.printStackTrace();
    }

    public void sendSingleMessage(String message,String sessionId) throws Exception{
        for (MyWebSocket socket : copyOnWriteArraySet) {
            if (sessionId.equals(socket.getUserClientId())){
                socket.sendMessage(message);
                break;
            }
        }
    }
    /**
     * f发送消息
     * @param message
     * @throws Exception
     */
    public void sendMessage(String message) throws Exception{
        this.session.getBasicRemote().sendText(message);
    }

    public  void sendInfo(String message) throws Exception{
        for (MyWebSocket myWebSocket : copyOnWriteArraySet) {
            try {
                myWebSocket.sendMessage(message);
            }catch (Exception e){
                continue;
            }
        }

    }

    public String getUserClientId() {
        return userClientId;
    }

    public void setUserClientId(String userClientId) {
        this.userClientId = userClientId;
    }
}



