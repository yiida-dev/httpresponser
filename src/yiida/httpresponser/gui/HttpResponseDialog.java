package yiida.httpresponser.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class HttpResponseDialog extends JDialog implements ResultDialog<HttpResponse> {

    private static final long serialVersionUID = 1204780736077343716L;

    private JTextArea textAreaReq;
    private JTextField textFieldStatus;
    private JTextField textFieldMimeType;
    private JTextArea textAreaBody;

    private boolean commit;
    private InputResult result;

    public HttpResponseDialog() {
        //this.setResizable(false);
        this.setTitle("Respond this request.");
        this.setSize(600, 480);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        JPanel panel = new JPanel();
        panel.setMaximumSize(new Dimension(2048, 256));
        textAreaReq = new JTextArea();
        textAreaReq.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textAreaReq);
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        box.add(panel);

        panel = new JPanel();
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        JLabel label = new JLabel("Status Code:");
        layout.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, label, 20, SpringLayout.NORTH, panel);
        panel.add(label);
        textFieldStatus = new JTextField(10);
        layout.putConstraint(SpringLayout.WEST, textFieldStatus, 5, SpringLayout.EAST, label);
        layout.putConstraint(SpringLayout.NORTH, textFieldStatus, 0, SpringLayout.NORTH, label);
        panel.add(textFieldStatus);
        label = new JLabel("MIME Type:");
        layout.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.EAST, textFieldStatus);
        layout.putConstraint(SpringLayout.NORTH, label, 0, SpringLayout.NORTH, textFieldStatus);
        panel.add(label);
        textFieldMimeType = new JTextField(10);
        textFieldMimeType.setText("text/plain");
        layout.putConstraint(SpringLayout.WEST, textFieldMimeType, 5, SpringLayout.EAST, label);
        layout.putConstraint(SpringLayout.NORTH, textFieldMimeType, 0, SpringLayout.NORTH, label);
        panel.add(textFieldMimeType);
        label = new JLabel("MessageBody:");
        layout.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.SOUTH, textFieldStatus);
        panel.add(label);
        textAreaBody = new JTextArea(18,50);
        scrollPane = new JScrollPane(textAreaBody);
        layout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.SOUTH, label);
        panel.add(scrollPane);
        JButton button = new JButton("Respond");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result.code = textFieldStatus.getText();
                result.mime = textFieldMimeType.getText();
                result.body = textAreaBody.getText();
                commit = true;
                HttpResponseDialog.this.setVisible(false);
            }
        });
        layout.putConstraint(SpringLayout.EAST, button, 0, SpringLayout.EAST, scrollPane);
        layout.putConstraint(SpringLayout.SOUTH, button, -10, SpringLayout.NORTH, scrollPane);
        panel.add(button);

        box.add(panel);

        this.add(box, BorderLayout.CENTER);
        commit = false;
        result = new InputResult();
    }

    public void setRequest(String request) {
        textAreaReq.setText(request);
    }

    @Override
    public HttpResponse showWithResult() {
        commit = false;
        textFieldStatus.setText("");
        textAreaBody.setText("");
        this.setVisible(true);
        if(!commit) {
            result.code = "404";
            result.body = "";
        }
        return result;
    }

    private static final class InputResult implements HttpResponse {

        String code;
        String body;
        String mime;

        InputResult() {}

        @Override
        public String getStatusCode() {
            return code;
        }

        @Override
        public String getBody() {
            return body;
        }

        @Override
        public String getMimeType() {
            return mime;
        }
        
        @Override
        public Charset getCharset() {
            return null;
        }
    }
}
