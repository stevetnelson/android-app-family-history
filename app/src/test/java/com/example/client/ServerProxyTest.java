package com.example.client;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.ArrayList;
import Result.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ServerProxyTest {

    @Test
    public void loginSuccess() {
        String json = "{\n" +
                "  \"password\": \"parker\",\n" +
                "  \"username\": \"sheila\"\n" +
                "}";
        String url = "http://localhost:8080/user/login";
        LoginRegisterResult postResult;
        String result = "";
        try {
            String requestResult = ServerProxy.request(json, url, "", "POST");
            postResult = Serializer.deserialize(requestResult, LoginRegisterResult.class);
            result = postResult.getPersonID();
        }
        catch(IOException i) { }
        assertEquals(result, "Sheila_Parker");
    }

    @Test
    public void loginFailure() {
        String json = "{\n" +
                "  \"password\": \"invalid\",\n" +
                "  \"username\": \"login\"\n" +
                "}";
        String url = "http://localhost:8080/user/login";
        String result = "success";
        try {
            result = ServerProxy.request(json, url, "", "POST");
        }
        catch(IOException i) { }
        assertEquals(result, "");
    }

    @Test
    public void registerSuccess() {
        String json = "{\n" +
                "  \"email\": \"test\",\n" +
                "  \"firstName\": \"test\",\n" +
                "  \"gender\": \"m\",\n" +
                "  \"lastName\": \"test\",\n" +
                "  \"password\": \"test\",\n" +
                "  \"username\": \"test\"\n" +
                "}";
        String url = "http://localhost:8080/user/register";
        LoginRegisterResult postResult;
        String result = "";
        try {
            String requestResult = ServerProxy.request(json, url, "", "POST");
            postResult = Serializer.deserialize(requestResult, LoginRegisterResult.class);
            result = postResult.getUsername();
        }
        catch(IOException i) { }
        assertEquals(result, "test");
    }

    @Test
    public void registerFailure() {
        String json = "{\n" +
                "  \"email\": \"test\",\n" +
                "  \"firstName\": \"test\",\n" +
                "  \"gender\": \"m\",\n" +
                "  \"lastName\": \"test\",\n" +
                "  \"password\": \"test\",\n" +
                "  \"username\": \"test\"\n" +
                "}";
        String url = "http://localhost:8080/user/register";
        String result = "success";
        try {
            result = ServerProxy.request(json, url, "", "POST");
        }
        catch(IOException i) { }
        assertEquals(result, "");
    }

    @Test
    public void getPeopleSuccess() {
        String json = "{\n" +
                "  \"password\": \"parker\",\n" +
                "  \"username\": \"sheila\"\n" +
                "}";
        LoginRegisterResult postResult;
        String authToken = "";
        try {
            String requestResult = ServerProxy.request(json, "http://localhost:8080/user/login", "", "POST");
            postResult = Serializer.deserialize(requestResult, LoginRegisterResult.class);
            authToken = postResult.getAuthtoken();
        }
        catch(IOException i) { }
        String url = "http://localhost:8080/person";
        ArrayList<PersonSingleResult> result = new ArrayList<>();
        try {
            String requestResult = ServerProxy.request("", url, authToken, "GET");
            PersonFamilyResult personFamilyResult = Serializer.deserialize(requestResult, PersonFamilyResult.class);
            result.addAll(personFamilyResult.getData());
        }
        catch(IOException i) { }
        assertEquals(result.get(0).getPersonID(), "Sheila_Parker");
    }

    @Test
    public void getPeopleFailure() {
        String url = "http://localhost:8080/person";
        String result = "";
        try {
            result = ServerProxy.request("", url, "wrong_auth_token", "GET");
        }
        catch(IOException i) { }
        assertEquals(result, "");
    }

    @Test
    public void getEventsSuccess() {
        String json = "{\n" +
                "  \"password\": \"parker\",\n" +
                "  \"username\": \"sheila\"\n" +
                "}";
        LoginRegisterResult postResult;
        String authToken = "";
        try {
            String requestResult = ServerProxy.request(json, "http://localhost:8080/user/login", "", "POST");
            postResult = Serializer.deserialize(requestResult, LoginRegisterResult.class);
            authToken = postResult.getAuthtoken();
        }
        catch(IOException i) { }
        String url = "http://localhost:8080/event";
        ArrayList<EventSingleResult> result = new ArrayList<>();
        try {
            String requestResult = ServerProxy.request("", url, authToken, "GET");
            EventFamilyResult eventFamilyResult = Serializer.deserialize(requestResult, EventFamilyResult.class);
            result.addAll(eventFamilyResult.getData());
        }
        catch(IOException i) { }
        assertEquals(result.get(0).getEventID(), "Sheila_Birth");
    }

    @Test
    public void getEventsFailure() {
        String url = "http://localhost:8080/event";
        String result = "";
        try {
            result = ServerProxy.request("", url, "wrong_auth_token", "GET");
        }
        catch(IOException i) { }
        assertEquals(result, "");
    }
}