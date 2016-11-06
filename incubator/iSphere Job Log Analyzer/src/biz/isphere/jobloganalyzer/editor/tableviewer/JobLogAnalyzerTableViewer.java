/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobloganalyzer.editor.tableviewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.jobloganalyzer.model.JobLog;
import biz.isphere.jobloganalyzer.model.JobLogMessage;

public class JobLogAnalyzerTableViewer implements IJobLogMessagesViewer, JobLogAnalyzerTableColumns {

    public enum Columns {
        SELECTED ("selected", COLUMN_SELECTED),
        DATE ("date", COLUMN_DATE),
        TIME ("time", COLUMN_TIME),
        ID ("id", COLUMN_ID),
        TYPE ("type", COLUMN_TYPE),
        SEVERITY ("severity", COLUMN_SEVERITY),
        TEXT ("text", COLUMN_TEXT),
        FROM_LIBRARY ("fromLibrary", COLUMN_FROM_LIBRARY),
        FROM_PROGRAM ("fromProgram", COLUMN_FROM_PROGRAM),
        FROM_STATEMENT ("fromStatement", COLUMN_FROM_STATEMENT),
        TO_LIBRARY ("toLibrary", COLUMN_TO_LIBRARY),
        TO_PROGRAM ("toProgram", COLUMN_TO_PROGRAM),
        TO_STATEMENT ("toStatement", COLUMN_TO_STATEMENT),
        FROM_MODULE ("fromModule", COLUMN_FROM_MODULE),
        TO_MODULE ("toModule", COLUMN_TO_MODULE),
        FROM_PROCEDURE ("fromProcedure", COLUMN_FROM_PROCEDURE),
        TO_PROCEDURE ("toProcedure", COLUMN_TO_PROCEDURE);

        public final String name;
        public final int columnNumber;

        private Columns(String name, int columnNumber) {
            this.name = name;
            this.columnNumber = columnNumber;
        }

        public static String[] names() {

            List<String> names = new ArrayList<String>();
            for (Columns column : Columns.values()) {
                names.add(column.name);
            }

            return names.toArray(new String[names.size()]);
        }

    }

    private Table table;
    private TableViewer tableViewer;

    public void setInputData(JobLog jobLog) {

        tableViewer.setInput(jobLog);
    }

    public void createViewer(Composite composite) {

        // Create a composite to hold the children
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_BOTH);
        composite.setLayoutData(gridData);

        // Set numColumns to 3 for the buttons
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 4;
        composite.setLayout(layout);

        // Create the table
        createTable(composite);

        // Create and setup the TableViewer
        createTableViewer();
        tableViewer.setContentProvider(new JobLogAnalyzerContentProvider());
        tableViewer.setLabelProvider(new JobLogAnalyzerLabelProvider());

    }

    public List<String> getColumnNames() {
        return Arrays.asList(Columns.names());
    }

    /**
     * Create the Table
     */
    private void createTable(Composite parent) {
        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

        table = new Table(parent, style);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalSpan = 3;
        table.setLayoutData(gridData);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        // 1. column with image/checkboxes - NOTE: The SWT.CENTER has no
        // effect!!
        TableColumn column = new TableColumn(table, SWT.CENTER, Columns.SELECTED.columnNumber);
        column.setText("");
        column.setWidth(WIDTH_SELECTED);

        // 2. column with date sent
        column = new TableColumn(table, SWT.LEFT, Columns.DATE.columnNumber);
        column.setText("Date");
        column.setWidth(WIDTH_DATE);
        // Add listener to column so tasks are sorted by description when
        // clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.DESCRIPTION));
            }
        });

        // 3. column with time sent
        column = new TableColumn(table, SWT.LEFT, Columns.TIME.columnNumber);
        column.setText("Time");
        column.setWidth(WIDTH_TIME);
        // Add listener to column so tasks are sorted by owner when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.OWNER));
            }
        });

        // 4. column with message id
        column = new TableColumn(table, SWT.LEFT, Columns.ID.columnNumber);
        column.setText("Id");
        column.setWidth(WIDTH_ID);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 5. column with message type
        column = new TableColumn(table, SWT.LEFT, Columns.TYPE.columnNumber);
        column.setText("Type");
        column.setWidth(WIDTH_TYPE);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 6. column with message severity
        column = new TableColumn(table, SWT.CENTER, Columns.SEVERITY.columnNumber);
        column.setText("Severity");
        column.setWidth(WIDTH_SEVERITY);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 7. column with message text
        column = new TableColumn(table, SWT.LEFT, Columns.TEXT.columnNumber);
        column.setText("Text");
        column.setWidth(WIDTH_TEXT);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 8. column with from library
        column = new TableColumn(table, SWT.LEFT, Columns.FROM_LIBRARY.columnNumber);
        column.setText("From library");
        column.setWidth(WIDTH_FROM_LIBRARY);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 9. column with from program
        column = new TableColumn(table, SWT.LEFT, Columns.FROM_PROGRAM.columnNumber);
        column.setText("From program");
        column.setWidth(WIDTH_FROM_PROGRAM);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 10. column with from statement
        column = new TableColumn(table, SWT.LEFT, Columns.FROM_STATEMENT.columnNumber);
        column.setText("From statement");
        column.setWidth(WIDTH_FROM_STATEMENT);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 11. column with to library
        column = new TableColumn(table, SWT.LEFT, Columns.TO_LIBRARY.columnNumber);
        column.setText("To library");
        column.setWidth(WIDTH_TO_LIBRARY);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 12. column with to program
        column = new TableColumn(table, SWT.LEFT, Columns.TO_PROGRAM.columnNumber);
        column.setText("To program");
        column.setWidth(WIDTH_TO_PROGRAM);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 13. column with to statement
        column = new TableColumn(table, SWT.LEFT, Columns.TO_STATEMENT.columnNumber);
        column.setText("To statement");
        column.setWidth(WIDTH_TO_STATEMENT);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 14. column with from module
        column = new TableColumn(table, SWT.LEFT, Columns.FROM_MODULE.columnNumber);
        column.setText("From module");
        column.setWidth(WIDTH_FROM_MODULE);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 15. column with to module
        column = new TableColumn(table, SWT.LEFT, Columns.TO_MODULE.columnNumber);
        column.setText("To module");
        column.setWidth(WIDTH_TO_MODULE);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 16. column with from module
        column = new TableColumn(table, SWT.LEFT, Columns.FROM_PROCEDURE.columnNumber);
        column.setText("From procedure");
        column.setWidth(WIDTH_FROM_PROCEDURE);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });

        // 17. column with to module
        column = new TableColumn(table, SWT.LEFT, Columns.TO_PROCEDURE.columnNumber);
        column.setText("To procedure");
        column.setWidth(WIDTH_TO_PROCEDURE);
        // Add listener to column so tasks are sorted by percent when clicked
        column.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                // tableViewer.setSorter(new
                // ExampleTaskSorter(ExampleTaskSorter.PERCENT_COMPLETE));
            }
        });
    }

    /**
     * Create the TableViewer
     */
    private void createTableViewer() {

        tableViewer = new TableViewer(table);
        tableViewer.setUseHashlookup(true);

        tableViewer.setColumnProperties(Columns.names());

        // Create the cell editors
        CellEditor[] editors = new CellEditor[Columns.values().length];

        // Column 1 : Completed (Checkbox)
        editors[0] = new CheckboxCellEditor(table);

        // Column 2 : Description (Free text)
        // TextCellEditor textEditor = new TextCellEditor(table);
        // ((Text)textEditor.getControl()).setTextLimit(60);
        // editors[1] = textEditor;

        // Column 3 : Owner (Combo Box)
        // editors[2] = new ComboBoxCellEditor(table, taskList.getOwners(),
        // SWT.READ_ONLY);

        // Column 4 : Percent complete (Text with digits only)
        // textEditor = new TextCellEditor(table);
        // ((Text)textEditor.getControl()).addVerifyListener(
        //
        // new VerifyListener() {
        // public void verifyText(VerifyEvent e) {
        // // Here, we could use a RegExp such as the following
        // // if using JRE1.4 such as e.doit = e.text.matches("[\\-0-9]*");
        // e.doit = "0123456789".indexOf(e.text) >= 0;
        // }
        // });
        // editors[3] = textEditor;

        // Assign the cell editors to the viewer
        tableViewer.setCellEditors(editors);
        // Set the cell modifier for the viewer
        tableViewer.setCellModifier(new JobLogAnalyzerCellModifier(this));
        // Set the default sorter for the viewer
        // tableViewer.setSorter(new
        // ExampleTaskSorter(ExampleTaskSorter.DESCRIPTION));
    }

    public void updateJobLogMessage(JobLogMessage jobLogMessage) {
        tableViewer.update(jobLogMessage, null);
    }
}
