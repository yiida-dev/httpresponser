package yiida.httpresponser.gui;

import java.nio.charset.Charset;

public interface HttpResponse {

    String getStatusCode();

    String getBody();
    
    String getMimeType();
    
    Charset getCharset();
    
}
