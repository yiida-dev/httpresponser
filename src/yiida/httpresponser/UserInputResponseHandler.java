package yiida.httpresponser;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import yiida.httpresponser.gui.HttpResponse;
import yiida.httpresponser.gui.HttpResponseDialog;
import yiida.httpresponser.net.ResponseHandler;

public final class UserInputResponseHandler implements ResponseHandler {

    private Map<String, String> httpStatusMsg;
    private HttpResponseDialog dialog;
    public UserInputResponseHandler() {
        httpStatusMsg = new HashMap<String, String>(60, 1.0f);
        httpStatusMsg.put("100", "Continue");
        httpStatusMsg.put("101", "Switching Protocols");
        httpStatusMsg.put("102", "Processing");
        httpStatusMsg.put("200", "OK");
        httpStatusMsg.put("201", "Created");
        httpStatusMsg.put("202", "Accepted");
        httpStatusMsg.put("203", "Non-Authoritative Information");
        httpStatusMsg.put("204", "No Content");
        httpStatusMsg.put("205", "Reset Content");
        httpStatusMsg.put("206", "Partial Content");
        httpStatusMsg.put("207", "Multi-Status");
        httpStatusMsg.put("226", "IM Used");
        httpStatusMsg.put("300", "Multiple Choices");
        httpStatusMsg.put("301", "Moved Permanently");
        httpStatusMsg.put("302", "Found");
        httpStatusMsg.put("303", "See Other");
        httpStatusMsg.put("304", "Not Modified");
        httpStatusMsg.put("305", "Use Proxy");
        httpStatusMsg.put("306", "(Unused)");
        httpStatusMsg.put("307", "Temporary Redirect");
        httpStatusMsg.put("400", "Bad Request");
        httpStatusMsg.put("401", "Unauthorized");
        httpStatusMsg.put("402", "Payment Required");
        httpStatusMsg.put("403", "Forbidden");
        httpStatusMsg.put("404", "Not Found");
        httpStatusMsg.put("405", "Method Not Allowed");
        httpStatusMsg.put("406", "Not Acceptable");
        httpStatusMsg.put("407", "Proxy Authentication Required");
        httpStatusMsg.put("408", "Request Timeout");
        httpStatusMsg.put("409", "Conflict");
        httpStatusMsg.put("410", "Gone");
        httpStatusMsg.put("411", "Length Required");
        httpStatusMsg.put("412", "Precondition Failed");
        httpStatusMsg.put("413", "Request Entity Too Large");
        httpStatusMsg.put("414", "Request-URI Too Long");
        httpStatusMsg.put("415", "Unsupported Media Type");
        httpStatusMsg.put("416", "Requested Range Not Satisfiable");
        httpStatusMsg.put("417", "Expectation Failed");
        httpStatusMsg.put("418", "I'm a teapot");
        httpStatusMsg.put("422", "Unprocessable Entity");
        httpStatusMsg.put("423", "Locked");
        httpStatusMsg.put("424", "Failed Dependency");
        httpStatusMsg.put("426", "Upgrade Required");
        httpStatusMsg.put("500", "Internal Server Error");
        httpStatusMsg.put("501", "Not Implemented");
        httpStatusMsg.put("502", "Bad Gateway");
        httpStatusMsg.put("503", "Service Unavailable");
        httpStatusMsg.put("504", "Gateway Timeout");
        httpStatusMsg.put("505", "HTTP Version Not Supported");
        httpStatusMsg.put("506", "Variant Also Negotiates");
        httpStatusMsg.put("507", "Insufficient Storage");
        httpStatusMsg.put("509", "Bandwidth Limit Exceeded");
        httpStatusMsg.put("510", "Not Extended");

        dialog = new HttpResponseDialog();
        dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }

    @Override
    public String respond(String request) {
        dialog.setRequest(request);
        UserInput input = new UserInput(dialog);
        try {
            SwingUtilities.invokeAndWait(input);
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            System.out.println("thread interrupted while waiting user input.");
            return "";
        }
        HttpResponse response = input.result;

        String msg = httpStatusMsg.get(response.getStatusCode());
        if(msg == null) msg = "";
        String res = "HTTP/1.1 "+ response.getStatusCode() + " " + msg + "\r\n";
        res += "Content-Type: text/plain\r\n";
        res += "Content-Encoding: utf-8\r\n";
        res += "Connection: close\r\n";
        res += "\r\n";
        res += response.getBody();
        return res;
    }

    private static final class UserInput implements Runnable {

        HttpResponse result;
        HttpResponseDialog dialog;
        UserInput(HttpResponseDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void run() {
            result = dialog.showWithResult();
        }
    }
}
