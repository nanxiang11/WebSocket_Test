package com.example.ws;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/{username}")
@Component
public class Myws {

    private static Map<String, Myws> webSocketSet = new ConcurrentHashMap<String, Myws>();
    private static Map<String, Session> map = new HashMap<String, Session>();
    private static List<String> namelist = new ArrayList<String>();
    private static JSONObject jsonObject = new JSONObject();
    private static JSONObject jsonObject2 = new JSONObject();
    private static List<String> nm_msg = new ArrayList<String>();

    private Session session;
    private String name;

    @OnOpen
    public void onpen(Session session, @PathParam(value = "username") String username){
        if(username == null){
            username = "游客";
        }
        this.session = session;
        this.name = "南" + getname();  // 随机名字测试用
//        this.name = username;

        webSocketSet.put(name, this);
        map.put(username, session);

        namelist.clear();  // 清空原来的信息
        setonlion();
        jsonObject.put("onlinepp", namelist);
        String message = jsonObject.toString();
        broadcast2(message);

    }

    @OnClose
    public void onclose(){
        webSocketSet.remove(this.name);  // 移除对象

        namelist.clear();
        setonlion();
        jsonObject.clear();

        jsonObject.put("onlinepp", namelist);
        String message = jsonObject.toString();
        broadcast3(message);
    }

    @OnMessage
    public void onmessage(String message){

        String[] message2 = message.split("-");
        String type = message2[0];
        String newmessage = message2[1];
        String tousername = message2[2];

        if(type.equals("q")){
            nm_msg.clear();
            jsonObject2.clear();

            nm_msg.add(name);
            nm_msg.add(newmessage);
            nm_msg.add("q");  // 判断
            nm_msg.add(tousername);  // 占位置防止报错
            jsonObject2.put("chat", nm_msg);
            String message3 = jsonObject2.toString();
            broadcast(message3);
        }else if(type.equals("s")){
            nm_msg.clear();
            jsonObject2.clear();

            nm_msg.add(name);
            nm_msg.add(newmessage);
            nm_msg.add("s");
            nm_msg.add(tousername);

            jsonObject2.put("chat", nm_msg);
            String message3 = jsonObject2.toString();
            broadcast4(message3, tousername);
        }

    }


    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    public void broadcast(String message){
        for (Map.Entry<String, Myws> item : webSocketSet.entrySet()){
            item.getValue().session.getAsyncRemote().sendText(message);
        }
    }

    public void broadcast2(String message){
        for (Map.Entry<String, Myws> item : webSocketSet.entrySet()){
            item.getValue().session.getAsyncRemote().sendText(message);
        }
    }

    public void broadcast3(String message){
        for (Map.Entry<String, Myws> item : webSocketSet.entrySet()){
            if (!item.getKey().equals(name)){
                item.getValue().session.getAsyncRemote().sendText(message);
            }
        }
    }

    public void broadcast4(String message, String tousername){  // 私聊
        webSocketSet.get(tousername).session.getAsyncRemote().sendText(message);  // 接收者
        webSocketSet.get(name).session.getAsyncRemote().sendText(message);  // 发送者
    }

    public void setonlion(){
        for (Map.Entry<String, Myws> item : webSocketSet.entrySet()){
                namelist.add(item.getKey());
        }
    }

    public String getname() {
        String linkNo = "";
        // 用字符数组的方式随机
        String model = "小大天明画美丽豪子余多少浩然兄弟朋友美韵紫萱好人坏蛋误解不要停栖栖遑遑可";
        char[] m = model.toCharArray();
        for (int j = 0; j < 2; j++) {
            char c = m[(int) (Math.random() * 36)];
            // 保证六位随机数之间没有重复的
            if (linkNo.contains(String.valueOf(c))) {
                j--;
                continue;
            }
            linkNo = linkNo + c;
        }
        return linkNo;
    }


}
