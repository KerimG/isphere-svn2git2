/*******************************************************************************
 * Copyright (c) 2005 SoftLanding Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     SoftLanding - initial API and implementation
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/
package biz.isphere.messagesubsystem.rse;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.base.swt.widgets.UpperCaseOnlyVerifier;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.stringlisteditor.StringListEditor;
import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.internal.QEZSNDMG;

public class SendMessageDialog extends XDialog {

    private static final String MESSAGE_TYPE = "MESSAGE_TYPE"; //$NON-NLS-1$
    private static final String DELIVERY_MODE = "DELIVERY_MODE"; //$NON-NLS-1$
    private static final String MESSAGE_TEXT = "MESSAGE_TEXT"; //$NON-NLS-1$
    private static final String MESSAGE_RECIPIENT_TYPES = "MESSAGE_RECIPIENT_TYPES"; //$NON-NLS-1$
    private static final String MESSAGE_RECIPIENT = "MESSAGE_RECIPIENT"; //$NON-NLS-1$
    private static final String MESSAGE_RECIPIENTS_COUNT = "MESSAGE_RECIPIENTS_COUNT"; //$NON-NLS-1$
    private static final String MESSAGE_RECIPIENT_ITEM = "MESSAGE_RECIPIENTS_ITEM_"; //$NON-NLS-1$

    private static final int DEFAULT_INDEX_MESSAGE_TYPE = 0;
    private static final int DEFAULT_INDEX_DELIVERY_MODE = 0;
    private static final int DEFAULT_INDEX_RECIPIENT_TYPES = 0;
    private static final int DEFAULT_INDEX_RECIPIENT = 0;

    private static final String RECIPIENT_LIST = "*LIST"; //$NON-NLS-1$

    private static final int BUTTON_RESET_ID = -1;

    private Combo comboMessageType;
    private Combo comboDeliveryMode;
    private Text textMessageText;
    private Combo comboRecipientTypes;
    private Combo comboRecipient;

    private SendMessageOptions sendMessageOptions;
    private StringListEditor receipientsEditor;

    private String overWriteMessageText;

    public SendMessageDialog(Shell shell) {
        super(shell);

        this.sendMessageOptions = null;
    }

    public void setMessageText(String text) {
        this.overWriteMessageText = text;
    }

    @Override
    public Control createDialogArea(Composite parent) {

        parent.getShell().setText(Messages.Send_Message);

        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NONE);
        scrolledComposite.setLayout(new GridLayout());
        scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        Composite mainPanel = new Composite(scrolledComposite, SWT.BORDER);
        mainPanel.setLayout(new GridLayout(2, false));
        mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label labelMessageType = new Label(mainPanel, SWT.NONE);
        labelMessageType.setLayoutData(createLabelLayoutData());
        labelMessageType.setText(Messages.Message_type_colon);

        comboMessageType = WidgetFactory.createReadOnlyCombo(mainPanel);
        comboMessageType.setLayoutData(createInputFieldLayoutData());
        comboMessageType.setItems(new String[] { QEZSNDMG.TYPE_INFO, QEZSNDMG.TYPE_INQUERY });
        comboMessageType.select(DEFAULT_INDEX_MESSAGE_TYPE);

        Label labelDeliveryMode = new Label(mainPanel, SWT.NONE);
        labelDeliveryMode.setLayoutData(createLabelLayoutData());
        labelDeliveryMode.setText(Messages.Delivery_mode_colon);

        comboDeliveryMode = WidgetFactory.createReadOnlyCombo(mainPanel);
        comboDeliveryMode.setLayoutData(createInputFieldLayoutData());
        comboDeliveryMode.setItems(new String[] { QEZSNDMG.DELIVERY_NORMAL, QEZSNDMG.DELIVERY_BREAK });
        comboDeliveryMode.select(DEFAULT_INDEX_DELIVERY_MODE);

        Label labelMessageText = new Label(mainPanel, SWT.NONE);
        labelMessageText.setLayoutData(createLabelLayoutData());
        labelMessageText.setText(Messages.Message_text_colon);

        textMessageText = WidgetFactory.createMultilineText(mainPanel, true, true);
        GridData textMessageTextLayoutData = new GridData(GridData.FILL_BOTH);
        textMessageTextLayoutData.minimumHeight = 100;
        textMessageText.setLayoutData(textMessageTextLayoutData);
        textMessageText.setText(""); //$NON-NLS-1$
        textMessageText.addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    e.doit = false;
                }
            }
        });
        textMessageText.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == SWT.TAB) {
                    e.doit = true;
                }
            }
        });

        Label labelRecipientTypes = new Label(mainPanel, SWT.NONE);
        labelRecipientTypes.setLayoutData(createLabelLayoutData());
        labelRecipientTypes.setText(Messages.Recipient_type_colon);

        comboRecipientTypes = WidgetFactory.createReadOnlyCombo(mainPanel);
        comboRecipientTypes.setLayoutData(createInputFieldLayoutData());
        comboRecipientTypes.setItems(new String[] { QEZSNDMG.RECIPIENT_TYPE_USER, QEZSNDMG.RECIPIENT_TYPE_DSP });
        comboRecipientTypes.select(DEFAULT_INDEX_RECIPIENT_TYPES);

        Label labelRecipients = new Label(mainPanel, SWT.NONE);
        labelRecipients.setLayoutData(createLabelLayoutData());
        labelRecipients.setText(Messages.Recipients_colon);

        comboRecipient = WidgetFactory.createCombo(mainPanel);
        comboRecipient.setTextLimit(10);
        comboRecipient.setLayoutData(createInputFieldLayoutData());
        comboRecipient.setItems(new String[] { RECIPIENT_LIST, QEZSNDMG.RECIPIENT_ALL, QEZSNDMG.RECIPIENT_ALLACT, QEZSNDMG.RECIPIENT_SYSOPR });
        comboRecipient.select(DEFAULT_INDEX_RECIPIENT);
        comboRecipient.addVerifyListener(new UpperCaseOnlyVerifier());
        comboRecipient.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                setControlEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        comboRecipient.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setControlEnablement();
            }
        });

        new Label(mainPanel, SWT.NONE); // place holder

        receipientsEditor = new StringListEditor(mainPanel, SWT.NONE);
        receipientsEditor.setTextLimit(10);
        receipientsEditor.setEnableLowerCase(true);

        createStatusLine(mainPanel);

        scrolledComposite.setContent(mainPanel);
        scrolledComposite.setMinSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        loadScreenValues();

        setControlEnablement();

        return scrolledComposite;
    }

    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {

        Button button = super.createButton(parent, id, label, defaultButton);
        if (defaultButton) {
            receipientsEditor.setParentDefaultButton(button);
        }

        return button;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button buttonReset = createButton(parent, BUTTON_RESET_ID, Messages.RESET_LABEL, false);
        if (buttonReset != null) {
            buttonReset.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent arg0) {
                    resetPressed();
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });
        }
        super.createButtonsForButtonBar(parent);
    }

    private GridData createLabelLayoutData() {
        return new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    }

    private GridData createInputFieldLayoutData() {
        return new GridData(100, SWT.DEFAULT);
    }

    private void setControlEnablement() {

        if (RECIPIENT_LIST.equals(comboRecipient.getText())) {
            receipientsEditor.setEnabled(true);
        } else {
            receipientsEditor.setEnabled(false);
        }
    }

    @Override
    public int open() {

        if (getShell() == null) {
            // create the window
            create();
        }

        getShell().forceActive();

        return super.open();
    }

    private void resetPressed() {
        performReset();
    }

    private void performReset() {

        comboMessageType.select(DEFAULT_INDEX_MESSAGE_TYPE);
        comboDeliveryMode.select(DEFAULT_INDEX_DELIVERY_MODE);
        textMessageText.setText(""); //$NON-NLS-1$
        comboRecipientTypes.select(DEFAULT_INDEX_RECIPIENT_TYPES);
        comboRecipient.select(DEFAULT_INDEX_RECIPIENT);
        comboRecipient.setText(""); //$NON-NLS-1$

        overWriteInitialValues();

        receipientsEditor.clearAll();

        setControlEnablement();

        setErrorMessage(null);
    }

    @Override
    public void okPressed() {

        if (!validateInput()) {
            return;
        }

        storeInput();

        storeScreenValues();

        super.okPressed();
    }

    private boolean validateInput() {

        setErrorMessage(null);

        if (textMessageText.getText().trim().length() <= 0) {
            setErrorMessage(Messages.Message_text_is_missing);
            textMessageText.setFocus();
            return false;
        }

        if (!validateRecipient(comboRecipient.getText().trim())) {
            comboRecipient.setFocus();
            return false;
        }

        boolean isALL = false;
        boolean isSysOpr = false;
        if (RECIPIENT_LIST.equals(comboRecipient.getText())) {
            String[] recipients = receipientsEditor.getItems();
            if (recipients.length <= 0) {
                setErrorMessage(Messages.Recipients_are_missing);
                receipientsEditor.setFocus();
                return false;
            }

            for (int i = 0; i < recipients.length; i++) {
                if (!validateRecipient(recipients[i])) {
                    receipientsEditor.setFocus(i);
                    return false;
                }
                if (QEZSNDMG.RECIPIENT_ALL.equals(recipients[i])) {
                    isALL = true;
                }
                if (QEZSNDMG.RECIPIENT_SYSOPR.equals(recipients[i])) {
                    isALL = true;
                }
            }

            if (isALL && recipients.length > 1) {
                setErrorMessage(Messages.bind(Messages.A_must_be_the_only_item_in_the_list, QEZSNDMG.RECIPIENT_ALL));
                receipientsEditor.setFocus();
                return false;
            }
        } else {
            if (QEZSNDMG.RECIPIENT_ALL.equals(comboRecipient.getText())) {
                isALL = true;
            }
            if (QEZSNDMG.RECIPIENT_SYSOPR.equals(comboRecipient.getText())) {
                isSysOpr = true;
            }
        }

        if (isALL && QEZSNDMG.RECIPIENT_TYPE_DSP.equals(comboRecipientTypes.getText())) {
            setErrorMessage(Messages.bind(Messages.A_cannot_be_used_if_B_is_specified_for_the_C_parameter, new String[] { QEZSNDMG.RECIPIENT_ALL,
                QEZSNDMG.RECIPIENT_TYPE_DSP, Messages.Recipient_type }));
            receipientsEditor.setFocus();
            return false;
        }
        if (isSysOpr && QEZSNDMG.RECIPIENT_TYPE_DSP.equals(comboRecipientTypes.getText())) {
            setErrorMessage(Messages.bind(Messages.A_cannot_be_used_if_B_is_specified_for_the_C_parameter, new String[] { QEZSNDMG.RECIPIENT_SYSOPR,
                QEZSNDMG.RECIPIENT_TYPE_DSP, Messages.Recipient_type }));
            receipientsEditor.setFocus();
            return false;
        }

        return true;
    }

    public boolean validateRecipient(String recipient) {

        if (QEZSNDMG.RECIPIENT_ALL.equals(recipient) || QEZSNDMG.RECIPIENT_ALLACT.equals(recipient) || QEZSNDMG.RECIPIENT_SYSOPR.equals(recipient)
            || RECIPIENT_LIST.equals(recipient)) {
            return true;
        }

        if (recipient.length() <= 0) {
            setErrorMessage(Messages.Recipients_are_missing);
            return false;
        }

        Validator validator = Validator.getNameInstance();
        if (!validator.validate(recipient)) {
            setErrorMessage(Messages.Invalid_recipient);
            return false;
        }

        return true;
    }

    public void storeInput() {

        sendMessageOptions = new SendMessageOptions();

        sendMessageOptions.setMessageType(comboMessageType.getText());
        sendMessageOptions.setDeliveryMode(comboDeliveryMode.getText());
        sendMessageOptions.setMessageText(textMessageText.getText());
        sendMessageOptions.setRecipientType(comboRecipientTypes.getText());

        String recipient = comboRecipient.getText().trim();
        if (RECIPIENT_LIST.equals(recipient)) {
            String[] recipients = receipientsEditor.getItems();
            sendMessageOptions.setRecipients(recipients);
        } else {
            sendMessageOptions.setRecipients(new String[] { comboRecipient.getText() });
        }

    }

    public SendMessageOptions getInput() {
        return sendMessageOptions;
    }

    /**
     * Restores the screen values of the last copy operation.
     */
    private void loadScreenValues() {

        comboMessageType.setText(loadValue(MESSAGE_TYPE, comboMessageType.getItems()[0]));
        comboDeliveryMode.setText(loadValue(DELIVERY_MODE, comboDeliveryMode.getItems()[0]));
        textMessageText.setText(loadValue(MESSAGE_TEXT, "")); //$NON-NLS-1$
        comboRecipientTypes.setText(loadValue(MESSAGE_RECIPIENT_TYPES, comboRecipientTypes.getItem(0)));
        comboRecipient.setText(loadValue(MESSAGE_RECIPIENT, "")); //$NON-NLS-1$

        int count = loadIntValue(MESSAGE_RECIPIENTS_COUNT, 0);
        String[] recipients = new String[count];
        for (int i = 0; i < recipients.length; i++) {
            String recipient = loadValue(MESSAGE_RECIPIENT_ITEM + i, null);
            if (recipient != null) {
                recipients[i] = recipient;
            }
        }

        receipientsEditor.setItems(recipients);

        overWriteInitialValues();

        validateInput();
    }

    private void overWriteInitialValues() {

        if (overWriteMessageText != null) {
            textMessageText.setText(overWriteMessageText);
        }
    }

    /**
     * Stores the screen values that are preserved for the next copy operation.
     */
    private void storeScreenValues() {

        storeValue(MESSAGE_TYPE, comboMessageType.getText());
        storeValue(DELIVERY_MODE, comboDeliveryMode.getText());
        storeValue(MESSAGE_TEXT, textMessageText.getText());
        storeValue(MESSAGE_RECIPIENT_TYPES, comboRecipientTypes.getText());
        storeValue(MESSAGE_RECIPIENT, comboRecipient.getText());

        String[] recipients = receipientsEditor.getItems();
        storeValue(MESSAGE_RECIPIENTS_COUNT, recipients.length);
        for (int i = 0; i < recipients.length; i++) {
            storeValue(MESSAGE_RECIPIENT_ITEM + i, recipients[i]);
        }

    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return getShell().computeSize(SWT.DEFAULT, 600, true);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

}
