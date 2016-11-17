/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.perspective.rse;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public abstract class AbstractJobLogExplorerPerspectiveLayout implements IPerspectiveFactory {

    public static final String ID = "biz.isphere.joblogexplorer.rse.perspective.JobLogExplorerPerspectiveLayout";//$NON-NLS-1$

    private static final String NAV_FOLDER_ID = "biz.isphere.joblogexplorer.rse.perspective.JobLogExplorerPerspectiveLayout.NavFolder";//$NON-NLS-1$
    private static final String PROPS_FOLDER_ID = "biz.isphere.joblogexplorer.rse.perspective.JobLogExplorerPerspectiveLayout.PropsFolder";//$NON-NLS-1$
    private static final String CMDLOG_FOLDER_ID = "biz.isphere.joblogexplorer.rse.perspective.JobLogExplorerPerspectiveLayout.CmdLogFolder";//$NON-NLS-1$

    public void createInitialLayout(IPageLayout layout) {

        defineLayout(layout);
    }

    private void defineLayout(IPageLayout layout) {

        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        IFolderLayout folder;

        // Place remote system view to left of editor area.
        folder = layout.createFolder(NAV_FOLDER_ID, IPageLayout.LEFT, 0.25F, editorArea);
        folder.addView(getRemoveSystemsViewID());

        // Place properties view below remote system view.
        folder = layout.createFolder(PROPS_FOLDER_ID, IPageLayout.BOTTOM, 0.75F, NAV_FOLDER_ID);
        folder.addView("org.eclipse.ui.views.PropertySheet");

        // Place command log view below editor area.
        folder = layout.createFolder(CMDLOG_FOLDER_ID, IPageLayout.BOTTOM, 0.75F, editorArea);
        folder.addView("com.ibm.etools.iseries.rse.ui.view.cmdlog");

        layout.addShowViewShortcut(getRemoveSystemsViewID());
        layout.addShowViewShortcut("org.eclipse.ui.views.PropertySheet");
        layout.addShowViewShortcut("com.ibm.etools.iseries.rse.ui.view.cmdlog");

        layout.addPerspectiveShortcut(ID);
    }

    protected abstract String getRemoveSystemsViewID();
}