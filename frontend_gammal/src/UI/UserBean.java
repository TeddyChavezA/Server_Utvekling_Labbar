package UI;

import BO.UserHandler;
import DTO.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean {

    public static final String urlpath = "http://172.16.83.82:8080/RESTful_services_war";

    private int id;
    @ManagedProperty("#{username}")
    private String username;
    @ManagedProperty("#{password}")
    private String password;
    private String name;
    private DetailsBean detailsBean;
    private LogBean logBean;
    private MenuView menuView;
    private MessageBean messageBean;
    private DTO_Messages inboxList;
    private ArrayList<DTO_Message> allMessages;

    public MessageBean getMessageBean() {
        return messageBean;
    }

    public void setMessageBean(MessageBean messageBean) {
        this.messageBean = messageBean;
    }



    public LogBean getLogBean() {
        return logBean;
    }

    public void setLogBean(LogBean logBean) {
        this.logBean = logBean;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<DTO_Message> getAllMessages() {
        if(allMessages == null){
            allMessages = getMessages();
        }
        return allMessages;
    }

    public void setAllMessages(ArrayList<DTO_Message> allMessages) {
        this.allMessages = allMessages;
    }

    @PostConstruct
    public void Init() {
        System.out.println("UserBean Init()");
        username="enter username";
        detailsBean = new DetailsBean();
        logBean = new LogBean();
        menuView = new MenuView();
        //inboxList = null;
        messageBean = new MessageBean();


    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    /*public boolean doLogin() {
        return UserHandler.login(username, password);
    }*/
    public Collection getNames() {
        return UserHandler.getUserNamesByName(username);
    }

    public String oldLogin() {
        BufferedReader br = null;
        InputStream inputStream = null;
        String redirectValue = "error.xhtml";
        try {
            URL url = new URL(urlpath +"/Login");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            out.write("username=" +username +"&password=" +password);
            out.close();

            int responsecode = httpCon.getResponseCode();

            inputStream = httpCon.getInputStream();
            br = null;
            switch(responsecode) {
                case 200:
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = br.readLine();

                    if(line.equals("success")){

                        redirectValue = "mypage.xhtml";
                    }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(br != null) { try{br.close();} catch (Exception e){} }
            if(inputStream != null) try{inputStream.close();} catch (Exception e){}
        }
        return redirectValue;
    }

    public String register(){
        BufferedReader br = null;
        InputStream inputStream = null;
        String redirectValue = "error.xhtml";
        try {
            URL url = new URL(urlpath +"/Register");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            out.write("name="+name +"&username=" +username +"&password=" +password);
            out.close();

            int responsecode = httpCon.getResponseCode();

            inputStream = httpCon.getInputStream();
            br = null;
            switch(responsecode) {
                case 200:
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = br.readLine();

                    if(line.equals("success")){

                        redirectValue = "login.xhtml";
                    }

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(br != null) { try{br.close();} catch (Exception e){} }
            if(inputStream != null) try{inputStream.close();} catch (Exception e){}
        }
        return redirectValue;
    }

    public String doLogin() {
        BufferedReader br = null;
        InputStream inputStream = null;
        String redirectValue = "error.xhtml";
        try {
            URL url = new URL(urlpath +"/Login");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");

            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            out.write("username=" +username +"&password=" +password);
            out.close();

            int responseCode = httpCon.getResponseCode();
            System.out.println("Responsecode: " +responseCode);

            inputStream = httpCon.getInputStream();
            br = null;
            switch(responseCode) {
                case 200:
                    //System.out.println(responsecode);
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = br.readLine();
                    DTO_User myself = null;
                    System.out.println(line);
                    Gson gson = new Gson();
                    if(line != null) myself = gson.fromJson(line, DTO_User.class);

                    if(myself != null){
                        detailsBean.setId(myself.getId());
                        menuView.setMyId(myself.getId());
                        detailsBean.setName(myself.getName());
                        detailsBean.setUsername(myself.getUsername());
                        detailsBean.setNoOfUnreadMessages(myself.getNoOfUnreadMessages());
                        redirectValue = "mypage.xhtml";
                    }
                    break;
            }
        }catch (Exception e) {

            e.printStackTrace();
        }
        finally {
            if(br != null) { try{br.close();} catch (Exception e){} }
            if(inputStream != null) try{inputStream.close();} catch (Exception e){}
        }
        System.out.println("Details: " + detailsBean.toString());
        return redirectValue;
    }
    public void doLogout(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }

    public String getWelcomeMessage() {
        BufferedReader br = null;
        InputStream inputStream = null;
        String returnMessage = "Login failed.";
        try {
            URL url = new URL(urlpath +"/MyPage");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");

            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());


            out.write("username=" +username +"&password=" +password);
            out.close();


            int responseCode = httpCon.getResponseCode();
            System.out.println("Responsecode: " +responseCode);

            inputStream = httpCon.getInputStream();
            br = null;
            switch(responseCode) {
                case 200:
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = br.readLine();

                    Gson gson = new Gson();
                    DTO_User user = gson.fromJson(line, DTO_User.class);
                    detailsBean.setNoOfUnreadMessages(user.getNoOfUnreadMessages());
                    returnMessage = "Hello " +detailsBean.getName()  +" You have " +detailsBean.getNoOfUnreadMessages() +" unread messages.";
                    break;
            }
        }catch (Exception e) {

            e.printStackTrace();
        }
        finally {
            if(br != null) { try{br.close();} catch (Exception e){} }
            if(inputStream != null) try{inputStream.close();} catch (Exception e){}
        }
        return returnMessage;

    }

    public String getMyLog(){
        BufferedReader br = null;
        InputStream inputStream = null;
        String entries = "";
        try {
            URL url = new URL(urlpath +"/MyLogPage/GetLog");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");

            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            out.write("username=" +username +"&password=" +password);
            out.close();

            int responseCode = httpCon.getResponseCode();
            System.out.println("Responsecode: " +responseCode);

            inputStream = httpCon.getInputStream();

            br = new BufferedReader(new InputStreamReader(inputStream));
            String gaysonString = br.readLine();
            Gson gayson = new Gson();
            DTO_Log log = gayson.fromJson(gaysonString, DTO_Log.class);
            System.out.println("Printing out content: ");

            StringBuilder sb = new StringBuilder();
            ArrayList<DTO_Post> postList = log.getPostList();
            for(DTO_Post post: postList) {
                sb.append(post.getContent() +"<br>");
            }
            entries = sb.toString();
            System.out.println(entries);

        }catch (Exception e) {

            e.printStackTrace();
        }
        finally {
            if(br != null) { try{br.close();} catch (Exception e){} }
            if(inputStream != null) try{inputStream.close();} catch (Exception e){}
        }
        return entries;
    }

    public String createLog(){
        BufferedReader br = null;
        InputStream inputStream = null;
        String redirectValue = "error.xhtml";
        String content = logBean.getContent();
        if(content != null) {
            System.out.println(content);
        }
        try {
            URL url = new URL(urlpath +"/MyLogPage/CreateLog");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");

            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            out.write("username=" +username +"&password=" +password +"&content=" +logBean.getContent());
            out.close();
            System.out.println(username +" " +password);

            int responseCode = httpCon.getResponseCode();
            System.out.println("Responsecode: " +responseCode);

            inputStream = httpCon.getInputStream();
            br = null;
            switch(responseCode) {
                case 200:
                    //System.out.println(responsecode);
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = br.readLine();

                    if(line == null) {
                        System.out.println("Line is null123");
                    }
                    else {
                        String[] lineSplit = null;
                        while (line != null){
                            System.out.println(line);
                            lineSplit = line.split(":");
                            System.out.println("lineSplit length " +lineSplit.length);
                            switch(lineSplit[0]){
                                case "id": detailsBean.setId(Integer.parseInt(lineSplit[1]));break;
                                case "name": detailsBean.setName(lineSplit[1]);
                                    System.out.println("name is: " +lineSplit[1]);break;
                                case "username": detailsBean.setUsername(lineSplit[1]); break;
                                case "noOfUnreadMessages": detailsBean.setNoOfUnreadMessages(Integer.parseInt(lineSplit[1])); break;
                            }
                            line = br.readLine();
                        }
                    }
                    redirectValue = "mypage.xhtml";
                    break;
                case 403:
                    //Wrong login
                    break;

            }
        }catch (Exception e) {

            e.printStackTrace();
        }
        finally {
            if(br != null) { try{br.close();} catch (Exception e){} }
            if(inputStream != null) try{inputStream.close();} catch (Exception e){}
        }
        System.out.println("Details: " + detailsBean.toString());
        return redirectValue;
    }

    public String writeMessage(){
        BufferedReader br = null;
        InputStream inputStream = null;
        String redirectValue = "error.xhtml";
        try {
            URL url = new URL(urlpath +"/MessagePage/CreateMessage");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");

            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());

            messageBean.setIdFrom(id);

            DTO_Message message = messageBean.getDTO();

            Gson gson = new Gson();
            String toGson = gson.toJson(message);

            out.write("username=" +username +"&password=" +password +"&gson_message=" +toGson);
            out.close();
            System.out.println(username +" " +password);

            int responseCode = httpCon.getResponseCode();
            System.out.println("Responsecode: " +responseCode);

            inputStream = httpCon.getInputStream();
            br = null;
            switch(responseCode) {
                case 200:
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = br.readLine();
                    redirectValue = "messageSent.xhtml";
                    break;
            }
        }catch (Exception e) {

            e.printStackTrace();
        }
        finally {
            if(br != null) { try{br.close();} catch (Exception e){} }
            if(inputStream != null) try{inputStream.close();} catch (Exception e){}
        }
        return redirectValue;
    }

    public String getMyMessages() {
        String redirect = "error.xhtml";

        return redirect;
    }

    public String getMyName() {
        return detailsBean.getName();
    }

    /**
     *  Navigation-menu
     */
    public String getMenu() {
        return menuView.getNavigationMenu();
    }


    /**
     * Load DTO items from backend and present to UI presented in HTML code
     *
     *
     *
     *
     */

    /**
     * Get all messages belonging to user
     */


    public ArrayList<DTO_Message> getMessages() {
        //Return to ui
        String messages = "";

        //Download messages from backend
        InputStream inputStream = null;
        BufferedReader br = null;
        try {
            URL url = new URL(urlpath +"/MessagePage/GetMessages");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");

            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());

            out.write("username=" +username +"&password=" +password);
            out.close();

            int responseCode = httpCon.getResponseCode();
            System.out.println("Responsecode: " +responseCode);

            inputStream = httpCon.getInputStream();
            br = null;
            switch(responseCode) {
                case 200:
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = br.readLine();
                    if(line != null) {
                        Gson gson = new Gson();
                        inboxList = gson.fromJson(line, DTO_Messages.class);
                        System.out.println("Messages received, size: " +inboxList.getMessagesList().size());
                        //messageBean.setInboxList(inboxList.getMessagesList());
                    }
                    break;
            }
        }catch (Exception e) {

            e.printStackTrace();
        }
        finally {
            if(br != null) { try{br.close();} catch (Exception e){} }
            if(inputStream != null) try{inputStream.close();} catch (Exception e){}
        }

        return inboxList.getMessagesList();
    }

    /**
     * Read a message (downloaded by getMessages) post update for "isRead"-variable to backend if message was not
     * previously read.
     */
    public String getMessage() {
        if (password == null) {
            System.out.println("userBean is null");
        }

        String messageBody = "";
        int messageId = -1;
        DTO_Message readMessage = null;
        System.out.println("Testing... " +username);
        /*
        try {
            messageId = Integer.parseInt(id);

        }catch (Exception e) {
            return "Error: id is not a integer.";
        }*/

        System.out.println("Message id: " +messageId);


        try {
            InputStream inputStream = null;
            BufferedReader br = null;
            URL url = new URL(urlpath + "/MessagePage/GetMessage");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");

            OutputStreamWriter out = new OutputStreamWriter(
                        httpCon.getOutputStream());
            System.out.println(username + " " +password +" " +messageId);
            out.write("username=carlos" + "&password=test123" +"&messageId=13");
            out.close();

            int responseCode = httpCon.getResponseCode();
            System.out.println("Responsecode: " + responseCode);

            inputStream = httpCon.getInputStream();
            br = null;
            switch (responseCode) {
                case 200:
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = br.readLine();
                    Gson gson = new Gson();
                    readMessage = gson.fromJson(line, DTO_Message.class);
                    if (line == null) {
                        return "Server responses with null or fail";
                    }
                    messageBody = messageBean.parseMessageToXhtml(readMessage);
                    break;
            }

        } catch (Exception e) {

            return "Error: message not found.";
        }

        return messageBody;
    }

    /**
     * Get all users from database and display with link to each users log in xhtml format
     */
    public String getAllUsers() {
        DTO_Users allUsers = null;
        String displayUsers = "";
        try {
            InputStream inputStream = null;
            BufferedReader br = null;
            URL url = new URL(urlpath + "/UserPage/GetAllUsers");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");

            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            out.write("username=" + username + "&password=" + password);
            out.close();

            int responseCode = httpCon.getResponseCode();
            System.out.println("Responsecode: " + responseCode);

            inputStream = httpCon.getInputStream();
            br = null;
            switch (responseCode) {
                case 200:
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = br.readLine();
                    Gson gson = new Gson();
                    if (line == null || !line.equals("success")) {
                        return "Server responses with null or fail";
                    }
                    allUsers = gson.fromJson(line, DTO_Users.class);
                    ArrayList<String> userList = allUsers.getUserList();
                    StringBuilder sb = new StringBuilder();
                    for(String u: userList) {
                        sb.append("<a href=\"display_user.xhtml\"?username="+u +"\"><br />");
                    }
                    break;
            }

        } catch (Exception e) {

            return "Error: message not found.";
        }
        return displayUsers;
    }


}