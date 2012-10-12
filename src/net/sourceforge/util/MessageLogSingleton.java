package net.sourceforge.util;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * Singleton class used to log messages:
 * sample use,
 * MessageLogSingleton ml = MessageLogSingleton.instance();
 * ml.append( "Hello World" );
 * @author morkeld@user.sourceforge.net
 */
public class MessageLogSingleton
{
    private static MessageLogSingleton instance = null;

    private JButton saveButton = null; 
    private JButton clearButton = null; 
    private String myBuffer = new String();
    private JTextArea myTextArea = null;
    private JScrollPane areaScrollPane = null;
    private JPanel myPanel = null;

    /**
    Protected so that no one can instantiate it
    */
    protected MessageLogSingleton() {
    }

    /**
    Return the Instance of our MessageLogSingleton
    @return The single instance of our MessageLogSingleton
    */
    public static MessageLogSingleton instance() {
        if (instance == null)
            instance = new MessageLogSingleton();
        return instance;
    }

    /**
    Create a visual represenation of the message log for GUIs
    @return A Component that visually represents this object
    */
    public Component toComponent()
    {
        if (myPanel == null) {
            myTextArea = new JTextArea();
            myTextArea.setEditable(false);
            myTextArea.setText(myBuffer);
            myTextArea.setLineWrap(true);
            myTextArea.setWrapStyleWord(true);

            areaScrollPane = new JScrollPane(myTextArea);
            areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            areaScrollPane.setPreferredSize(new Dimension(250, 250));

            // create the save button
            final JFileChooser fc = new JFileChooser(".");

            saveButton = new JButton("Save Message Log...");
            saveButton.setMnemonic(KeyEvent.VK_S);
            saveButton.setToolTipText("Save Message Buffer");
            saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (fc.showSaveDialog(myTextArea) == JFileChooser.APPROVE_OPTION) {
                        saveBuffer(fc.getSelectedFile().toString());
                    }
                    else {
                        append("Save command cancelled by user.");
                    }
                }
            });

            // create the clear button
            clearButton = new JButton("Clear Message Log");
            clearButton.setMnemonic(KeyEvent.VK_C);
            clearButton.setToolTipText("Clear the message Buffer");
            clearButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    clearBuffer();
                }
            });

            JPanel tmpPane = new JPanel();
            tmpPane.setLayout(new BorderLayout());
            tmpPane.add(clearButton, BorderLayout.EAST);
            tmpPane.add(saveButton, BorderLayout.WEST);
            myPanel = new JPanel();
            myPanel.setLayout(new BorderLayout());
            myPanel.setBorder(new javax.swing.border.TitledBorder(BorderFactory.createEmptyBorder(), 
                "-Message Log-"));
            myPanel.add(areaScrollPane, BorderLayout.CENTER);
            myPanel.add(tmpPane, BorderLayout.SOUTH);
        }
        return myPanel;
    }

    /**
    Append a string to our message log
    @param String s the string to append
    */
    public void append(String s)
    {
        s += "\n";
        myBuffer += s;
        if (myTextArea != null) {
            myTextArea.append(s);
            Caret c = myTextArea.getCaret();
            c.setDot(myTextArea.getDocument().getLength());
        }        
    }

    /**
    Set the entire contents of our message log to this value
    @param String s the string to reset the message log to
    */
    public void setText(String s)
    {
        myBuffer = s;
        if (myTextArea != null)
            myTextArea.setText(myBuffer);        
    }

    /**
    Save the contents of our message log to a file
    @param String outputFile the file to write the message log in
    */
    public void saveBuffer(String outputFile)
    {
        try {
            // open file for append
            FileWriter logFile = new FileWriter(outputFile, true);  
            logFile.write(myBuffer, 0, myBuffer.length());
            //logFile.flush();
            logFile.close();
            append("Saving: " + outputFile + ".");
        } catch(Exception e) {
            append("ERROR ::= " + e.getMessage());
        }
    }

    /**
    Clear the contents of our message log
    */
    public void clearBuffer()
    {
        myBuffer = "";
        if (myTextArea != null)
            myTextArea.setText("");      
    }

    /**
    Print the contents of our message log to System.out
    */
    public void printBuffer()
    {
        if (myBuffer != null)
            System.out.println(myBuffer);       
    }
}

