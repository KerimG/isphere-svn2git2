/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobloganalyzer.editor.tableviewer;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.jobloganalyzer.model.JobLogMessage;

/**
 * This class implements an ICellModifier An ICellModifier is called when the
 * user modifes a cell in the tableViewer
 */

public class JobLogAnalyzerCellModifier implements ICellModifier, JobLogAnalyzerTableColumns {
    private JobLogAnalyzerTableViewer tableViewer;

    /**
     * Constructor
     * 
     * @param TableViewerExample an instance of a TableViewerExample
     */
    public JobLogAnalyzerCellModifier(JobLogAnalyzerTableViewer tableViewer) {
        super();
        this.tableViewer = tableViewer;
    }

    /**
     * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
     *      java.lang.String)
     */
    public boolean canModify(Object element, String property) {

        // Find the index of the column
        int columnIndex = tableViewer.getColumnNames().indexOf(property);

        switch (columnIndex) {
        case JobLogAnalyzerTableColumns.COLUMN_SELECTED:
            return true;
        default:
            return false;
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
     *      java.lang.String)
     */
    public Object getValue(Object element, String property) {

        // Find the index of the column
        int columnIndex = tableViewer.getColumnNames().indexOf(property);

        Object result = null;
        JobLogMessage jobLogMessage = (JobLogMessage)element;

        switch (columnIndex) {
        case JobLogAnalyzerTableColumns.COLUMN_SELECTED:
            result = new Boolean(jobLogMessage.isSelected());
            break;
        default:
            result = "";
        }
        return result;
    }

    /**
     * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
     *      java.lang.String, java.lang.Object)
     */
    public void modify(Object element, String property, Object value) {

        // Find the index of the column
        int columnIndex = tableViewer.getColumnNames().indexOf(property);

        TableItem item = (TableItem)element;
        JobLogMessage jobLogMessages = (JobLogMessage)item.getData();

        switch (columnIndex) {
        case JobLogAnalyzerTableColumns.COLUMN_SELECTED:
            jobLogMessages.setSelected(((Boolean)value).booleanValue());
            break;
        default:
        }

        tableViewer.updateJobLogMessage(jobLogMessages);
    }
}
