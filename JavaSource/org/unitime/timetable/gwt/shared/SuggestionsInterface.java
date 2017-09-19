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
package org.unitime.timetable.gwt.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.unitime.timetable.gwt.command.client.GwtRpcRequest;
import org.unitime.timetable.gwt.command.client.GwtRpcResponse;
import org.unitime.timetable.gwt.command.client.GwtRpcResponseNull;
import org.unitime.timetable.gwt.resources.GwtConstants;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Tomas Muller
 */
public class SuggestionsInterface implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	
	public static class ClassInfo implements IsSerializable, Serializable, Comparable<ClassInfo> {
		private static final long serialVersionUID = 1L;
		private String iName;
		private Long iClassId;
		private String iPref;
		private int iRoomCap;
		private int iNrRooms;
		private int iOrd = -1;
		private String iNote;
		
		public ClassInfo() {}
		
		public ClassInfo(String name, Long classId, int nrRooms, String pref, int roomCapacity, int ord, String note) {
			iName = name;
			iClassId = classId;
			iPref = pref;
			iNrRooms = nrRooms;
			iRoomCap = roomCapacity;
			iOrd = ord;
			iNote = note;
		}
		
		public void setName(String name) { iName = name; }
		public String getName() { return iName; }
		
		public void setClassId(Long classId) { iClassId = classId; }
		public Long getClassId() { return iClassId; }
		
		public void setRoomCapacity(int roomCap) { iRoomCap = roomCap; }
		public int getRoomCapacity() { return iRoomCap; }
		
		public void setPref(String pref) { iPref = pref; }
		public String getPref() { return iPref; }
		
		public void setNote(String note) { iNote = note; }
		public String getNote() { return iNote; }

		public void setNrRooms(int nrRooms) { iNrRooms = nrRooms; }
		public int nrRooms() { return iNrRooms; }
		
		@Override
		public boolean equals(Object o) {
			if (o==null || !(o instanceof ClassInfo)) return false;
			return getClassId().equals(((ClassInfo)o).getClassId());
		}
		
		@Override
		public int compareTo(ClassInfo ci) {
			if (iOrd >= 0 && ci.iOrd >= 0) {
				int cmp = Double.compare(iOrd, ci.iOrd);
				if (cmp!=0) return cmp;
			}
			int cmp = TableInterface.NaturalOrderComparator.compare(getName(), ci.getName());
			if (cmp != 0) return cmp;
			return getClassId().compareTo(ci.getClassId());
		}
		
		@Override
		public String toString() {
			return getName() == null ? getClassId().toString() : getName();
		}
	}
	
	public static class RoomInfo implements IsSerializable, Serializable, Comparable<RoomInfo> {
		private static final long serialVersionUID = 1L;
		private String iName;
		private Long iRoomId;
		private int iPref;
		private int iSize;
		private boolean iStrike;
		
		public RoomInfo() {}
		public RoomInfo(String name, Long roomId, int size, int pref) {
			iName = name;
			iRoomId = roomId;
			iPref = pref;
			iSize = size;
			iStrike = (iPref > 500);
		}
		public RoomInfo(Long roomId) {
			iRoomId = roomId;
		}
		
		public void setName(String name) { iName = name; }
		public String getName() { return iName; }
		
		public void setId(Long id) { iRoomId = id; }
		public Long getId() { return iRoomId; }
		
		public void setPref(int pref) { iPref = pref; }
		public int getPref() { return iPref; }
		
		public void setStriked(boolean striked) { iStrike = striked; }
		public boolean isStriked() { return iStrike; }
		
		public void setSize(int size) { iSize = size; }
		public int getSize() { return iSize; }
		
		@Override
		public boolean equals(Object o) {
			if (o==null || !(o instanceof RoomInfo)) return false;
			return getId().equals(((RoomInfo)o).getId());
		}

		@Override
		public int compareTo(RoomInfo r) {
			if (isStriked() && !r.isStriked()) return 1;
			if (!isStriked() && r.isStriked()) return -1;
			return TableInterface.NaturalOrderComparator.compare(getName(), r.getName());
		}
		
		@Override
		public String toString() {
			return (getName() == null ? getId().toString() : getName());
		}
	}
	
	public static enum DayCode {
		MON(64),
		TUE(32),
		WED(16),
		THU(8),
		FRI(4),
		SAT(2),
		SUN(1),
		;
		DayCode(int code) { iCode = code; }
		private int iCode;
		public int getCode() { return iCode; }
	}
	
	public static class DateInfo implements IsSerializable, Serializable, Comparable<DateInfo> {
		private static final long serialVersionUID = 1L;
		private String iDatePatternName;
		private int iDatePatternPref;
		private Long iDatePatternId;
		
		public DateInfo() {}
		public DateInfo(Long id, String name, int pref) {
			iDatePatternId = id;
			iDatePatternName = name;
			iDatePatternPref = pref;
		}
		public DateInfo(SelectedAssignment assignment) {
			iDatePatternId = assignment.getDatePatternId();
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof DateInfo)) return false;
			DateInfo d = (DateInfo)o;
			if (getDatePatternId() != null && d.getDatePatternId() != null)
				return getDatePatternId().equals(d.getDatePatternId());
			return getDatePatternName().equals(d.getDatePatternName());
		}
		
		@Override
		public int hashCode() {
			return (getDatePatternId() == null ? getDatePatternName().hashCode() : getDatePatternId().hashCode());
		}
		
		public void setDatePatternName(String dpName) { iDatePatternName = dpName; }
		public String getDatePatternName() { return iDatePatternName; }

		public void setDatePatternId(Long dpId) { iDatePatternId = dpId; }
		public Long getDatePatternId() { return iDatePatternId; }
		
		public void setDatePatternPreference(int dpPref) { iDatePatternPref = dpPref; }
		public int getDatePatternPreference() { return iDatePatternPref; }
		
		@Override
		public int compareTo(DateInfo d) {
			int cmp = TableInterface.NaturalOrderComparator.compare(getDatePatternName(), d.getDatePatternName());
			if (cmp != 0) return cmp;
			return getDatePatternId().compareTo(d.getDatePatternId());
		}
	}
	
	public static class TimeInfo implements IsSerializable, Serializable, Comparable<TimeInfo> {
		private static final long serialVersionUID = 1L;
		private int iDays;
		private int iStartSlot;
		private int iMin;
		private int iPref;
		private boolean iStrike = false;
		private Long iPatternId = null;
		private DateInfo iDatePattern = null;
		
		public TimeInfo() {}
		public TimeInfo(int days, int startSlot, int pref, int min, String datePatternName, Long patternId, Long datePatternId, int datePatternPref) {
			iDays = days;
			iStartSlot = startSlot;
			iPref = pref;
			iStrike = (iPref > 500);
			iMin = min;
			iDatePattern = new DateInfo(datePatternId, datePatternName, datePatternPref);
			iPatternId = patternId;
		}
		public TimeInfo(SelectedAssignment assignment) {
			iDays = assignment.getDays();
			iStartSlot = assignment.getStartSlot();
			iPatternId = assignment.getPatternId();
			iDatePattern = new DateInfo(assignment);
		}
		
		public void setDays(int days) { iDays = days; }
		public int getDays() { return iDays; }
		
		public void setStartSlot(int startSlot) { iStartSlot = startSlot; }
		public int getStartSlot() { return iStartSlot; }
		
		public void setPref(int pref) { iPref = pref; }
		public int getPref() { return iPref; }
		
		public void setMin(int min) { iMin = min; }
		public int getMin() { return iMin; }
		
		public void setDatePatternName(String dpName) {
			if (iDatePattern == null) iDatePattern = new DateInfo();
			iDatePattern.setDatePatternName(dpName);
		}
		public String getDatePatternName() { return (iDatePattern == null ? null : iDatePattern.getDatePatternName()); }

		public void setStriked(boolean striked) { iStrike = striked; }
		public boolean isStriked() { return iStrike; }
		
		public void setPatternId(Long patternId) { iPatternId = patternId; }
		public Long getPatternId() { return iPatternId; }
		
		public void setDatePatternId(Long dpId) {
			if (iDatePattern == null) iDatePattern = new DateInfo();
			iDatePattern.setDatePatternId(dpId);
		}
		public Long getDatePatternId() { return (iDatePattern == null ? null : iDatePattern.getDatePatternId()); }
		
		public void setDatePatternPreference(int dpPref) {
			if (iDatePattern == null) iDatePattern = new DateInfo();
			iDatePattern.setDatePatternPreference(dpPref);
		}
		public int getDatePatternPreference() { return (iDatePattern == null ? null : iDatePattern.getDatePatternPreference()); }
		
		public DateInfo getDatePattern() { return iDatePattern; }
		public boolean hasDatePattern() { return iDatePattern != null; }
		
		public String getDaysName(GwtConstants CONSTANTS) {
			return getDaysName(CONSTANTS.shortDays());
		}
		
		public String getDaysName(String[] shortDays) {
			if (shortDays == null) shortDays = new String[] {"M", "T", "W", "Th", "F", "S", "Su"};
			String ret = "";
			for (DayCode dc: DayCode.values())
				if ((dc.getCode() & iDays)!=0) ret += shortDays[dc.ordinal()];
			return ret;
		}
		
		public String getDaysName() {
			return getDaysName(new String[] {"M", "T", "W", "Th", "F", "S", "Su"});
		}
		
		public static String slot2time(int timeSinceMidnight, boolean useAmPm) {
			int hour = timeSinceMidnight / 60;
		    int min = timeSinceMidnight % 60;
		    if (useAmPm)
		    	return (hour==0?12:hour>12?hour-12:hour)+":"+(min<10?"0":"")+min+(hour<24 && hour>=12?"p":"a");
		    else
		    	return hour + ":" + (min < 10 ? "0" : "") + min;
		}
			
		public String getStartTime(GwtConstants CONSTANTS) {
			return slot2time(5 * iStartSlot, CONSTANTS.useAmPm());
		}
		
		public String getEndTime(GwtConstants CONSTANTS) {
			return slot2time(5 * iStartSlot + iMin, CONSTANTS.useAmPm());
		}
		
		@Override
		public boolean equals(Object o) {
			if (o==null || !(o instanceof TimeInfo)) return false;
			TimeInfo t = (TimeInfo)o;
			return t.getDays()==getDays() && t.getStartSlot()==getStartSlot() && t.getPatternId().equals(getPatternId()) && t.getDatePatternId().equals(getDatePatternId());
		}
		
		public String getName(boolean endTime, GwtConstants CONSTANTS) {
			return getDaysName(CONSTANTS) + " " + getStartTime(CONSTANTS) + (endTime ? " - " + getEndTime(CONSTANTS) : "");
		}
		
		@Override
		public int compareTo(TimeInfo t) {
			if (isStriked() && !t.isStriked()) return 1;
			if (!isStriked() && t.isStriked()) return -1;
			int cmp = TableInterface.NaturalOrderComparator.compare(getDatePatternName(), t.getDatePatternName());
			if (cmp!=0) return cmp;
			cmp = -Double.compare(getDays(),t.getDays());
			if (cmp!=0) return cmp;
			cmp = Double.compare(getStartSlot(),t.getStartSlot());
			if (cmp!=0) return cmp;
			cmp = Double.compare(getMin(),t.getMin());
			return cmp;
		}
		
		@Override
		public String toString() {
			return getDaysName() + " " + slot2time(5 * iStartSlot, true) + " - " + slot2time(5 * iStartSlot + iMin, true) + (hasDatePattern() ? " " + getDatePatternName() : "");
		}
	}
	
	public static class InstructorInfo implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private String iName;
		private Long iInstructorId;
		
		public InstructorInfo() {}
		public InstructorInfo(String name, Long instructorId) {
			iName = name;
			iInstructorId = instructorId;
		}
		
		public void setName(String name) { iName = name; }
		public String getName() { return iName; }
		
		public void setId(Long id) { iInstructorId = id; }
		public Long getId() { return iInstructorId; }

		@Override
		public boolean equals(Object o) {
			if (o==null || !(o instanceof InstructorInfo)) return false;
			return getId().equals(((InstructorInfo)o).getId());
		}
	}
	
	public static class AssignmentPreferenceInfo implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private double iNormalizedTimePreference = 0.0;
		private double iBestNormalizedTimePreference = 0.0;
		private int iTimePreference = 0;
		private Map<Long, Integer> iRoomPreference = new HashMap<Long, Integer>();
		private int iBestRoomPreference = 0;
		private int iNrStudentConflicts = 0;
		private int iNrHardStudentConflicts = 0;
		private int iNrDistanceStudentConflicts = 0;
		private int iNrCommitedStudentConflicts = 0;
		private int iNrTimeLocations = 0;
		private int iNrRoomLocations = 0;
		private int iNrSameRoomPlacementsNoConf = 0;
	    private int iNrSameTimePlacementsNoConf = 0;
		private int iNrPlacementsNoConf = 0;
		private int iBtbInstructorPreference = 0;
		private boolean iIsInitial = false;
		private String iInitialAssignment = null;
		private boolean iHasInitialSameTime = false;
		private boolean iHasInitialSameRoom = false;
		private double iPerturbationPenalty = 0.0;
		private int iTooBigRoomPreference = 0;
		private long iMinRoomSize = 0;
		private int iUselessHalfHours = 0;
		private double iDeptBalancPenalty = 0;
		private double iSpreadPenalty = 0;
		private int iMaxDeptBalancPenalty = 0;
		private double iMaxSpreadPenalty = 0;
		private int iGroupConstraintPref = 0;
		private int iDatePatternPref = 0;
		private Integer iStudentGroupPercent = null;
		private String iStudentGroupComment = null;
		private Map<String, Double> iCriteria = new HashMap<String, Double>();
		
		public AssignmentPreferenceInfo() {}
		
		public double getNormalizedTimePreference() { return iNormalizedTimePreference; }
		public void setNormalizedTimePreference(double normalizedTimePreference) { iNormalizedTimePreference = normalizedTimePreference; }
		public double getBestNormalizedTimePreference() { return iBestNormalizedTimePreference; }
		public void setBestNormalizedTimePreference(double bestNormalizedTimePreference) { iBestNormalizedTimePreference = bestNormalizedTimePreference; }
		public int getTimePreference() { return iTimePreference; }
		public void setTimePreference(int timePreference) { iTimePreference = timePreference; }
		public int getRoomPreference(Long roomId) { 
			Integer pref = iRoomPreference.get(roomId);
			return (pref==null?0:pref.intValue());
		}
		public int sumRoomPreference() {
			int ret = 0;
			for (Integer pref: iRoomPreference.values())
				ret += pref;
		    return ret;
		}
		public void setRoomPreference(Map<Long, Integer> pref) { iRoomPreference = new HashMap<Long, Integer>(pref); }
		public Map<Long, Integer> getRoomPreference() { return iRoomPreference; }
		public void setRoomPreference(Long roomId, int roomPreference) { iRoomPreference.put(roomId,new Integer(roomPreference)); }
		public int getBestRoomPreference() { return iBestRoomPreference; }
		public void setBestRoomPreference(int bestRoomPreference) { iBestRoomPreference = bestRoomPreference; }
		public int getNrStudentConflicts() { return iNrStudentConflicts; }
		public void setNrStudentConflicts(int nrStudentConflicts) { iNrStudentConflicts = nrStudentConflicts; }
		public int getNrHardStudentConflicts() { return iNrHardStudentConflicts; }
		public void setNrHardStudentConflicts(int nrHardStudentConflicts) { iNrHardStudentConflicts = nrHardStudentConflicts; }
		public int getNrDistanceStudentConflicts() { return iNrDistanceStudentConflicts; }
		public void setNrDistanceStudentConflicts(int nrDistanceStudentConflicts) { iNrDistanceStudentConflicts = nrDistanceStudentConflicts; }
		public int getNrCommitedStudentConflicts() { return iNrCommitedStudentConflicts; }
		public void setNrCommitedStudentConflicts(int nrCommitedStudentConflicts) { iNrCommitedStudentConflicts = nrCommitedStudentConflicts; }
		public int getNrTimeLocations() { return iNrTimeLocations; }
		public void setNrTimeLocations(int nrTimeLocations) { iNrTimeLocations = nrTimeLocations; }
		public int getNrRoomLocations() { return iNrRoomLocations; }
		public void setNrRoomLocations(int nrRoomLocations) { iNrRoomLocations = nrRoomLocations; }
		public int getNrSameRoomPlacementsNoConf() { return iNrSameRoomPlacementsNoConf; }
		public void setNrSameRoomPlacementsNoConf(int nrSameRoomPlacementsNoConf) { iNrSameRoomPlacementsNoConf = nrSameRoomPlacementsNoConf; }
		public int getNrSameTimePlacementsNoConf() { return iNrSameTimePlacementsNoConf; }
		public void setNrSameTimePlacementsNoConf(int nrSameTimePlacementsNoConf) { iNrSameTimePlacementsNoConf = nrSameTimePlacementsNoConf; }
		public int getNrPlacementsNoConf() { return iNrPlacementsNoConf; }
		public void setNrPlacementsNoConf(int nrPlacementsNoConf) { iNrPlacementsNoConf = nrPlacementsNoConf; }
		public int getBtbInstructorPreference() { return iBtbInstructorPreference; }
		public void setBtbInstructorPreference(int btbInstructorPreference) { iBtbInstructorPreference = btbInstructorPreference; }
		public boolean getIsInitial() { return iIsInitial; }
		public void setIsInitial(boolean isInitial) { iIsInitial = isInitial; }
		public String getInitialAssignment() { return iInitialAssignment; }
		public void setInitialAssignment(String initialAssignment) { iInitialAssignment = initialAssignment; }
		public boolean getHasInitialSameTime() { return iHasInitialSameTime; }
		public void setHasInitialSameTime(boolean hasInitialSameTime) { iHasInitialSameTime = hasInitialSameTime; }
		public boolean getHasInitialSameRoom() { return iHasInitialSameRoom; }
		public void setHasInitialSameRoom(boolean hasInitialSameRoom) { iHasInitialSameRoom = hasInitialSameRoom; }
		public double getPerturbationPenalty() { return iPerturbationPenalty; }
		public void setPerturbationPenalty(double perturbationPenalty) { iPerturbationPenalty = perturbationPenalty; }
		public int getTooBigRoomPreference() { return iTooBigRoomPreference; } 
		public void setTooBigRoomPreference(int tooBigRoomPreference) { iTooBigRoomPreference = tooBigRoomPreference; }
		public long getMinRoomSize() { return iMinRoomSize; } 
		public void setMinRoomSize(long minRoomSize) { iMinRoomSize = minRoomSize; }
		public int getUselessHalfHours() { return iUselessHalfHours; }
		public void setUselessHalfHours(int uselessHalfHours) { iUselessHalfHours = uselessHalfHours; }
		public double getDeptBalancPenalty() { return iDeptBalancPenalty; }
		public void setDeptBalancPenalty(double deptBalancPenalty) { iDeptBalancPenalty = deptBalancPenalty; }
		public int getMaxDeptBalancPenalty() { return iMaxDeptBalancPenalty; }
		public void setMaxDeptBalancPenalty(int deptBalancPenalty) { iMaxDeptBalancPenalty = deptBalancPenalty; }
		public int getGroupConstraintPref() { return iGroupConstraintPref; }
		public void setGroupConstraintPref(int groupConstraintPref) { iGroupConstraintPref = groupConstraintPref; }
		public double getSpreadPenalty() { return iSpreadPenalty; }
		public void setSpreadPenalty(double spreadPenalty) { iSpreadPenalty = spreadPenalty; }
		public double getMaxSpreadPenalty() { return iMaxSpreadPenalty; }
		public void setMaxSpreadPenalty(double spreadPenalty) { iMaxSpreadPenalty = spreadPenalty; }
		public int getDatePatternPref() { return iDatePatternPref; }
		public void setDatePatternPref(int datePatternPref) { iDatePatternPref = datePatternPref; }
		
		public Integer getStudentGroupPercent() { return iStudentGroupPercent; }
		public void setStudentGroupPercent(Integer percent) { iStudentGroupPercent = percent; }
		public String getStudentGroupComment() { return iStudentGroupComment; }
		public void setStudentGroupComment(String comment) { iStudentGroupComment = comment; }
		
		public void setCriterion(String name, Double value) { iCriteria.put(name, value); }
		public Map<String, Double> getCriteria() { return iCriteria; }
		public double getCriterion(String name) {
			Double value = iCriteria.get(name);
			return (value == null ? 0.0 : value.doubleValue());
		}
	}
	
	public static class CurriculumInfo implements IsSerializable, Serializable, Comparable<CurriculumInfo> {
		private static final long serialVersionUID = 1L;
		private String iName;
		private int iNrStudents;
		
		public CurriculumInfo() {}
		public CurriculumInfo(String name, int nrStudents) {
			iName = name;
			iNrStudents = nrStudents;
		}
		
		public void setName(String name) { iName = name; }
		public String getName() { return iName; }
		public void setNrStudents(int nrStudents) { iNrStudents = nrStudents; }
		public int getNrStudents() { return iNrStudents; }
		
		public int compareTo(CurriculumInfo i) {
			if (getNrStudents() != i.getNrStudents())
				return (i.getNrStudents() > getNrStudents() ? -1 : 1);
			return getName().compareTo(i.getName());
		}
	}
	
	public static class JenrlInfo implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		public int iJenrl = 0;
		public boolean iIsSatisfied = false;
		public boolean iIsHard = false;
		public boolean iIsDistance = false;
		public boolean iIsFixed = false;
		public boolean iIsCommited = false;
		public boolean iIsImportant = false;
		public boolean iIsInstructor = false;
		public double iDistance = 0.0;
		private TreeSet<CurriculumInfo> iCurriculum2nrStudents = null;
	
		public int getJenrl() { return iJenrl; }
		public void setJenrl(int jenrl) { iJenrl = jenrl; }
		public boolean isSatisfied() { return iIsSatisfied; }
		public void setIsSatisfied(boolean isSatisfied) { iIsSatisfied = isSatisfied; }
		public boolean isHard() { return iIsHard; }
		public void setIsHard(boolean isHard) { iIsHard = isHard; }
		public boolean isDistance() { return iIsDistance; }
		public void setIsDistance(boolean isDistance) { iIsDistance = isDistance; }
		public boolean isFixed() { return iIsFixed; }
		public void setIsFixed(boolean isFixed) { iIsFixed = isFixed; }
		public boolean isCommited() { return iIsCommited; }
		public void setIsCommited(boolean isCommited) { iIsCommited = isCommited; }
		public boolean isImportant() { return iIsImportant; }
		public void setIsImportant(boolean isImportant) { iIsImportant = isImportant; }
		public boolean isInstructor() { return iIsInstructor; }
		public void setIsInstructor(boolean isInstructor) { iIsInstructor = isInstructor; }
		public double getDistance() { return iDistance; }
		public void setDistance(double distance) { iDistance = distance; }
		
		public boolean hasCurricula() { return iCurriculum2nrStudents != null; }
		public void addCurriculum(CurriculumInfo info) {
			if (iCurriculum2nrStudents == null) iCurriculum2nrStudents = new TreeSet<CurriculumInfo>();
			iCurriculum2nrStudents.add(info);
		}
		public Set<CurriculumInfo> getCurricula() { return iCurriculum2nrStudents; }
	}
	
	public static class StudentConflictInfo implements IsSerializable, Serializable, Comparable<StudentConflictInfo> {
		private static final long serialVersionUID = 1L;
		private JenrlInfo iInfo;
		private ClassAssignmentDetails iOther = null;
		private ClassAssignmentDetails iAnother = null;
		
		public StudentConflictInfo() {}
		public StudentConflictInfo(JenrlInfo jenrl, ClassAssignmentDetails other) {
			iInfo = jenrl;
			iOther = other;
		}
		
		public void setOther(ClassAssignmentDetails other) { iOther = other; }
		public ClassAssignmentDetails getOther() { return iOther; }
		public void setAnother(ClassAssignmentDetails another) { iAnother = another; }
		public ClassAssignmentDetails getAnother() { return iAnother; }
		public void setInfo(JenrlInfo info) { iInfo = info; }
		public JenrlInfo getInfo() { return iInfo; }

		@Override
		public int compareTo(StudentConflictInfo o) {
			if (getInfo().getJenrl() != o.getInfo().getJenrl())
				return getInfo().getJenrl() > o.getInfo().getJenrl() ? -1 : 1;
			int cmp = getOther().getClazz().getName().compareTo(o.getOther().getClazz().getName());
			if (cmp != 0) return cmp;
			return getOther().getClazz().compareTo(o.getOther().getClazz());
		}
	}
	
	public static class GroupConstraintInfo implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		public String iPreference = "0";
		public boolean iIsSatisfied = false;
		public String iName = null;
	    public String iType = null;
		
		public GroupConstraintInfo() { }
		
		public String getPreference() { return iPreference; }
		public void setPreference(String preference) { iPreference = preference; }
		public boolean isSatisfied() { return iIsSatisfied; }
		public void setIsSatisfied(boolean isSatisfied) { iIsSatisfied = isSatisfied; }
		public String getName() { return iName; }
		public void setName(String name) { iName = name; }
	    public String getType() { return iType; }
	    public void setType(String type) { iType = type; }
	}
	
	public static class DistributionInfo implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private GroupConstraintInfo iInfo;
		private List<ClassAssignmentDetails> iOther = new ArrayList<ClassAssignmentDetails>();
		
		public DistributionInfo() {}
		public DistributionInfo(GroupConstraintInfo info) {
			iInfo = info;
		}
		
		public void setInfo(GroupConstraintInfo info) { iInfo = info; }
		public GroupConstraintInfo getInfo() { return iInfo; }
		
		public void addClass(ClassAssignmentDetails other) { iOther.add(other); }
		public List<ClassAssignmentDetails> getOtherClasses() { return iOther; }
	}
	
	public static class BtbInstructorInfo implements IsSerializable, Serializable {
		private static final long serialVersionUID = 1L;
		private ClassAssignmentDetails iOther = null;
		private ClassAssignmentDetails iAnother = null;
		private int iPref;
		
		public BtbInstructorInfo() {}
		public BtbInstructorInfo(ClassAssignmentDetails other, int pref) {
			iOther = other;
			iPref = pref;
		}
		
		public void setOther(ClassAssignmentDetails other) { iOther = other; }
		public ClassAssignmentDetails getOther() { return iOther; }
		public void setAnother(ClassAssignmentDetails another) { iAnother = another; }
		public ClassAssignmentDetails getAnother() { return iAnother; }
		public void setPreference(int preference) { iPref = preference; }
		public int getPreference() { return iPref; }
	}
	
	public static class ClassAssignmentDetailsRequest implements GwtRpcRequest<ClassAssignmentDetails>{
		private Long iClassId;
		private List<SelectedAssignment> iAssignments;
		
		public ClassAssignmentDetailsRequest() {}
		public ClassAssignmentDetailsRequest(Long classId) {
			setClassId(classId);
		}
		
		public void setClassId(Long classId) { iClassId = classId; }
		public Long getClassId() { return iClassId; }
		
		public boolean hasAssignments() { return iAssignments != null && !iAssignments.isEmpty(); }
		public void addAssignment(SelectedAssignment a) {
			if (iAssignments == null) iAssignments = new ArrayList<SelectedAssignment>();
			for (Iterator<SelectedAssignment> i = iAssignments.iterator(); i.hasNext(); ) {
				SelectedAssignment other = i.next();
				if (other.equals(a)) i.remove();
			}
			iAssignments.add(a);
		}
		public SelectedAssignment removeAssignment(Long classId) {
			if (iAssignments == null) return null;
			for (Iterator<SelectedAssignment> i = iAssignments.iterator(); i.hasNext(); ) {
				SelectedAssignment other = i.next();
				if (other.getClassId().equals(classId)) {
					i.remove();
					return other;
				}
			}
			return null;
		}
	}
	
	public static class ClassAssignmentDetails implements GwtRpcResponse, Serializable {
		private static final long serialVersionUID = 1L;
		private ClassInfo iClass = null;
		private TimeInfo iTime = null;
		private List<RoomInfo> iRoom = null;
		private List<InstructorInfo> iInstructor = null;
		private TimeInfo iInitialTime = null;
		private List<RoomInfo> iInitialRoom = null;
		private TimeInfo iAssignedTime = null;
		private List<RoomInfo> iAssignedRoom = null;
		private AssignmentPreferenceInfo iAssignmentInfo = null;
		private AssignmentPreferenceInfo iAssignedAssignmentInfo = null;
		private List<RoomInfo> iRooms = null;
		private List<TimeInfo> iTimes = null;
		private List<StudentConflictInfo> iStudentConflicts = null;
		private List<DistributionInfo> iDistributionConflicts = null;
		private List<BtbInstructorInfo> iBtbInstructorConflicts = null;
		private String iConflict = null;
		
		public ClassAssignmentDetails() {}
		
		public void setClazz(ClassInfo clazz) { iClass = clazz; }
		public ClassInfo getClazz() { return iClass; }
		public void setTime(TimeInfo time) { iTime = time; }
		public TimeInfo getTime() { return iTime; }
		public int getNrRooms() { return iRoom == null ? 0 : iRoom.size(); }
		public void setRoom(RoomInfo room) {
			if (iRoom == null) iRoom = new ArrayList<RoomInfo>();
			iRoom.add(room);
		}
		public List<RoomInfo> getRoom() { return iRoom; }
		public int getNrInstructors() { return iInstructor == null ? 0 : iInstructor.size(); }
		public void setInstructor(InstructorInfo instructor) {
			if (iInstructor == null) iInstructor = new ArrayList<InstructorInfo>();
			iInstructor.add(instructor);
		}
		public List<InstructorInfo> getInstructor() { return iInstructor; }
		public void setPreferences(AssignmentPreferenceInfo info) { iAssignmentInfo = info; }
		public AssignmentPreferenceInfo getPreferences() { return iAssignmentInfo; }
		public void setAssignedPreferences(AssignmentPreferenceInfo info) { iAssignedAssignmentInfo = info; }
		public AssignmentPreferenceInfo getAssignedPreferences() { return iAssignedAssignmentInfo; }
		public void setInitialTime(TimeInfo time) { iInitialTime = time; }
		public TimeInfo getInitialTime() { return iInitialTime; }
		public int getNrInitialRooms() { return iInitialRoom == null ? 0 : iInitialRoom.size(); }
		public void setInitialRoom(RoomInfo room) {
			if (iInitialRoom == null) iInitialRoom = new ArrayList<RoomInfo>();
			iInitialRoom.add(room);
		}
		public List<RoomInfo> getInitialRoom() { return iInitialRoom; }
		public void setAssignedTime(TimeInfo time) { iAssignedTime = time; }
		public TimeInfo getAssignedTime() { return iAssignedTime; }
		public int getNrAssignedRooms() { return iAssignedRoom == null ? 0 : iAssignedRoom.size(); }
		public void setAssignedRoom(RoomInfo room) {
			if (iAssignedRoom == null) iAssignedRoom = new ArrayList<RoomInfo>();
			iAssignedRoom.add(room);
		}
		public List<RoomInfo> getAssignedRoom() { return iAssignedRoom; }
		
		public boolean hasRooms() { return iRooms != null && !iRooms.isEmpty(); }
		public void addRoom(RoomInfo room) {
			if (iRooms == null) iRooms = new ArrayList<RoomInfo>();
			iRooms.add(room);
		}
		public List<RoomInfo> getRooms() { return iRooms; }
		
		public boolean hasTimes() { return iTimes != null && !iTimes.isEmpty(); }
		public void addTime(TimeInfo time) {
			if (iTimes == null) iTimes = new ArrayList<TimeInfo>();
			iTimes.add(time);
		}
		public List<TimeInfo> getTimes() { return iTimes; }
		
		public SelectedAssignment getSelection() {
			if (getTime() == null) return null;
			SelectedAssignment selection = new SelectedAssignment();
			selection.setClassId(getClazz().getClassId());
			selection.setDatePatternId(getTime().getDatePatternId());
			selection.setDays(getTime().getDays());
			selection.setPatternId(getTime().getPatternId());
			selection.setStartSlot(getTime().getStartSlot());
			if (getRoom() != null)
				for (RoomInfo room: getRoom())
					selection.addRoomId(room.getId());
			return selection;
		}
		
		public SelectedAssignment getAssignedSelection() {
			if (getAssignedTime() == null) return null;
			SelectedAssignment selection = new SelectedAssignment();
			selection.setClassId(getClazz().getClassId());
			selection.setDatePatternId(getAssignedTime().getDatePatternId());
			selection.setDays(getAssignedTime().getDays());
			selection.setPatternId(getAssignedTime().getPatternId());
			selection.setStartSlot(getAssignedTime().getStartSlot());
			if (getAssignedRoom() != null)
				for (RoomInfo room: getAssignedRoom())
					selection.addRoomId(room.getId());
			return selection;
		}

		public boolean hasStudentConflicts() { return iStudentConflicts != null && !iStudentConflicts.isEmpty(); }
		public void addStudentConflict(StudentConflictInfo conf) {
			if (iStudentConflicts == null) iStudentConflicts = new ArrayList<StudentConflictInfo>();
			iStudentConflicts.add(conf);
		}
		public List<StudentConflictInfo> getStudentConflicts() { return iStudentConflicts; }
		
		public boolean hasDistributionConflicts() { return iDistributionConflicts != null && !iDistributionConflicts.isEmpty(); }
		public void addDistributionConflict(DistributionInfo conf) {
			if (iDistributionConflicts == null) iDistributionConflicts = new ArrayList<DistributionInfo>();
			iDistributionConflicts.add(conf);
		}
		public List<DistributionInfo> getDistributionConflicts() { return iDistributionConflicts; }
		public boolean hasViolatedDistributionConflicts() {
			if (iDistributionConflicts == null) return false;
			for (DistributionInfo di: iDistributionConflicts)
				if (!di.getInfo().isSatisfied()) return true;
			return false;
		}
		
		public boolean hasBtbInstructorConflicts() { return iBtbInstructorConflicts != null && !iBtbInstructorConflicts.isEmpty(); }
		public void addBtbInstructorConflict(BtbInstructorInfo conf) {
			if (iBtbInstructorConflicts == null) iBtbInstructorConflicts = new ArrayList<BtbInstructorInfo>();
			iBtbInstructorConflicts.add(conf);
		}
		public List<BtbInstructorInfo> getBtbInstructorConflicts() { return iBtbInstructorConflicts; }
		
		public boolean isInitial() {
			return getAssignedTime()!=null && getAssignedRoom()!=null && getAssignedTime().equals(getInitialTime()) && getAssignedRoom().equals(getInitialRoom());
		}
		public int getNrDates() {
			Set<Long> dates = new HashSet<Long>();
			if (getTimes() != null)
				for (TimeInfo time: getTimes())
					dates.add(time.getDatePatternId());
			return dates.size();
		}
		
		public String getConflict() { return iConflict; }
		public void setConflict(String conflict) { iConflict = conflict; }
		public boolean hasConflict() { return iConflict != null && !iConflict.isEmpty(); }
		
		@Override
		public String toString() {
			String ret = (getClazz() == null ? "" : getClazz().toString());
			if (getAssignedTime() == null) {
				ret += ": " + getAssignedTime();
			} else if (getTime() != null) {
				ret += ": " + getTime();
			} else {
				ret += ": Not Assigned";
			}
			if (getAssignedRoom() != null) {
				for (int i = 0; i < getAssignedRoom().size(); i++)
					ret += (i == 0 ? "" : ", ") + getAssignedRoom().get(i);
			} else if (getRoom() != null) {
				for (int i = 0; i < getRoom().size(); i++)
					ret += (i == 0 ? "" : ", ") + getRoom().get(i);
			}
			return ret; 
		}
	}
	
	public static class PreferenceInterface implements IsSerializable {
		private String iCode, iName, iAbbv;
		private String iColor;
		private Long iId;
		private int iPreference;
		
		public PreferenceInterface() {}
		public PreferenceInterface(Long id, String color, String code, String name, String abbv, int preference) {
			iId = id; iColor = color; iCode = code; iName = name; iAbbv = abbv; iPreference = preference;
		}
		
		public String getColor() { return iColor; }
		public void setColor(String color) { iColor = color; }
		
		public String getCode() { return iCode; }
		public void setCode(String code) { iCode = code; }

		public String getName() { return iName; }
		public void setName(String name) { iName = name; }
		
		public String getAbbv() { return iAbbv; }
		public void setAbbv(String abbv) { iAbbv = abbv; }

		public Long getId() { return iId; }
		public void setId(Long id) { iId = id; }
		
		public int getPreference() { return iPreference; }
		public void setPreference(int preference) { iPreference = preference; }

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof PreferenceInterface)) return false;
			return getId().equals(((PreferenceInterface)o).getId());
		}
		
		@Override
		public int hashCode() {
			return getId().hashCode();
		}
	}
	
	public static class SuggestionProperties implements GwtRpcResponse, Serializable {
		private static final long serialVersionUID = 1L;
		List<PreferenceInterface> iPreferences = new ArrayList<PreferenceInterface>();
		
		public void addPreference(PreferenceInterface preference) { iPreferences.add(preference); }
		public List<PreferenceInterface> getPreferences() { return iPreferences; }
		public PreferenceInterface getPreference(String prefProlog) {
			for (PreferenceInterface p: iPreferences)
				if (p.getCode().equals(prefProlog)) return p;
			return null;
		}
		public PreferenceInterface getPreference(int intPref) {
			if (intPref >= 50)
				return getPreference("P");
			if (intPref >= 4)
				return getPreference("2");
			if (intPref > 0)
				return getPreference("1");
			if (intPref <= -50)
				return getPreference("R");
			if (intPref <= -4)
				return getPreference("-2");
			if (intPref < 0)
				return getPreference("-1");
			return getPreference("0");
		}
	}
	
	public static class SuggestionPropertiesRequest implements GwtRpcRequest<SuggestionProperties>{
		public SuggestionPropertiesRequest() {}
	}
	
	public static class SelectedAssignment implements GwtRpcResponse, Serializable {
		private static final long serialVersionUID = 1L;
		private Long iClassId;
		private int iDays = 0;
		private int iStartSlot = 0;
		private List<Long> iRoomIds;
		private Long iPatternId;
		private Long iDatePatternId;
		
		public SelectedAssignment() {}
		public SelectedAssignment(Long classId) {
			iClassId = classId;
		}
		public SelectedAssignment(Long classId, int days, int startSlot, List<Long> roomIds, Long patternId, Long datePatternId) {
			iClassId = classId; iDays = days; iStartSlot = startSlot; iRoomIds = roomIds; iPatternId = patternId; iDatePatternId = datePatternId;
		}
		
		public void setClassId(Long classId) { iClassId = classId; }
		public Long getClassId() { return iClassId; }
		public void setDays(int days) { iDays = days; }
		public int getDays() { return iDays; }
		public void setStartSlot(int slot) { iStartSlot = slot; }
		public int getStartSlot() { return iStartSlot; }
		public void setRoomIds(List<Long> roomIds) { iRoomIds = roomIds; }
		public void addRoomId(Long roomId) {
			if (iRoomIds == null) iRoomIds = new ArrayList<Long>();
			iRoomIds.add(roomId);
		}
		public List<Long> getRoomIds() { return iRoomIds; }
		public void setPatternId(Long patternId) { iPatternId = patternId; }
		public Long getPatternId() { return iPatternId; }
		public void setDatePatternId(Long patternId) { iDatePatternId = patternId; }
		public Long getDatePatternId() { return iDatePatternId; }
		
		@Override
		public boolean equals(Object o) {
			if (o==null || !(o instanceof SelectedAssignment)) return false;
			return iClassId.equals(((SelectedAssignment)o).getClassId());
		}
		
		@Override
		public int hashCode() { return iClassId.hashCode(); }
		
		@Override
		public String toString() {
			return "SelectedAssignment{class=" + getClassId() + ", days=" + getDays() + ", slot=" + getStartSlot() + ", rooms=" + getRoomIds() + "}";
		}
	}

	public static class SelectedAssignmentsRequest implements GwtRpcRequest<Suggestion>{
		private Long iClassId;
		private List<SelectedAssignment> iAssignments;
		
		public SelectedAssignmentsRequest() {}
		public SelectedAssignmentsRequest(Long classId) {
			setClassId(classId);
		}
		
		public void setClassId(Long classId) { iClassId = classId; }
		public Long getClassId() { return iClassId; }
		
		public boolean hasAssignments() { return iAssignments != null && !iAssignments.isEmpty(); }
		public void addAssignment(SelectedAssignment a) {
			if (iAssignments == null) iAssignments = new ArrayList<SelectedAssignment>();
			for (Iterator<SelectedAssignment> i = iAssignments.iterator(); i.hasNext(); ) {
				SelectedAssignment other = i.next();
				if (other.equals(a)) i.remove();
			}
			iAssignments.add(a);
		}
		public SelectedAssignment removeAssignment(Long classId) {
			if (iAssignments == null) return null;
			for (Iterator<SelectedAssignment> i = iAssignments.iterator(); i.hasNext(); ) {
				SelectedAssignment other = i.next();
				if (other.getClassId().equals(classId)) {
					i.remove();
					return other;
				}
			}
			return null;
		}
		public List<SelectedAssignment> getAssignments() { return iAssignments; }
	}
	
	public static class Suggestion implements GwtRpcResponse, Serializable, Comparable<Suggestion> {
		private static final long serialVersionUID = 1L;
	    private double iValue = 0;
	    private int iTooBigRooms = 0;
	    private long iUselessSlots = 0;
	    private double iGlobalTimePreference = 0;
	    private long iGlobalRoomPreference = 0;
	    private long iGlobalGroupConstraintPreference = 0;
	    private long iViolatedStudentConflicts = 0;
	    private long iHardStudentConflicts = 0;
	    private long iDistanceStudentConflicts = 0;
	    private long iCommitedStudentConflicts = 0;
	    private long iInstructorDistancePreference = 0;
	    private int iDepartmentSpreadPenalty = 0;
	    private int iUnassignedVariables = 0;
	    private double iPerturbationPenalty = 0;
	    private int iSpreadPenalty = 0;
	    private List<ClassAssignmentDetails> iUnresolvedConflicts = null;
	    private List<ClassAssignmentDetails> iDifferentAssignments = null;
	    private List<DistributionInfo> iDistributionConflicts = null;
	    private List<BtbInstructorInfo> iBtbInstructorConflicts = null;
	    private List<StudentConflictInfo> iStudentConflicts = null;
	    private boolean iCanAssign = true;
	    private ClassAssignmentDetails iPlacement = null;
	    private ClassAssignmentDetails iSelectedPlacement = null;
	    private double iBaseValue = 0;
	    private int iBaseTooBigRooms = 0;
	    private long iBaseUselessSlots = 0;
	    private double iBaseGlobalTimePreference = 0;
	    private long iBaseGlobalRoomPreference = 0;
	    private long iBaseGlobalGroupConstraintPreference = 0;
	    private long iBaseViolatedStudentConflicts = 0;
	    private long iBaseHardStudentConflicts = 0;
	    private long iBaseDistanceStudentConflicts = 0;
	    private long iBaseCommitedStudentConflicts = 0;
	    private long iBaseInstructorDistancePreference = 0;
	    private int iBaseDepartmentSpreadPenalty = 0;
	    private int iBaseUnassignedVariables = 0;
	    private double iBasePerturbationPenalty = 0;
	    private int iBaseSpreadPenalty = 0;
	    private Map<String, Double> iCriteria = new HashMap<String, Double>();
	    private Map<String, Double> iBaseCriteria = new HashMap<String, Double>();
		
		public Suggestion() {}
		
		public boolean hasUnresolvedConflicts() { return iUnresolvedConflicts != null && !iUnresolvedConflicts.isEmpty(); }
		public List<ClassAssignmentDetails> getUnresolvedConflicts() { return iUnresolvedConflicts; }
		public void addUnresolvedConflict(ClassAssignmentDetails conflict) {
			if (iUnresolvedConflicts == null)
				iUnresolvedConflicts = new ArrayList<ClassAssignmentDetails>();
			iUnresolvedConflicts.add(conflict);
		}
		
		public boolean hasDifferentAssignments() { return iDifferentAssignments != null && !iDifferentAssignments.isEmpty(); }
		public List<ClassAssignmentDetails> getDifferentAssignments() { return iDifferentAssignments; }
		public void addDifferentAssignment(ClassAssignmentDetails assignment) {
			if (iDifferentAssignments == null)
				iDifferentAssignments = new ArrayList<ClassAssignmentDetails>();
			iDifferentAssignments.add(assignment);
		}
		
		public boolean hasBtbInstructorConflicts() { return iBtbInstructorConflicts != null && !iBtbInstructorConflicts.isEmpty(); }
		public void addBtbInstructorConflict(BtbInstructorInfo conf) {
			if (iBtbInstructorConflicts == null) iBtbInstructorConflicts = new ArrayList<BtbInstructorInfo>();
			iBtbInstructorConflicts.add(conf);
		}
		public List<BtbInstructorInfo> getBtbInstructorConflicts() { return iBtbInstructorConflicts; }
		
		public boolean hasStudentConflicts() { return iStudentConflicts != null && !iStudentConflicts.isEmpty(); }
		public void addStudentConflict(StudentConflictInfo conf) {
			if (iStudentConflicts == null) iStudentConflicts = new ArrayList<StudentConflictInfo>();
			iStudentConflicts.add(conf);
		}
		public List<StudentConflictInfo> getStudentConflicts() { return iStudentConflicts; }
		
		public boolean hasDistributionConflicts() { return iDistributionConflicts != null && !iDistributionConflicts.isEmpty(); }
		public void addDistributionConflict(DistributionInfo conf) {
			if (iDistributionConflicts == null) iDistributionConflicts = new ArrayList<DistributionInfo>();
			iDistributionConflicts.add(conf);
		}
		public List<DistributionInfo> getDistributionConflicts() { return iDistributionConflicts; }
		public boolean hasViolatedDistributionConflicts() {
			if (iDistributionConflicts == null) return false;
			for (DistributionInfo di: iDistributionConflicts)
				if (!di.getInfo().isSatisfied()) return true;
			return false;
		}
		
		public boolean isCanAssign() { return iCanAssign; }
		public void setCanAssign(boolean canAssign) { iCanAssign = canAssign; }
		
		public double getValue() { return iValue; }
		public void setValue(double value) { iValue = value; }

		public int getTooBigRooms() { return iTooBigRooms; }
		public void setTooBigRooms(int tooBigRooms) { iTooBigRooms = tooBigRooms; }

		public long getUselessSlots() { return iUselessSlots; }
		public void setUselessSlots(long uselessSlots) { iUselessSlots = uselessSlots; }

		public double getGlobalTimePreference() { return iGlobalTimePreference; }
		public void setGlobalTimePreference(double globalTimePreference) { iGlobalTimePreference = globalTimePreference; }

		public long getGlobalRoomPreference() { return iGlobalRoomPreference; }
		public void setGlobalRoomPreference(long globalRoomPreference) { iGlobalRoomPreference = globalRoomPreference; }

		public long getGlobalGroupConstraintPreference() { return iGlobalGroupConstraintPreference; }
		public void setGlobalGroupConstraintPreference(long globalGroupConstraintPreference) { iGlobalGroupConstraintPreference = globalGroupConstraintPreference; }

		public long getViolatedStudentConflicts() { return iViolatedStudentConflicts; }
		public void setViolatedStudentConflicts(long violatedStudentConflicts) { iViolatedStudentConflicts = violatedStudentConflicts; }

		public long getHardStudentConflicts() { return iHardStudentConflicts; }
		public void setHardStudentConflicts(long hardStudentConflicts) { iHardStudentConflicts = hardStudentConflicts; }

		public long getDistanceStudentConflicts() { return iDistanceStudentConflicts; }
		public void setDistanceStudentConflicts(long distanceStudentConflicts) { iDistanceStudentConflicts = distanceStudentConflicts; }

		public long getCommitedStudentConflicts() { return iCommitedStudentConflicts; }
		public void setCommitedStudentConflicts(long commitedStudentConflicts) { iCommitedStudentConflicts = commitedStudentConflicts; }

		public long getInstructorDistancePreference() { return iInstructorDistancePreference; }
		public void setInstructorDistancePreference(long instructorDistancePreference) { iInstructorDistancePreference =instructorDistancePreference; }

		public int getDepartmentSpreadPenalty() { return iDepartmentSpreadPenalty; }
		public void setDepartmentSpreadPenalty(int departmentSpreadPenalty) { iDepartmentSpreadPenalty = departmentSpreadPenalty; }

		public int getUnassignedVariables() { return iUnassignedVariables; }
		public void setUnassignedVariables(int unassignedVariables) { iUnassignedVariables = unassignedVariables; }

		public double getPerturbationPenalty() { return iPerturbationPenalty; }
		public void setPerturbationPenalty(double perturbationPenalty) { iPerturbationPenalty = perturbationPenalty; }

		public int getSpreadPenalty() { return iSpreadPenalty; }
		public void setSpreadPenalty(int spreadPenalty) { iSpreadPenalty = spreadPenalty; }
		
		public ClassAssignmentDetails getPlacement() { return iPlacement; }
		public void setPlacement(ClassAssignmentDetails placement) { iPlacement = placement; }
		
		public ClassAssignmentDetails getSelectedPlacement() { return iSelectedPlacement; }
		public void setSelectedPlacement(ClassAssignmentDetails placement) { iSelectedPlacement = placement; }
		
		public double getBaseValue() { return iBaseValue; }
		public void setBaseValue(double value) { iBaseValue = value; }

		public int getBaseTooBigRooms() { return iBaseTooBigRooms; }
		public void setBaseTooBigRooms(int tooBigRooms) { iBaseTooBigRooms = tooBigRooms; }

		public long getBaseUselessSlots() { return iBaseUselessSlots; }
		public void setBaseUselessSlots(long uselessSlots) { iBaseUselessSlots = uselessSlots; }

		public double getBaseGlobalTimePreference() { return iBaseGlobalTimePreference; }
		public void setBaseGlobalTimePreference(double globalTimePreference) { iBaseGlobalTimePreference = globalTimePreference; }

		public long getBaseGlobalRoomPreference() { return iBaseGlobalRoomPreference; }
		public void setBaseGlobalRoomPreference(long globalRoomPreference) { iBaseGlobalRoomPreference = globalRoomPreference; }

		public long getBaseGlobalGroupConstraintPreference() { return iBaseGlobalGroupConstraintPreference; }
		public void setBaseGlobalGroupConstraintPreference(long globalGroupConstraintPreference) { iBaseGlobalGroupConstraintPreference = globalGroupConstraintPreference; }

		public long getBaseViolatedStudentConflicts() { return iBaseViolatedStudentConflicts; }
		public void setBaseViolatedStudentConflicts(long violatedStudentConflicts) { iBaseViolatedStudentConflicts = violatedStudentConflicts; }

		public long getBaseHardStudentConflicts() { return iBaseHardStudentConflicts; }
		public void setBaseHardStudentConflicts(long hardStudentConflicts) { iBaseHardStudentConflicts = hardStudentConflicts; }

		public long getBaseDistanceStudentConflicts() { return iBaseDistanceStudentConflicts; }
		public void setBaseDistanceStudentConflicts(long distanceStudentConflicts) { iBaseDistanceStudentConflicts = distanceStudentConflicts; }

		public long getBaseCommitedStudentConflicts() { return iBaseCommitedStudentConflicts; }
		public void setBaseCommitedStudentConflicts(long commitedStudentConflicts) { iBaseCommitedStudentConflicts = commitedStudentConflicts; }

		public long getBaseInstructorDistancePreference() { return iBaseInstructorDistancePreference; }
		public void setBaseInstructorDistancePreference(long instructorDistancePreference) { iBaseInstructorDistancePreference =instructorDistancePreference; }

		public int getBaseDepartmentSpreadPenalty() { return iBaseDepartmentSpreadPenalty; }
		public void setBaseDepartmentSpreadPenalty(int departmentSpreadPenalty) { iBaseDepartmentSpreadPenalty = departmentSpreadPenalty; }

		public int getBaseUnassignedVariables() { return iBaseUnassignedVariables; }
		public void setBaseUnassignedVariables(int unassignedVariables) { iBaseUnassignedVariables = unassignedVariables; }

		public double getBasePerturbationPenalty() { return iBasePerturbationPenalty; }
		public void setBasePerturbationPenalty(double perturbationPenalty) { iBasePerturbationPenalty = perturbationPenalty; }

		public int getBaseSpreadPenalty() { return iBaseSpreadPenalty; }
		public void setBaseSpreadPenalty(int spreadPenalty) { iBaseSpreadPenalty = spreadPenalty; }
		
		public void setCriterion(String name, Double value) { iCriteria.put(name, value); }
		public Map<String, Double> getCriteria() { return iCriteria; }
		public double getCriterion(String name) {
			Double value = iCriteria.get(name);
			return (value == null ? 0.0 : value.doubleValue());
		}
		
		public void setBaseCriterion(String name, Double value) { iBaseCriteria.put(name, value); }
		public Map<String, Double> getBaseCriteria() { return iBaseCriteria; }
		public double getBaseCriterion(String name) {
			Double value = iBaseCriteria.get(name);
			return (value == null ? 0.0 : value.doubleValue());
		}
		
		public List<SelectedAssignment> getAssignment(boolean conflicts) {
			List<SelectedAssignment> ret = new ArrayList<SelectedAssignment>();
			if (hasDifferentAssignments())
				for (ClassAssignmentDetails detail: getDifferentAssignments())
					ret.add(detail.getAssignedSelection());
			if (conflicts && hasUnresolvedConflicts())
				for (ClassAssignmentDetails detail: getUnresolvedConflicts())
					ret.add(new SelectedAssignment(detail.getClazz().getClassId()));
			return ret;
		}

		@Override
		public int compareTo(Suggestion other) {
			int cmp = Double.compare(getValue(), other.getValue());
			if (cmp != 0) return cmp;
			return (getDifferentAssignments() == null ? "" : getDifferentAssignments()).toString().compareTo(other.getDifferentAssignments() == null ? "" : other.getDifferentAssignments().toString());
		}
	}
	
	public static class MakeAssignmentRequest implements GwtRpcRequest<GwtRpcResponseNull>{
		private List<SelectedAssignment> iAssignments;
		
		public MakeAssignmentRequest() {}
		public MakeAssignmentRequest(List<SelectedAssignment> assignments) {
			iAssignments = assignments;
		}

		public boolean hasAssignments() { return iAssignments != null && !iAssignments.isEmpty(); }
		public void addAssignment(SelectedAssignment a) {
			if (iAssignments == null) iAssignments = new ArrayList<SelectedAssignment>();
			for (Iterator<SelectedAssignment> i = iAssignments.iterator(); i.hasNext(); ) {
				SelectedAssignment other = i.next();
				if (other.equals(a)) i.remove();
			}
			iAssignments.add(a);
		}
		public SelectedAssignment removeAssignment(Long classId) {
			if (iAssignments == null) return null;
			for (Iterator<SelectedAssignment> i = iAssignments.iterator(); i.hasNext(); ) {
				SelectedAssignment other = i.next();
				if (other.getClassId().equals(classId)) {
					i.remove();
					return other;
				}
			}
			return null;
		}
		public List<SelectedAssignment> getAssignments() { return iAssignments; }
	}
	
	public static class ComputeSuggestionsRequest implements GwtRpcRequest<Suggestions>{
		private Long iClassId;
		private List<SelectedAssignment> iAssignments;
		private int iDepth = 2;
		private int iLimit = 20;
		private int iTimeLimit = 5000;
		private boolean iAllowBreakHard = false, iSameRoom = false, iSameTime = false;
		
		public ComputeSuggestionsRequest() {}
		public ComputeSuggestionsRequest(Long classId) {
			setClassId(classId);
		}
		public ComputeSuggestionsRequest(Long classId, List<SelectedAssignment> assignments) {
			setClassId(classId);
			iAssignments = assignments;
		}
		
		public void setClassId(Long classId) { iClassId = classId; }
		public Long getClassId() { return iClassId; }
		
		public boolean hasAssignments() { return iAssignments != null && !iAssignments.isEmpty(); }
		public void addAssignment(SelectedAssignment a) {
			if (iAssignments == null) iAssignments = new ArrayList<SelectedAssignment>();
			for (Iterator<SelectedAssignment> i = iAssignments.iterator(); i.hasNext(); ) {
				SelectedAssignment other = i.next();
				if (other.equals(a)) i.remove();
			}
			iAssignments.add(a);
		}
		public SelectedAssignment removeAssignment(Long classId) {
			if (iAssignments == null) return null;
			for (Iterator<SelectedAssignment> i = iAssignments.iterator(); i.hasNext(); ) {
				SelectedAssignment other = i.next();
				if (other.getClassId().equals(classId)) {
					i.remove();
					return other;
				}
			}
			return null;
		}
		public List<SelectedAssignment> getAssignments() { return iAssignments; }
		
		public void setDepth(int depth) { iDepth = depth; }
		public int getDepth() { return iDepth; }
		
		public void setLimit(int limit) { iLimit = limit; }
		public int getLimit() { return iLimit; }
		
		public void setTimeLimit(int timeLimit) { iTimeLimit = timeLimit; }
		public int getTimeLimit() { return iTimeLimit; }
		
		public void setAllowBreakHard(boolean allowBreakHard) { iAllowBreakHard = allowBreakHard; }
		public boolean isAllowBreakHard() { return iAllowBreakHard; }
		public void setSameRoom(boolean sameRoom) { iSameRoom = sameRoom; }
		public boolean isSameRoom() { return iSameRoom; }
		public void setSameTime(boolean sameTime) { iSameTime = sameTime; }
		public boolean isSameTime() { return iSameTime; }
	}
	
	public static class Suggestions implements GwtRpcResponse, Serializable {
		private static final long serialVersionUID = 1L;
		private TreeSet<Suggestion> iSuggestions = new TreeSet<Suggestion>();
		private boolean iTimeoutReached = false;
	    private int iNrCombinationsConsidered = 0;
	    private int iNrSolutions = 0;
	    private int iDepth = 2;
	    private int iLimit = 20;
	    private int iTimeLimit = 5000;
	    private Long iClassId = null;
	    private boolean iAllowBreakHard = false, iSameRoom = false, iSameTime = false;
	    private Suggestion iBaseSuggestion;
		
		public Suggestions() {}
		
		public Suggestions(ComputeSuggestionsRequest request) {
			iLimit = request.getLimit();
			iTimeLimit = request.getTimeLimit();
			iDepth = request.getDepth();
			iClassId = request.getClassId();
			iAllowBreakHard = request.isAllowBreakHard();
			iSameRoom = request.isSameRoom();
			iSameTime = request.isSameTime();
		}
		
		public TreeSet<Suggestion> getSuggestions() { return iSuggestions; }
		public int size() { return iSuggestions.size(); }
		public Suggestion last() { return iSuggestions.last(); }
		public void addSuggestion(Suggestion suggestion) {
			iSuggestions.add(suggestion);
			if (iSuggestions.size() > iLimit) iSuggestions.remove(iSuggestions.last());
		}
		
		public void setTimeoutReached(boolean timeoutReached) { iTimeoutReached = timeoutReached; }
	    public boolean isTimeoutReached() { return iTimeoutReached; }
	    
	    public void setNrCombinationsConsidered(int nrCombinationsConsidered) { iNrCombinationsConsidered = nrCombinationsConsidered; }
	    public int getNrCombinationsConsidered() { return iNrCombinationsConsidered; }
	    
	    public void setNrSolutions(int nrSolutions) { iNrSolutions = nrSolutions; }
	    public int getNrSolutions() { return iNrSolutions; }
	    
	    public void setLimit(int limit) { iLimit = limit; }
	    public int getLimit() { return iLimit; }
	    
		public void setTimeLimit(int timeLimit) { iTimeLimit = timeLimit; }
		public int getTimeLimit() { return iTimeLimit; }
		
		public void setDepth(int depth) { iDepth = depth; }
		public int getDepth() { return iDepth; }
		
		public void setClassId(Long classId) { iClassId = classId; }
		public Long getClassId() { return iClassId; }
		
		public void setAllowBreakHard(boolean allowBreakHard) { iAllowBreakHard = allowBreakHard; }
		public boolean isAllowBreakHard() { return iAllowBreakHard; }
		public void setSameRoom(boolean sameRoom) { iSameRoom = sameRoom; }
		public boolean isSameRoom() { return iSameRoom; }
		public void setSameTime(boolean sameTime) { iSameTime = sameTime; }
		public boolean isSameTime() { return iSameTime; }
		
		public Suggestion getBaseSuggestion() { return iBaseSuggestion; }
		public void setBaseSuggestion(Suggestion suggestion) { iBaseSuggestion = suggestion; }
	}
}