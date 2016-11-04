/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobloganalyzer.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import biz.isphere.base.internal.StringHelper;

public class JobLogReader {

    private static final int IDLE = 1;
    private static final int PARSE_PAGE_HEADER = 2;
    private static final int PARSE_MESSAGE = 3;

    private JobLogReaderConfiguration configuration;
    private int mode;
    private int headerCount;

    private JobLog jobLog;
    private JobLogPage jobLogPage;
    private JobLogMessage jobLogMessage;

    private String messageIndent;
    private List<String> messageAttributes;
    private int lastMessageAttribute;

    /**
     * Constructs a new JobLogReader object.
     */
    public JobLogReader() {
        
        this.configuration = new JobLogReaderConfiguration();
        this.configuration.loadConfiguration("en");
    }

    /**
     * This method loads a given job log from a plain-text stream file.
     * 
     * @param pathName - Name of the file.
     * @return number of bytes processed (linefeed bytes excluded)
     */
    public int loadFromStmf(String pathName) {

        BufferedReader br = null;
        jobLog = new JobLog();
        messageIndent = null;
        messageAttributes = new LinkedList<String>();
        lastMessageAttribute = -1;

        int count = -1;
        
        try {

            String line;

            br = new BufferedReader(new FileReader(pathName));

            mode = IDLE;
            while ((line = br.readLine()) != null) {

                count = count + line.length();
                
                mode = checkForStartOfPage(line);

                switch (mode) {
                case PARSE_PAGE_HEADER:
                    mode = parsePageHeader(line);
                    break;

                case PARSE_MESSAGE:
                    mode = parseMessage(line);
                    break;

                default:
                    break;
                }

            }

            if (jobLogMessage != null && messageAttributes.size() > 0) {
                updateMessageAttributes(jobLogMessage, messageAttributes);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        print(jobLog);

        return count;
    }

    /**
     * This method check the current line for the page number to start a new
     * page.
     * <p>
     * <code><ul>
     * <li>.5770SS1.V7R2M0.140418........................Display.Job.Log.......................GFD400....03.11.16..14:58:40.CET.....Page....1</li>
     * </ul></code>
     * 
     * @param line - current line of the job log.
     * @return new parser mode
     */
    private int checkForStartOfPage(String line) {

        if (line.length() <= 116) {
            return mode;
        }

        Matcher matcher = configuration.getStartOfPage().matcher(line.trim());
        while (matcher.find()) {
            jobLogPage = jobLog.addPage();
            jobLogPage.setPageNumber(new Integer(matcher.group(3).trim()).intValue());
            if (!jobLog.isHeaderComplete()) {
                return PARSE_PAGE_HEADER;
            } else {
                return mode;
            }
        }

        return mode;
    }

    /**
     * This method parses the header data that are at the top of each page of
     * the job log.
     * <p>
     * <code><ul>
     * <li>From module . . . . . . . . : LIBL#LOAD</li>
     * <li>From procedure . . . . . . : LIBL#LOAD</li>
     * <li>Statement . . . . . . . . . : 5500</li>
     * <li>To module . . . . . . . . . : START#RZ</li>
     * <li>To procedure . . . . . . . : START#RZ</li>
     * <li>Statement . . . . . . . . . : 5300</li>
     * <li>Message . . . . : ISPHEREDVP PY27V5R4 ...</li>
     * <li>Cause . . . . . : Es ist kein zusätzlicher ...</li> *
     * </ul></code>
     * 
     * @param line - current line of the job log.
     * @return new parser mode
     */
    private int parsePageHeader(String line) {

        if (jobLog.isHeaderComplete()) {
            return mode;
        }

        Matcher matcher = configuration.getPageHeader().matcher(line);
        while (matcher.find()) {
            headerCount++;
            switch (headerCount) {
            case 1:
                jobLog.setJobName(matcher.group(2));
                break;

            case 2:
                jobLog.setJobUserName(matcher.group(2));
                break;

            case 3:
                jobLog.setJobNumber(matcher.group(2));
                break;

            case 4:
                jobLog.setJobDescriptionName(matcher.group(2));
                break;

            case 5:
                jobLog.setJobDescriptionLibraryName(matcher.group(2));
                break;

            default:
                break;
            }
        }

        if (!jobLog.isHeaderComplete()) {
            return mode;
        }

        return PARSE_MESSAGE;
    }

    /**
     * This method parses a given line of the job log in order to:
     * <ul>
     * <li>Start a new message.</li>
     * <li>Collect the attributes of the current message.</li>
     * </ul>
     * 
     * @param line - current line of the job log.
     * @return new parser mode
     */
    private int parseMessage(String line) {

        Matcher matcher;

        if (line.trim().length() == 0) {
            return mode;
        }

        // Scan for the first line of the message
        matcher = configuration.getStartOfMessage().matcher(line.trim());
        while (matcher.find()) {
            updateMessageAttributes(jobLogMessage, messageAttributes);
            jobLogMessage = jobLogPage.addMessage();
            jobLogMessage.setId(matcher.group(1));
            jobLogMessage.setType(matcher.group(2));
            if (matcher.group(3) != null) {
                jobLogMessage.setSeverity(new Integer(matcher.group(3)).intValue());
            }
            jobLogMessage.setDate(matcher.group(4));
            jobLogMessage.setTime(matcher.group(5));

            messageIndent = null;
            messageAttributes.clear();
            lastMessageAttribute = -1;

            return mode;
        }

        if (jobLogMessage == null) {
            return mode;
        }

        // Scan for message attributes such as:
        // From module
        // From procedure
        // From statement
        // ...
        matcher = configuration.getMessageAttribute().matcher(line);
        while (matcher.find()) {
            if (messageAttributes.size() == 0) {
                messageIndent = getMessageContinuationIndention(line);
            }
            messageAttributes.add(matcher.group(2));
            lastMessageAttribute = messageAttributes.size() - 1;
        }

        // Check line for message continuation
        if (messageIndent != null && line.startsWith(messageIndent) && lastMessageAttribute >= 0) {
            String attributeValue = concatenate(messageAttributes.get(lastMessageAttribute), line.substring(messageIndent.length()));
            messageAttributes.set(lastMessageAttribute, attributeValue);
        }

        return mode;
    }

    /**
     * Concatenates the given string and inserts a SPACE, when the first string
     * does not end with a special character.
     * 
     * @param string1 - First part of the string.
     * @param string2 - Second part of the string.
     * @return concatenated string
     */
    private String concatenate(String string1, String string2) {

        if (string1.length() == 0) {
            return string2;
        }

        // String lastChar = string1.substring(string1.length()-1,
        // string1.length());
        // if ("-_".indexOf(lastChar) >= 0) {
        // return string1 + string2;
        // }

        return string1 + " " + string2;
    }

    /**
     * This method updates the attributes of the current message. It is
     * triggered when the parser detects the beginning of the next message.
     * 
     * @param jobLogMessage - current message
     * @param messageAttributes - attributes of the current message
     */
    private void updateMessageAttributes(JobLogMessage jobLogMessage, List<String> messageAttributes) {

        if (jobLogMessage == null && messageAttributes.size() > 0) {
            throw new RuntimeException("Parameter 'jobLogMessage' must not be null!");
        }

        if (messageAttributes.size() >= 7) {
            jobLogMessage.setToModule(messageAttributes.get(0));
            jobLogMessage.setToProcedure(messageAttributes.get(1));
            jobLogMessage.setToStatement(messageAttributes.get(2));
            jobLogMessage.setFromModule(messageAttributes.get(3));
            jobLogMessage.setFromProcedure(messageAttributes.get(4));
            jobLogMessage.setFromStatement(messageAttributes.get(5));
            jobLogMessage.setText(messageAttributes.get(6));
            if (messageAttributes.size() >= 8) {
                jobLogMessage.setCause(messageAttributes.get(7));
            }
            return;
        }

        if (messageAttributes.size() >= 4) {
            jobLogMessage.setToModule(messageAttributes.get(0));
            jobLogMessage.setToProcedure(messageAttributes.get(1));
            jobLogMessage.setToStatement(messageAttributes.get(2));
            jobLogMessage.setText(messageAttributes.get(3));
            if (messageAttributes.size() >= 5) {
                jobLogMessage.setCause(messageAttributes.get(4));
            }
            return;
        }

        if (messageAttributes.size() >= 1) {
            jobLogMessage.setText(messageAttributes.get(0));
            if (messageAttributes.size() >= 2) {
                jobLogMessage.setCause(messageAttributes.get(1));
            }
            return;
        }
    }

    /**
     * Returns the indention string of message continuation lines.
     * 
     * @param string - line of the current message attribute
     * @return indention string
     */
    private String getMessageContinuationIndention(String string) {

        int count = string.length() - StringHelper.trimL(string).length();

        return StringHelper.getFixLength("", count + 2); //$NON-NLS-1$
    }

    /**
     * This method is used for testing purposes.
     * <p>
     * It parses the specified job log and prints the result.
     * 
     * @param args - none (not used)
     */
    public static void main(String[] args) {

        JobLogReader main = new JobLogReader();
        // main.importFromStmf("c:/Temp/iSphere/Job Log Analyzer/iSphere Joblog - English_GFD400.txt");
        main.loadFromStmf("c:/Temp/iSphere/Job Log Analyzer/QPJOBLOG_2_712703_RADDATZ_TRADDATZA1_GFD400.txt");
        // main.loadFromStmf("c:/Temp/iSphere/Job Log Analyzer/iSphere_Spooled_File_QPJOBLOG_2_TRADDATZB1_RADDATZ_246474_WWSOBIDE_1160827_202522.txt");
        // main.loadFromStmf("c:/Temp/iSphere/Job Log Analyzer/QPJOBLOG_440_712206_CMONE_FR_D0008UJ_GFD400.txt");

    }

    private void print(JobLog jobLog) {

        System.out.println("Job log . . . . : " + jobLog.getQualifiedJobName());
        System.out.println("Job description : " + jobLog.getQualifiedJobDescriptionName());

        for (JobLogPage page : jobLog.getPages()) {
            System.out.println("  Page#: " + page.getPageNumber());
            for (JobLogMessage message : page.getMessages()) {

                System.out.println("    " + message.toString());

                printMessageAttribute("      Cause: ", message.getCause());
                printMessageAttribute("         to: ", message.getToModule());
                printMessageAttribute("           : ", message.getToProcedure());
                printMessageAttribute("           : ", message.getToStatement());
                printMessageAttribute("       from: ", message.getFromModule());
                printMessageAttribute("           : ", message.getFromProcedure());
                printMessageAttribute("           : ", message.getFromStatement());
            }
        }

    }

    private void printMessageAttribute(String label, String value) {

        if (value == null) {
            return;
        }

        System.out.println(label + value);
    }

}
