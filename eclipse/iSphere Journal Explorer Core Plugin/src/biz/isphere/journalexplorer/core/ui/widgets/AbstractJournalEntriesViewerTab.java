/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.IResizableTableColumnsViewer;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.sqleditor.SQLSyntaxErrorException;
import biz.isphere.core.swt.widgets.sqleditor.SqlEditor;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.model.SQLWhereClause;
import biz.isphere.journalexplorer.core.model.sqljep.NullValueVariable;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalViewerContentProvider;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalEntryLabelProvider;
import biz.isphere.journalexplorer.core.ui.model.AbstractTypeViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumn;
import biz.isphere.journalexplorer.core.ui.views.JournalEntryViewerView;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;

/**
 * This widget is a viewer for the journal entries of an output file of the
 * DSPJRN command. It is created by a sub-class of the
 * {@link AbstractTypeViewerFactory}. It is used by the "Journal Explorer" view
 * to create the tabs for the opened output files of the DSPJRN command.
 * 
 * @see JournalEntry
 * @see JournalEntryViewerView
 */
public abstract class AbstractJournalEntriesViewerTab extends CTabItem implements IResizableTableColumnsViewer, ISelectionChangedListener,
    ISelectionProvider, IPropertyChangeListener {

    private DialogSettingsManager dialogSettingsManager = null;

    private OutputFile outputFile;
    private Composite container;
    private Set<ISelectionChangedListener> selectionChangedListeners;
    private boolean isSqlEditorVisible;
    private SelectionListener loadJournalEntriesSelectionListener;

    private TableViewer tableViewer;
    private JournalEntries data;
    private Composite sqlEditorPanel;
    private Combo cboTableName;
    private SqlEditor sqlEditor;
    private SQLWhereClause filterClause;
    private SQLWhereClause selectClause;

    public AbstractJournalEntriesViewerTab(CTabFolder parent, OutputFile outputFile, SelectionListener loadJournalEntriesSelectionListener) {
        super(parent, SWT.NONE);

        setSqlEditorVisibility(false);

        this.outputFile = outputFile;
        this.container = new Composite(parent, SWT.NONE);
        this.selectionChangedListeners = new HashSet<ISelectionChangedListener>();
        this.isSqlEditorVisible = false;
        this.loadJournalEntriesSelectionListener = loadJournalEntriesSelectionListener;
        this.filterClause = null;
        this.selectClause = null;

        Preferences.getInstance().addPropertyChangeListener(this);
    }

    protected abstract String getLabel();

    protected abstract String getTooltip();

    protected OutputFile getOutputFile() {
        return outputFile;
    }

    private ContentAssistProposal[] getContentAssistProposals() {

        if (hasTableName()) {
            // TODO: get content assist proposals
            return JournalEntry.getBasicContentAssistProposals();
        } else {
            return JournalEntry.getBasicContentAssistProposals();
        }
    }

    private void createSqlEditor() {

        if (!isAvailable(sqlEditor)) {

            sqlEditorPanel = new Composite(getContainer(), SWT.BORDER);
            GridLayout ly_sqlEditorPanel = new GridLayout(3, false);
            sqlEditorPanel.setLayout(ly_sqlEditorPanel);
            sqlEditorPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            sqlEditor = new SqlEditor(sqlEditorPanel, getClass().getSimpleName(), getDialogSettingsManager(), SWT.NONE) {
                protected void createContentArea(Composite parent) {
                    setLayout(new GridLayout(3, false));
                    createTableEditorPanel(parent);
                    super.createContentArea(parent);
                };
            };

            sqlEditor.setContentAssistProposals(getContentAssistProposals());
            sqlEditor.addSelectionListener(loadJournalEntriesSelectionListener);
            sqlEditor.setWhereClause(getFilterClause().getClause());
            GridData gd_sqlEditor = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            gd_sqlEditor.heightHint = 160;
            gd_sqlEditor.horizontalSpan = ly_sqlEditorPanel.numColumns;
            sqlEditor.setLayoutData(gd_sqlEditor);
            getContainer().layout();
            sqlEditor.setFocus();
            sqlEditor.setBtnExecuteLabel(Messages.ButtonLabel_Filter);
            sqlEditor.setBtnExecuteToolTipText(Messages.ButtonTooltip_Filter);
            sqlEditor.addModifyListener(new SQLWhereClauseChangedListener());

            if (hasTableName()) {
                cboTableName.select(0);
            } else {
                cboTableName.setText(getFilterClause().getLibrary() + "/" + filterClause.getFile());
            }

            cboTableName.addModifyListener(new SQLWhereClauseChangedListener());
        }
    }

    private boolean hasTableName() {
        return StringHelper.isNullOrEmpty(getFilterClause().getFile()) || StringHelper.isNullOrEmpty(getFilterClause().getLibrary());
    }

    private void createTableEditorPanel(Composite parent) {

        Composite wherePanel = new Composite(parent, SWT.NONE);
        GridLayout wherePanelLayout = new GridLayout(1, false);
        wherePanelLayout.marginRight = wherePanelLayout.marginWidth;
        wherePanelLayout.marginHeight = 0;
        wherePanelLayout.marginWidth = 0;
        wherePanel.setLayout(wherePanelLayout);
        wherePanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label labelTableName = new Label(wherePanel, SWT.NONE);
        labelTableName.setText(Messages.JournalEntryView_Label_TableName);
        labelTableName.setToolTipText(Messages.JournalEntryView_Tooltip_TableName);

        Composite editorPanel = new Composite(parent, SWT.NONE);
        GridLayout editorPanelLayout = new GridLayout(2, false);
        editorPanelLayout.marginRight = wherePanelLayout.marginWidth;
        editorPanelLayout.marginHeight = 0;
        editorPanelLayout.marginWidth = 0;
        editorPanelLayout.verticalSpacing = -1;
        editorPanel.setLayout(editorPanelLayout);
        GridData gd_editorPanel = new GridData(GridData.FILL_HORIZONTAL);
        gd_editorPanel.horizontalSpan = 2;
        editorPanel.setLayoutData(gd_editorPanel);

        cboTableName = WidgetFactory.createUpperCaseCombo(editorPanel);
        GridData gd_tableName = new GridData();
        gd_tableName.widthHint = 200;
        cboTableName.setLayoutData(gd_tableName);
        cboTableName.setToolTipText(Messages.JournalEntryView_Tooltip_TableName);
        cboTableName.setItems(getTables());
        cboTableName.select(0);
        cboTableName.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent paramSelectionEvent) {
                // TODO: load content assist proposals from meta data cache
                sqlEditor.setContentAssistProposals(JournalEntry.getBasicContentAssistProposals());
            }
        });

        Button btnClear = WidgetFactory.createPushButton(editorPanel);
        btnClear.setText("X"); // //$NON-NLS-1$
        btnClear.setToolTipText(Messages.JournalEntryView_Tooltip_ClearTableName);
        btnClear.setLayoutData(new GridData());
        btnClear.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent paramSelectionEvent) {
                cboTableName.select(0);
            }
        });
    }

    private void destroySqlEditor() {

        if (sqlEditorPanel != null) {
            // Important, must be called to ensure the SqlEditor is removed from
            // the list of preferences listeners.
            sqlEditorPanel.dispose();
            sqlEditorPanel = null;
            getContainer().layout();
        }

    }

    protected void setSqlEditorEnabled(boolean enabled) {

        if (isSqlEditorVisible()) {
            sqlEditor.setEnabled(enabled);
        }
    }

    public void setFocusOnSqlEditor() {

        if (isSqlEditorVisible()) {
            sqlEditor.setFocus();
        }
    }

    public void storeSqlEditorHistory() {
        sqlEditor.storeHistory();
    }

    public void refreshSqlEditorHistory() {
        if (isAvailable(sqlEditor)) {
            sqlEditor.refreshHistory();
        }
    }

    protected void setSelectClause(SQLWhereClause whereClause) {
        this.selectClause = whereClause;
    }

    public SQLWhereClause getSelectClause() {
        return selectClause;
    }

    private void setFilterClause(SQLWhereClause whereClause) {
        this.filterClause = whereClause;
    }

    public SQLWhereClause getFilterClause() {

        if (filterClause == null) {
            setFilterClause(new SQLWhereClause());
        }

        return filterClause;
    }

    public void validateWhereClause(Shell shell, SQLWhereClause whereClause) throws SQLSyntaxErrorException {

        if (whereClause == null || !whereClause.hasClause()) {
            return;
        }

        try {

            HashMap<String, Integer> columnMapping = JournalEntry.getBasicColumnMapping();
            RowJEP sqljep = new ValidationRowJEP(whereClause);

            sqljep.parseExpression(columnMapping);
            sqljep.getValue(JournalEntry.getSampleRow());

        } catch (Exception e) {
            throw new SQLSyntaxErrorException(e);
        }

    }

    public boolean isFiltered() {
        return hasWhereClause();
    }

    private boolean hasWhereClause() {

        if (filterClause != null && filterClause.hasClause()) {
            return true;
        }

        if (selectClause != null && selectClause.hasClause()) {
            return true;
        }

        return false;
    }

    private boolean isAvailable(Control control) {

        if (control != null && !control.isDisposed()) {
            return true;
        }

        return false;
    }

    protected void initializeComponents() {

        setText(getLabel());
        setToolTipText(getTooltip());

        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tableViewer = createTableViewer(container);
        container.layout(true);
        setControl(container);
    }

    protected DialogSettingsManager getDialogSettingsManager() {

        if (dialogSettingsManager == null) {
            dialogSettingsManager = new DialogSettingsManager(ISphereJournalExplorerCorePlugin.getDefault().getDialogSettings(),
                AbstractJournalEntriesViewerTab.class);
        }
        return dialogSettingsManager;
    }

    protected Composite getContainer() {
        return container;
    }

    public void resetColumnWidths() {
        dialogSettingsManager.resetColumnWidths(tableViewer.getTable());
    }

    public boolean hasSqlEditor() {
        return false;
    }

    public boolean isSqlEditorVisible() {
        return isSqlEditorVisible;
    }

    public void setSqlEditorVisibility(boolean visible) {

        if (!hasSqlEditor()) {
            this.isSqlEditorVisible = false;
        } else {
            this.isSqlEditorVisible = visible;
        }

        setSqlEditorEnablement();
    }

    private void setSqlEditorEnablement() {

        if (hasSqlEditor()) {
            if (isSqlEditorVisible()) {
                createSqlEditor();
            } else {
                destroySqlEditor();
            }
        }
    }

    public JournalEntryColumn[] getColumns() {
        return getLabelProvider().getColumns();
    }

    private String[] getTables() {

        List<String> tables = new ArrayList<String>();

        tables.add("*");

        List<MetaTable> metaTables = new ArrayList<MetaTable>(MetaDataCache.getInstance().getCachedParsers());
        Collections.sort(metaTables, new Comparator<MetaTable>() {
            public int compare(MetaTable o1, MetaTable o2) {
                int result = compareTo(o1, o2);
                if (result != 0) {
                    return result;
                } else {
                    result = compareTo(o1.getLibrary(), o2.getLibrary());
                    if (result != 0) {
                        return result;
                    } else {
                        result = compareTo(o1.getName(), o2.getName());
                        if (result != 0) {
                            return result;
                        } else {
                            return o1.getName().compareTo(o2.getName());
                        }

                    }
                }
            }

            private int compareTo(Object o1, Object o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                } else {
                    if (o1 == null && o2 != null) {
                        return -1;
                    } else if (o1 != null && o2 == null) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });

        for (MetaTable metaTable : metaTables) {
            tables.add(metaTable.getQualifiedName());
        }

        return tables.toArray(new String[tables.size()]);
    }

    protected abstract TableViewer createTableViewer(Composite container);

    public abstract void openJournal(JournalExplorerView view, SQLWhereClause whereClause, SQLWhereClause filterWhereClause) throws Exception;

    public abstract void filterJournal(JournalExplorerView view, SQLWhereClause whereClause) throws Exception;

    public abstract void closeJournal();

    public abstract boolean isLoading();

    protected void setInputData(JournalEntries data) {

        if (data != null) {
            MetaDataCache.getInstance().preloadTables(data);
        }

        this.data = data;

        container.layout(true);
        tableViewer.setInput(null);
        tableViewer.setUseHashlookup(true);

        if (data != null) {
            tableViewer.setItemCount(data.size());
            tableViewer.setInput(data);
            tableViewer.getTable().setEnabled(true);
        } else {
            tableViewer.setItemCount(0);
            tableViewer.getTable().setEnabled(false);
        }

        tableViewer.setSelection(null);
    }

    @Override
    public void dispose() {

        if (data != null) {

            data.clear();
            data = null;
        }

        if (tableViewer != null) {

            tableViewer.getTable().dispose();
            tableViewer = null;
        }

        Preferences.getInstance().removePropertyChangeListener(this);

        super.dispose();
    }

    public void setAsSelectionProvider(SelectionProviderIntermediate selectionProvider) {
        selectionProvider.setSelectionProviderDelegate(tableViewer);
    }

    public void removeAsSelectionProvider(SelectionProviderIntermediate selectionProvider) {
        selectionProvider.removeSelectionProviderDelegate(tableViewer);
    }

    private void refreshTable() {
        if (tableViewer != null) {
            tableViewer.refresh(true);
        }

    }

    public StructuredSelection getSelection() {

        ISelection selection = tableViewer.getSelection();
        if (selection instanceof StructuredSelection) {
            return (StructuredSelection)selection;
        }

        return new StructuredSelection(new JournalEntry[0]);
    }

    public void setSelection(ISelection selection) {
        // satisfy the ISelectionProvider interface
        tableViewer.setSelection(selection);
    }

    public JournalEntry[] getSelectedItems() {

        List<JournalEntry> selectedItems = new LinkedList<JournalEntry>();

        StructuredSelection selection = getSelection();
        Iterator<?> iterator = selection.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof JournalEntry) {
                selectedItems.add((JournalEntry)object);
            }
        }

        return selectedItems.toArray(new JournalEntry[selectedItems.size()]);
    }

    public JournalEntries getInput() {

        JournalViewerContentProvider contentProvider = getContentProvider();
        return contentProvider.getInput();
    }

    private JournalViewerContentProvider getContentProvider() {
        return (JournalViewerContentProvider)tableViewer.getContentProvider();
    }

    private JournalEntryLabelProvider getLabelProvider() {
        return (JournalEntryLabelProvider)tableViewer.getLabelProvider();
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.remove(listener);
    }

    public void selectionChanged(SelectionChangedEvent event) {

        SelectionChangedEvent newEvent = new SelectionChangedEvent(this, event.getSelection());

        for (ISelectionChangedListener selectionChangedListener : selectionChangedListeners) {
            selectionChangedListener.selectionChanged(newEvent);
        }

        if (isSqlEditorVisible) {
            sqlEditor.setContentAssistProposals(getContentAssistProposals());
        }
    }

    public void propertyChange(PropertyChangeEvent event) {

        if (event.getProperty() == null) {
            return;
        }

        if (Preferences.HIGHLIGHT_USER_ENTRIES.equals(event.getProperty())) {
            refreshTable();
            return;
        }

        if (Preferences.ENABLED.equals(event.getProperty())) {
            refreshTable();
            return;
        }

        if (event.getProperty().startsWith(Preferences.COLORS)) {
            JournalEntryLabelProvider labelProvider = (JournalEntryLabelProvider)tableViewer.getLabelProvider();
            String columnName = event.getProperty().substring(Preferences.COLORS.length());
            Object object = event.getNewValue();
            if (object instanceof String) {
                String rgb = (String)event.getNewValue();
                if (columnName != null) {
                    Color color = ISphereJournalExplorerCorePlugin.getDefault().getColor(rgb);
                    labelProvider.setColumnColor(columnName, color);
                }
            }
            refreshTable();
            return;
        }
    }

    protected MetaTable getMetaData() {

        try {
            return MetaDataCache.getInstance().retrieveMetaData(getOutputFile());
        } catch (Exception e) {
            String fileName;
            if (getOutputFile() == null) {
                fileName = "null"; //$NON-NLS-1$
            } else {
                fileName = getOutputFile().toString();
            }
            ISpherePlugin.logError("*** Could not load meta data of file '" + fileName + "' ***", e); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }
    }

    private String[] splitTableName(String qualifiedTableName) {
        return qualifiedTableName.split("[./]");
    }

    private class SQLWhereClauseChangedListener implements ModifyListener {

        public void modifyText(ModifyEvent event) {

            String fileName = "";
            String libraryName = "";
            String qualifiedTableName = cboTableName.getText();

            if (!qualifiedTableName.startsWith("*")) {
                String[] parts = splitTableName(qualifiedTableName);
                if (parts.length == 2) {
                    libraryName = parts[0];
                    fileName = parts[1];
                } else if (parts.length == 1) {
                    libraryName = "";
                    fileName = parts[0];
                }
            }

            String whereClause = sqlEditor.getWhereClause().trim();

            setFilterClause(new SQLWhereClause(fileName, libraryName, whereClause));
        }
    }

    // RowJEP, which ignores undefined fields
    private class ValidationRowJEP extends RowJEP {

        private SQLWhereClause whereClause;

        public ValidationRowJEP(SQLWhereClause whereClause) {
            super(whereClause.getClause());
            this.whereClause = whereClause;
        }

        @Override
        public Entry<String, Comparable<?>> getVariable(String name) throws ParseException {
            Entry<String, Comparable<?>> variable = null;
            if (name.startsWith("JO")) {
                variable = super.getVariable(name); // JO*
            } else if (!noTableSpecified()) {
                variable = new NullValueVariable(name); // XY*/with table
                whereClause.setSpecificFields(true);
            }
            return variable; // JO*
        }

        public int findColumn(String name) {
            int result = super.findColumn(name);
            if (result < 0) {
                result = -1; // XY*/no table
            }
            return result;
        }

        @Override
        public Comparable<?> getColumnObject(int index) throws ParseException {
            return super.getColumnObject(index); // JO*
        }

        private boolean noTableSpecified() {
            String library = whereClause.getLibrary();
            String file = whereClause.getFile();
            return StringHelper.isNullOrEmpty(library) || StringHelper.isNullOrEmpty(file);
        };
    }
}
