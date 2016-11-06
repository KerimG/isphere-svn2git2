/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobloganalyzer.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.jobloganalyzer.editor.tableviewer.JobLogAnalyzerTableViewer;
import biz.isphere.jobloganalyzer.jobs.IDropFileListener;
import biz.isphere.jobloganalyzer.model.JobLog;
import biz.isphere.jobloganalyzer.model.JobLogReader;

public class JobLogAnalyzerEditor extends EditorPart implements IDropFileListener {

    public static final String ID = "biz.isphere.jobloganalyzer.editor.JobLogAnalyzerEditor";

    private JobLogAnalyzerTableViewer viewer;

    public JobLogAnalyzerEditor() {
        return;
    }

    @Override
    public void doSave(IProgressMonitor monitor) {

        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        setPartName("Job Log Analyzer");
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void createPartControl(Composite parent) {

        Composite mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(createGridLayoutNoMargin());
        disableDropSupportOnComposite(mainArea);

        SashForm sashForm = new SashForm(mainArea, SWT.NONE);
        GridData sashFormLayoutData = createGridDataFillAndGrab();
        sashForm.setLayoutData(sashFormLayoutData);

        createLeftPanel(sashForm);
        createRightPanel(sashForm);
        sashForm.setWeights(new int[] { 8, 2 });
    }

    @Override
    public void setFocus() {
    }

    /*
     * Drag & drop support
     */

    public void dropJobLog(DroppedLocalFile droppedJobLog, Object target) {

        if (droppedJobLog == null) {
            MessageDialogAsync.displayError(getShell(), Messages.Dropped_object_does_not_match_expected_type);
            return;
        }

        JobLogReader reader = new JobLogReader();
        final JobLog jobLog = reader.loadFromStmf(droppedJobLog.getPathName());

        new UIJob("") {

            @Override
            public IStatus runInUIThread(IProgressMonitor arg0) {
                viewer.setInputData(jobLog);
                return Status.OK_STATUS;
            }
        }.schedule();
    }

    private void addDropSupportOnComposite(Composite dialogEditorComposite) {

        Transfer[] transferTypes = new Transfer[] { PluginTransfer.getInstance() };
        int operations = DND.DROP_MOVE | DND.DROP_COPY;
        DropTargetListener listener = createEditorDropListener(this);

        DropTarget dropTarget = new DropTarget(dialogEditorComposite, operations);
        dropTarget.setTransfer(transferTypes);
        dropTarget.addDropListener(listener);
        dropTarget.setData(this);
    }

    private void disableDropSupportOnComposite(Composite dialogEditorComposite) {

        Transfer[] transferTypes = new Transfer[] {};
        int operations = DND.DROP_NONE;
        DropTargetListener listener = new DropVetoListerner();

        DropTarget dropTarget = new DropTarget(dialogEditorComposite, operations);
        dropTarget.setTransfer(transferTypes);
        dropTarget.addDropListener(listener);
    }

    protected DropTargetAdapter createEditorDropListener(IDropFileListener editor) {
        return new DropFileListener(editor);
    }

    private Composite createLeftPanel(SashForm sashForm) {

        Composite leftMainPanel = new Composite(sashForm, SWT.BORDER);
        leftMainPanel.setLayout(createGridLayoutWithMargin());
        leftMainPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        new Label(leftMainPanel, SWT.NONE).setText("Left Panel");

        Label separator = new Label(leftMainPanel, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(createGridDataFillAndGrab(1));

        Composite tableArea = new Composite(leftMainPanel, SWT.BORDER);
        tableArea.setLayout(createGridLayoutNoMargin());
        tableArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        viewer = new JobLogAnalyzerTableViewer();
        viewer.createViewer(tableArea);

        addDropSupportOnComposite(leftMainPanel);

        return leftMainPanel;
    }

    private Composite createRightPanel(SashForm sashForm) {

        Composite rightMainPanel = new Composite(sashForm, SWT.BORDER);
        rightMainPanel.setLayout(createGridLayoutWithMargin());
        rightMainPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        new Label(rightMainPanel, SWT.NONE).setText("Right Panel");

        Label separator = new Label(rightMainPanel, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(createGridDataFillAndGrab(1));

        Composite detailsArea = new Composite(rightMainPanel, SWT.BORDER);
        detailsArea.setLayout(createGridLayoutNoMargin());
        detailsArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        return rightMainPanel;
    }

    protected Shell getShell() {
        return this.getSite().getShell();
    }

    private GridLayout createGridLayoutNoMargin() {
        return createGridLayoutNoMargingSimple(1);
    }

    private GridLayout createGridLayoutNoMargingSimple(int columns) {
        GridLayout editorLayout = new GridLayout(columns, false);
        editorLayout.marginHeight = 0;
        editorLayout.marginWidth = 0;
        return editorLayout;
    }

    private GridLayout createGridLayoutWithMargin() {
        return createGridLayoutWithMargin(1);
    }

    private GridLayout createGridLayoutWithMargin(int columns) {
        GridLayout treeAreaLayout = createGridLayoutNoMargingSimple(columns);
        treeAreaLayout.marginHeight = 10;
        treeAreaLayout.marginWidth = 10;
        return treeAreaLayout;
    }

    private GridData createGridDataSimple() {
        return new GridData();
    }

    private GridData createGridDataSimple(int columns) {
        GridData gridData = createGridDataSimple();
        gridData.horizontalSpan = columns;
        return gridData;
    }

    private GridData createGridDataFillAndGrab() {
        GridData layoutData = createGridDataSimple();
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.verticalAlignment = SWT.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        return layoutData;
    }

    private GridData createGridDataFillAndGrab(int numColumns) {
        GridData layoutData = createGridDataFillAndGrab();
        layoutData.horizontalSpan = numColumns;
        layoutData.grabExcessVerticalSpace = false;
        return layoutData;
    }
}
