/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public final class UIHelper {

    private UIHelper() {
    }

    public static IWorkbenchPage getActivePage() {

        IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null) {
            return null;
        }

        IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
        if (null == activeWorkbenchWindow) {
            activeWorkbenchWindow = workbench.getWorkbenchWindows()[0];
        }

        return activeWorkbenchWindow.getActivePage();
    }

    public static IWorkbenchPart getActivePart() {

        IWorkbenchPage activePage = UIHelper.getActivePage();
        if (activePage == null) {
            return null;
        }

        return activePage.getActivePart();
    }

    public static IEditorPart getActiveEditor() {

        IWorkbenchPage activePage = getActivePage();
        if (activePage == null) {
            return null;
        }

        return activePage.getActiveEditor();
    }
}
