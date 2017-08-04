/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.JournalEntryComparator;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.ui.dialogs.SelectEntriesToCompareDialog;
import biz.isphere.journalexplorer.core.ui.dialogs.SideBySideCompareDialog;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntryDetailsViewer;

public class JournalEntryDetailsView extends ViewPart implements ISelectionListener, ISelectionChangedListener {

    public static final String ID = "biz.isphere.journalexplorer.core.ui.views.JournalEntryDetailsView"; //$NON-NLS-1$

    private JournalEntryDetailsViewer viewer;

    public JournalEntryDetailsView() {
    }

    @Override
    public void createPartControl(Composite parent) {

        this.viewer = new JournalEntryDetailsViewer(parent);
        this.viewer.addSelectionChangedListener(this);
        this.getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
        this.createActions();
        this.createToolBar();
        this.setActionEnablement(viewer.getSelection());
    }

    @Override
    public void dispose() {
        ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
        selectionService.removeSelectionListener(this);

        super.dispose();
    };

    private void createActions() {

    }

    protected void showSideBySideEntries() {

        Object[] input = getSelectedItems();

        if (input instanceof Object[]) {

            SideBySideCompareDialog sideBySideCompareDialog = new SideBySideCompareDialog(this.getSite().getShell());
            sideBySideCompareDialog.create();

            if (input.length == 2) {
                sideBySideCompareDialog.setInput((JournalProperties)input[0], (JournalProperties)input[1]);
                sideBySideCompareDialog.open();
            } else {

                SelectEntriesToCompareDialog selectEntriesToCompareDialog = new SelectEntriesToCompareDialog(this.getSite().getShell());
                selectEntriesToCompareDialog.create();
                selectEntriesToCompareDialog.setInput(input);

                if (selectEntriesToCompareDialog.open() == Window.OK) {
                    sideBySideCompareDialog.setInput((JournalProperties)selectEntriesToCompareDialog.getLeftEntry(),
                        (JournalProperties)selectEntriesToCompareDialog.getRightEntry());

                    sideBySideCompareDialog.open();
                }
            }
        }
    }

    protected void compareJOESDEntries() {

        Object[] input = getSelectedItems();

        if (input instanceof Object[]) {

            if (input.length == 2) {
                this.compareEntries(input[0], input[1]);
            } else {

                SelectEntriesToCompareDialog selectEntriesToCompareDialog = new SelectEntriesToCompareDialog(this.getSite().getShell());
                selectEntriesToCompareDialog.create();
                selectEntriesToCompareDialog.setInput(input);

                if (selectEntriesToCompareDialog.open() == Window.OK) {
                    this.compareEntries(selectEntriesToCompareDialog.getLeftEntry(), selectEntriesToCompareDialog.getRightEntry());
                }
            }
        } else {
            MessageDialog.openError(this.getSite().getShell(), Messages.E_R_R_O_R, Messages.JournalEntryView_UncomparableEntries); //$NON-NLS-1$
        }
    }

    private void compareEntries(Object leftObject, Object rightObject) {

        if (leftObject instanceof JournalProperties && rightObject instanceof JournalProperties) {

            JournalProperties left = (JournalProperties)leftObject;
            JournalProperties right = (JournalProperties)rightObject;

            new JournalEntryComparator().compare(left, right);
            this.viewer.refresh(true);

        } else {
            MessageDialog.openError(this.getSite().getShell(), Messages.E_R_R_O_R, Messages.JournalEntryView_UncomparableEntries); //$NON-NLS-1$
        }
    }

    private void createToolBar() {
        // IToolBarManager toolBarManager =
        // getViewSite().getActionBars().getToolBarManager();
        // toolBarManager.add(this.compare);
        // toolBarManager.add(this.showSideBySide);
        // toolBarManager.add(new Separator());
        // toolBarManager.add(this.openParserAssociations);
        // toolBarManager.add(this.reParseEntries);
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    // /
    // / ISelectionListener methods
    // /
    public void selectionChanged(IWorkbenchPart viewPart, ISelection selection) {

        @SuppressWarnings("rawtypes")
        Iterator structuredSelectionList;

        @SuppressWarnings("rawtypes")
        Iterator structuredSelectionElement;
        Object currentSelection;
        ArrayList<JournalProperties> input = new ArrayList<JournalProperties>();

        // if (viewPart instanceof JournalEntryView) {
        // structuredSelectionList =
        // ((IStructuredSelection)selection).iterator();
        // if (structuredSelectionList.hasNext()) {
        // structuredSelectionElement =
        // ((IStructuredSelection)structuredSelectionList.next()).iterator();
        // if (structuredSelectionElement.hasNext()) {
        // currentSelection = structuredSelectionElement.next();
        // if (currentSelection instanceof JournalProperties) {
        // input.add((JournalProperties)currentSelection);
        // }
        // }
        // }
        //
        // refreshViewer(input);
        // }

        if (viewPart instanceof JournalExplorerView || viewPart instanceof JournalEntryView) {
            if (selection instanceof IStructuredSelection) {
                structuredSelectionList = ((IStructuredSelection)selection).iterator();
                while (input.size() < 2 && structuredSelectionList.hasNext()) {
                    structuredSelectionElement = ((IStructuredSelection)structuredSelectionList.next()).iterator();
                    while (input.size() < 2 && structuredSelectionElement.hasNext()) {
                        currentSelection = structuredSelectionElement.next();
                        if (currentSelection instanceof JournalEntry) {
                            input.add(new JournalProperties((JournalEntry)currentSelection));
                        } else if (currentSelection instanceof JournalProperties) {
                            input.add((JournalProperties)currentSelection);
                        }

                    }
                }

                if (input.size() > 1) {
                    input.clear();
                }

                refreshViewer(input);
            }
        }

        setActionEnablement(viewer.getSelection());
    }

    private void refreshViewer(ArrayList<JournalProperties> input) {

        this.viewer.setInput(input.toArray());

        // Restore tree state
        this.viewer.expandAll();
    }

    public void selectionChanged(SelectionChangedEvent event) {

        ITreeSelection selection = getSelection(event);
        setActionEnablement(selection);
    }

    private void setActionEnablement(ISelection selection) {

        // ITreeSelection treeSelection;
        // if (selection instanceof ITreeSelection) {
        // treeSelection = (ITreeSelection)selection;
        // } else {
        // treeSelection = null;
        // }
        //
        // if (treeSelection != null) {
        // if (treeSelection != null && treeSelection.size() == 2) {
        // compare.setEnabled(true);
        // showSideBySide.setEnabled(true);
        // } else {
        // showSideBySide.setEnabled(false);
        // compare.setEnabled(false);
        // }
        //
        // Object[] items = getInput();
        //
        // if (items != null && items.length > 0) {
        // openParserAssociations.setEnabled(true);
        // reParseEntries.setEnabled(true);
        // } else {
        // openParserAssociations.setEnabled(false);
        // reParseEntries.setEnabled(false);
        // }
        // }
    }

    private JournalProperties[] getSelectedItems() {

        List<JournalProperties> selectedItems = new ArrayList<JournalProperties>();

        ITreeSelection selection = getSelection();
        Iterator<?> iterator = selection.iterator();

        Object currentItem;
        while (iterator.hasNext()) {

            currentItem = iterator.next();
            if (currentItem instanceof JournalProperties) {
                selectedItems.add((JournalProperties)currentItem);
            }
        }

        return selectedItems.toArray(new JournalProperties[selectedItems.size()]);
    }

    private ITreeSelection getSelection() {

        ISelection selection = viewer.getSelection();
        if (selection instanceof ITreeSelection) {
            return (ITreeSelection)selection;
        }

        return null;
    }

    private ITreeSelection getSelection(SelectionChangedEvent event) {

        ISelection selection = event.getSelection();
        if (selection instanceof ITreeSelection) {
            return (ITreeSelection)selection;
        }

        return null;
    }
}