package com.example.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import Result.Result;
import Result.*;
import java.io.IOException;

public class LoginTask implements Runnable {
    private String json = null;
    private String host = null;
    private String port = null;
    private String requestType = null;
    private Handler messageHandler = null;

    public LoginTask(String json, String host, String port, String requestType, Handler messageHandler) {
        this.json = json;
        this.host = host;
        this.port = port;
        this.requestType = requestType;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        String isSuccessful;
        String urlString = "http://" + host + ":" + port + "/user/" + requestType;
        try {
            isSuccessful = ServerProxy.request(json, urlString, "", "POST");
            Result postResult = Serializer.deserialize(isSuccessful, LoginRegisterResult.class);
            if (isSuccessful.matches("") || !postResult.isSuccess()) {
                sendMessage("LOGIN FAILED", "LOGIN FAILED");
            }
            else {
                isSuccessful = ServerProxy.request("", "http://" + host + ":" + port + "/person",
                        ((LoginRegisterResult)postResult).getAuthtoken(), "GET");
                if (isSuccessful.matches("")) {
                    sendMessage("LOGIN FAILED", "LOGIN FAILED");
                }
                else {
                    PersonFamilyResult personFamilyResult = Serializer.deserialize(isSuccessful, PersonFamilyResult.class);
                    isSuccessful = ServerProxy.request("", "http://" + host + ":" + port + "/event",
                            ((LoginRegisterResult) postResult).getAuthtoken(), "GET");
                    if (isSuccessful.matches("")) {
                        sendMessage("LOGIN FAILED", "LOGIN FAILED");
                    }
                    else {
                        EventFamilyResult eventFamilyResult = Serializer.deserialize(isSuccessful, EventFamilyResult.class);
                        isSuccessful = ServerProxy.request("", "http://" + host + ":" + port + "/person/" +
                                        ((LoginRegisterResult) postResult).getPersonID(),
                                ((LoginRegisterResult) postResult).getAuthtoken(), "GET");
                        if (isSuccessful.matches("")) {
                            sendMessage("LOGIN FAILED", "LOGIN FAILED");
                        }
                        else {
                            PersonSingleResult personSingleResult = Serializer.deserialize(isSuccessful, PersonSingleResult.class);
                            DataCache dataCache = DataCache.getInstance();
                            dataCache.resetInstance();
                            dataCache.setUser(personSingleResult);
                            dataCache.parsePeople(personFamilyResult);
                            dataCache.parseEvents(eventFamilyResult);
                            sendMessage(((PersonSingleResult)personSingleResult).getFirstName(), ((PersonSingleResult)personSingleResult).getLastName());
                        }
                    }
                }
            }
        }
        catch(IOException i) {
            isSuccessful = "";
        }
    }

    private void sendMessage(String firstName, String lastName) {
        Message message = Message.obtain();
        Bundle messageBundle = new Bundle();
        messageBundle.putString("first-name", firstName);
        messageBundle.putString("last-name", lastName);
        message.setData(messageBundle);
        messageHandler.sendMessage(message);
    }
}
