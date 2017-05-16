/******************************************************************************
 * Copyright (c) 2009-2016 Telink Semiconductor Co., LTD.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * -----------------------------------------------------------------------------
 * Module:
 * Purpose:
 * Reference :   
 * $Id: DirectoryNotStrictVariableFieldEditor.java 851 20.1.08-07 19:37:00Z innot $
 *******************************************************************************//******************************************************************************
 * Copyright (c) 2009-2016 Telink Semiconductor Co., LTD.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * -----------------------------------------------------------------------------
 * Module:
 * Purpose:
 * Reference :   
 * $Id: PCDBOutputListener.java 851 20.1.08-07 19:37:00Z innot $
 *     
 *******************************************************************************/

package com.telink.tc32eclipse.core.targets.tools;

import org.eclipse.core.runtime.IProgressMonitor;

import com.telink.tc32eclipse.core.tcdb.TCDBException.Reason;
import com.telink.tc32eclipse.core.toolinfo.ICommandOutputListener;

/**
 * Class to listen to the line-by-line output of PCDB and cancels the operation if certain key
 * strings appears in the output.
 * <p>
 * They are:
 * <ul>
 * <li><code>timeout</code></li>
 * <li><code>Can't open device</code></li>
 * <li><code>can't open config file</code></li>
 * <li><code>Can't find programmer id</code></li>
 * <li><code>TC32 Part ???? not found</code></li>
 * </ul>
 * </p>
 * <p>
 * Once any of these Strings is found in the output the associated Reason is set and PCDB is
 * aborted via the ProgressMonitor.
 * </p>
 * 
 * @author Peter Shieh
 * @since 0.1
 * 
 */
public class TCDBOutputListener implements ICommandOutputListener {

	private IProgressMonitor	fProgressMonitor;
	private Reason				fAbortReason;
	private String				fAbortLine;

	/*
	 * (non-Javadoc)
	 * @see com.telink.tc32eclipse.core.toolinfo.ICommandOutputListener#init(org.eclipse.core.runtime
	 * .IProgressMonitor)
	 */
	public void init(IProgressMonitor monitor) {
		fProgressMonitor = monitor;
		fAbortLine = null;
		fAbortReason = null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.telink.tc32eclipse.core.toolinfo.ICommandOutputListener#handleLine(java.lang.String,
	 * com.telink.tc32eclipse.core.toolinfo.ICommandOutputListener.StreamSource)
	 */
	public void handleLine(String line, StreamSource source) {

		boolean abort = false;

		if (line.contains("timeout")) {
			abort = true;
			fAbortReason = Reason.TIMEOUT;
		} else if (line.contains("can't open device")) {
			abort = true;
			fAbortReason = Reason.PORT_BLOCKED;
		} else if (line.contains("can't open config file")) {
			abort = true;
			fAbortReason = Reason.CONFIG_NOT_FOUND;
		} else if (line.contains("Can't find programmer id")) {
			abort = true;
			fAbortReason = Reason.UNKNOWN_PROGRAMMER;
		} /*
		 * else if (line.contains("no programmer has been specified")) { abort = true; fAbortReason
		 * = Reason.NO_PROGRAMMER; }
		 */else if (line.matches("TC32 Part.+not found")) {
			abort = true;
			fAbortReason = Reason.UNKNOWN_MCU;
		} else if (line.endsWith("execution aborted")) {
			abort = true;
			fAbortReason = Reason.USER_CANCEL;
		} else if (line.contains("usbdev_open")) {
			abort = true;
			fAbortReason = Reason.NO_USB;
		} else if (line.contains("failed to sync with")) {
			abort = true;
			fAbortReason = Reason.SYNC_FAIL;
		} else if (line.contains("initialization failed")) {
			abort = true;
			fAbortReason = Reason.INIT_FAIL;
		} else if (line.contains("NO_TARGET_POWER")) {
			abort = true;
			fAbortReason = Reason.NO_TARGET_POWER;
		}
		if (abort) {
			fProgressMonitor.setCanceled(true);
			fAbortLine = line;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.telink.tc32eclipse.core.toolinfo.ICommandOutputListener#getAbortLine()
	 */
	public String getAbortLine() {
		return fAbortLine;
	}

	/*
	 * (non-Javadoc)
	 * @see com.telink.tc32eclipse.core.toolinfo.ICommandOutputListener#getAbortReason()
	 */
	public Reason getAbortReason() {
		return fAbortReason;
	}

}
