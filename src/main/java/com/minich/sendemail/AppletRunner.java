package com.minich.sendemail;

import org.apache.commons.lang3.StringUtils;

import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppletRunner implements ActionListener {
    public static final String NEW_LINE_DELIMITER = "\n";
    public static final int TEXT_AREA_ROW_SIZE = 8;
    public static final int TEXT_AREA_COLUMN_SIZE = 54;
    public static final int PANEL_WIDTH = 650;
    public static final int PANEL_HEIGHT = 180;
    public static final int ELEMENT_LEFT_START_POSITION = 180;

    JFrame frame;
    JTextArea toEmailArea;
    JTextArea agreementNumberArea;
    JTextArea amountArea;
    JTextArea dateFromArea;

    public static void main(String[] args) {
        new AppletRunner();
    }

    public AppletRunner() {
        frame = new JFrame("Send email app");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 1000);

        toEmailArea = new JTextArea(TEXT_AREA_ROW_SIZE, TEXT_AREA_COLUMN_SIZE);
        JPanel toEmailPanel = getPanelWithTextArea(toEmailArea, "Email кому:", ELEMENT_LEFT_START_POSITION, 50, PANEL_WIDTH, PANEL_HEIGHT);
        frame.add(toEmailPanel);

        agreementNumberArea = new JTextArea(TEXT_AREA_ROW_SIZE, TEXT_AREA_COLUMN_SIZE);
        JPanel agreementNumberPanel = getPanelWithTextArea(agreementNumberArea, "Номер договора:", ELEMENT_LEFT_START_POSITION, 250, PANEL_WIDTH, PANEL_HEIGHT);
        frame.add(agreementNumberPanel);

        dateFromArea = new JTextArea(TEXT_AREA_ROW_SIZE, TEXT_AREA_COLUMN_SIZE);
        JPanel dateFromPanel = getPanelWithTextArea(dateFromArea, "Дата заключения договора:", ELEMENT_LEFT_START_POSITION, 450, PANEL_WIDTH, PANEL_HEIGHT);
        frame.add(dateFromPanel);

        amountArea = new JTextArea(TEXT_AREA_ROW_SIZE, TEXT_AREA_COLUMN_SIZE);
        JPanel amountPanel = getPanelWithTextArea(amountArea, "Сумма задолженности:", ELEMENT_LEFT_START_POSITION, 650, PANEL_WIDTH, PANEL_HEIGHT);
        frame.add(amountPanel);

        JButton button = new JButton("Send");
        button.setBounds(20, 850, 200, 30);
        button.addActionListener(this);
        frame.add(button);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String toEmailInput = toEmailArea.getText();
        String agreementNumberInput = agreementNumberArea.getText();
        String dateFromInput = dateFromArea.getText();
        String amountInput = amountArea.getText();

        if (StringUtils.isEmpty(toEmailInput) || StringUtils.isEmpty(agreementNumberInput)
                || StringUtils.isEmpty(dateFromInput) || StringUtils.isEmpty(amountInput)) {
            JOptionPane.showMessageDialog(frame, "Пожалуйста, заполните все поля.");
            return;
        }

        String[] splittedEmails = toEmailInput.split(NEW_LINE_DELIMITER);
        String[] splittedAgreementNumber = agreementNumberInput.split(NEW_LINE_DELIMITER);
        String[] splittedDateFrom = dateFromInput.split(NEW_LINE_DELIMITER);
        String[] splittedAmount = amountInput.split(NEW_LINE_DELIMITER);

        if (!checkLength(splittedEmails, splittedAgreementNumber, splittedDateFrom, splittedAmount)) {
            JOptionPane.showMessageDialog(frame, "Данные в одном из полей сопоставлены неверно. Если вы хотите указать несколько получателей письма, количество строк в каждом поле должно совпадать. ");
            return;
        }
        List<String> successEmails = new ArrayList<>();
        Map<String, String> errorEmail = new HashMap<>();
        for (int i = 0; i < splittedEmails.length; i++) {
            String emailToSend = splittedEmails[i].trim();
            String numberToSend = splittedAgreementNumber[i].trim();
            String dateToSend = splittedDateFrom[i].trim();
            String amountToSend = splittedAmount[i].trim();
            try {
                if (StringUtils.isEmpty(emailToSend) || StringUtils.isEmpty(numberToSend) || StringUtils.isEmpty(dateToSend) || StringUtils.isEmpty(amountToSend)) {
                    continue;
                }
                SendEmailService.sendEmail(emailToSend, numberToSend, dateToSend, amountToSend);
                successEmails.add(emailToSend);
            } catch (MessagingException | UnsupportedEncodingException messagingException) {
                errorEmail.put(emailToSend, messagingException.getMessage());
            }
        }
        showSuccessMessageIfRequired(successEmails);
        showErrorMessageIfRequired(errorEmail);
    }

    private void showErrorMessageIfRequired(Map<String, String> errorEmail) {
        if (!errorEmail.keySet().isEmpty()) {
            String message = StringUtils.EMPTY;
            for (String s : errorEmail.keySet()) {
                message = message + StringUtils.SPACE + s + " - " + errorEmail.get(s) + NEW_LINE_DELIMITER;
            }
            JOptionPane.showMessageDialog(frame, "Ошибка во время отправки:\n" + message);
        }
    }

    private void showSuccessMessageIfRequired(List<String> successEmails) {
        if (!successEmails.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Письма успешно отправлены по адресам: " + String.join(", ", successEmails));
        }
    }

    private boolean checkLength(String[] a1, String[] a2, String[] a3, String[] a4) {
        return a1.length == a2.length && a1.length == a3.length && a1.length == a4.length;
    }

    private JPanel getPanelWithTextArea(JTextArea textArea, String title, int posX, int posY, int width, int heights) {
        JPanel middlePanel = new JPanel();
        middlePanel.setBorder(new TitledBorder(new EtchedBorder(), title));
        textArea.setEditable(true);
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        middlePanel.add(scroll);
        middlePanel.setBounds(posX, posY, width, heights);
        return middlePanel;
    }
}
