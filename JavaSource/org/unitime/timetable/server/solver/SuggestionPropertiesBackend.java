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
package org.unitime.timetable.server.solver;

import org.unitime.timetable.gwt.command.server.GwtRpcImplementation;
import org.unitime.timetable.gwt.command.server.GwtRpcImplements;
import org.unitime.timetable.gwt.shared.SuggestionsInterface.PreferenceInterface;
import org.unitime.timetable.gwt.shared.SuggestionsInterface.SuggestionProperties;
import org.unitime.timetable.gwt.shared.SuggestionsInterface.SuggestionPropertiesRequest;
import org.unitime.timetable.model.PreferenceLevel;
import org.unitime.timetable.security.SessionContext;
import org.unitime.timetable.util.Constants;

/**
 * @author Tomas Muller
 */
@GwtRpcImplements(SuggestionPropertiesRequest.class)
public class SuggestionPropertiesBackend implements GwtRpcImplementation<SuggestionPropertiesRequest, SuggestionProperties> {

	@Override
	public SuggestionProperties execute(SuggestionPropertiesRequest request, SessionContext context) {
		SuggestionProperties response = new SuggestionProperties();
		
		for (PreferenceLevel pref: PreferenceLevel.getPreferenceLevelList(false)) {
			response.addPreference(new PreferenceInterface(
					pref.getUniqueId(),
					PreferenceLevel.prolog2color(pref.getPrefProlog()),
					pref.getPrefProlog(),
					pref.getPrefName(),
					pref.getAbbreviation(),
					Constants.preference2preferenceLevel(pref.getPrefProlog())));
		}

		return response;
	}

}
