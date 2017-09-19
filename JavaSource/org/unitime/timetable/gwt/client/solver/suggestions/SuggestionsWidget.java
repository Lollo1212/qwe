/*
 * Licensed to The Apereo Foundation under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * The Apereo Foundation licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
*/
package org.unitime.timetable.gwt.client.solver.suggestions;

import java.util.List;

import org.unitime.timetable.gwt.client.page.UniTimeNotifications;
import org.unitime.timetable.gwt.client.widgets.SimpleForm;
import org.unitime.timetable.gwt.client.widgets.UniTimeHeaderPanel;
import org.unitime.timetable.gwt.client.widgets.UniTimeTable.MouseClickListener;
import org.unitime.timetable.gwt.client.widgets.UniTimeTable.TableEvent;
import org.unitime.timetable.gwt.command.client.GwtRpcService;
import org.unitime.timetable.gwt.command.client.GwtRpcServiceAsync;
import org.unitime.timetable.gwt.resources.GwtMessages;
import org.unitime.timetable.gwt.shared.SuggestionsInterface.ComputeSuggestionsRequest;
import org.unitime.timetable.gwt.shared.SuggestionsInterface.SelectedAssignment;
import org.unitime.timetable.gwt.shared.SuggestionsInterface.SelectedAssignmentsRequest;
import org.unitime.timetable.gwt.shared.SuggestionsInterface.Suggestion;
import org.unitime.timetable.gwt.shared.SuggestionsInterface.Suggestions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Tomas Muller
 */
public class SuggestionsWidget extends SimpleForm {
	protected static final GwtMessages MESSAGES = GWT.create(GwtMessages.class);
	
	private SuggestionsPageContext iContext;
	private UniTimeHeaderPanel iHeader, iFooter;
	private ComputeSuggestionsRequest iRequest;
	private SuggestionsTable iTable;
	private int iMessageRow = 0;
	private HTML iMessage = null;
	protected static GwtRpcServiceAsync RPC = GWT.create(GwtRpcService.class);
	
	public SuggestionsWidget(SuggestionsPageContext context) {
		iContext = context;
		iHeader = new UniTimeHeaderPanel(MESSAGES.headerSuggestions());
		iFooter = new UniTimeHeaderPanel();
		iFooter.addButton("deeper", MESSAGES.buttonSearchDeeper(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				iRequest.setDepth(iRequest.getDepth() + 1);
				computeSuggestions();
			}
		});
		iFooter.addButton("longer", MESSAGES.buttonSearchLonger(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				iRequest.setTimeLimit(2 * iRequest.getTimeLimit());
				computeSuggestions();
			}
		});
		removeStyleName("unitime-NotPrintableBottomLine");
	}
	
	public void onSelection(Suggestion suggestion) {}
	
	public void clearMessage() {
		iHeader.clearMessage();
		if (iMessage != null) {
			iMessage.setHTML("");
			getRowFormatter().setVisible(iMessageRow, false);
		}
	}
	
	public void setInfoMessage(String message) {
		iHeader.clearMessage();
		if (iMessage != null) {
			iMessage.setHTML(message);
			getRowFormatter().setVisible(iMessageRow, true);
		}
	}
	
	public void showLoading() {
		iHeader.showLoading();
		if (iMessage != null) {
			iMessage.setHTML("");
			getRowFormatter().setVisible(iMessageRow, false);
		}
	}
	
	public void setRequest(SelectedAssignmentsRequest request) {
		setSelection(request.getAssignments(), request.getClassId());
	}
	
	public void setSelection(List<SelectedAssignment> assignments, Long classId) {
		iHeader.clearMessage();
		clear();
		if (iTable == null) {
			iTable = new SuggestionsTable(iContext.getProperties(), true);
			iTable.setVisible(false);
			iTable.addMouseClickListener(new MouseClickListener<Suggestion>() {
				@Override
				public void onMouseClick(TableEvent<Suggestion> event) {
					if (event.getData() != null) onSelection(event.getData());
				}
			});
		}
		if (classId != null) {
			addHeaderRow(iHeader);
			addRow(iTable);
			iMessage = new HTML(); iMessage.addStyleName("info-message");
			iMessageRow = addRow(iMessage);
			addBottomRow(iFooter);
			iRequest = new ComputeSuggestionsRequest(classId, assignments);
			computeSuggestions();
		}
	}
	
	protected void computeSuggestions() {
		showLoading();
		iFooter.setEnabled("longer", false);
		iFooter.setEnabled("deeper", false);
		iTable.setVisible(false);
		RPC.execute(iRequest, new AsyncCallback<Suggestions>() {

			@Override
			public void onFailure(Throwable caught) {
				iHeader.setErrorMessage(MESSAGES.failedToComputeSuggestions(caught.getMessage()));
				UniTimeNotifications.error(MESSAGES.failedToComputeSuggestions(caught.getMessage()), caught);
			}

			@Override
			public void onSuccess(Suggestions result) {
				iTable.setValue(result.getSuggestions());
				iTable.setVisible(true);
				iFooter.setEnabled("longer", result.isTimeoutReached());
				iFooter.setEnabled("deeper", true);
				if (result.size() == 0) {
					if (result.isTimeoutReached())
						setInfoMessage(MESSAGES.suggestionsNoteTimeoutNoResults(result.getTimeLimit() / 1000, result.getNrCombinationsConsidered(), result.getDepth()));
					else
						setInfoMessage(MESSAGES.suggestionsNoteNoTimeoutNoResults(result.getNrCombinationsConsidered(), result.getDepth()));
				} else if (result.size() < result.getNrSolutions()) {
					if (result.isTimeoutReached())
						setInfoMessage(MESSAGES.suggestionsNoteTimeoutNResults(result.getTimeLimit() / 1000, result.getNrCombinationsConsidered(), result.getDepth(), result.getSuggestions().size(), result.getNrSolutions()));
					else
						setInfoMessage(MESSAGES.suggestionsNoteNoTimeoutNResults(result.getNrCombinationsConsidered(), result.getDepth(), result.getSuggestions().size(), result.getNrSolutions()));
				} else {
					if (result.isTimeoutReached())
						setInfoMessage(MESSAGES.suggestionsNoteTimeoutAllResults(result.getTimeLimit() / 1000, result.getNrCombinationsConsidered(), result.getDepth(), result.getSuggestions().size()));
					else
						setInfoMessage(MESSAGES.suggestionsNoteNoTimeoutAllResults(result.getNrCombinationsConsidered(), result.getDepth(), result.getSuggestions().size()));
				}
			}
		});
	}

}
