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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.unitime.timetable.gwt.client.TimeHint;
import org.unitime.timetable.gwt.client.rooms.RoomHint;
import org.unitime.timetable.gwt.client.solver.SolverCookie;
import org.unitime.timetable.gwt.client.widgets.P;
import org.unitime.timetable.gwt.client.widgets.UniTimeTable;
import org.unitime.timetable.gwt.client.widgets.UniTimeTableHeader;
import org.unitime.timetable.gwt.client.widgets.UniTimeTableHeader.HasColumnName;
import org.unitime.timetable.gwt.client.widgets.UniTimeTableHeader.Operation;
import org.unitime.timetable.gwt.resources.GwtConstants;
import org.unitime.timetable.gwt.resources.GwtMessages;
import org.unitime.timetable.gwt.resources.GwtResources;
import org.unitime.timetable.gwt.shared.SuggestionsInterface.ClassAssignmentDetails;
import org.unitime.timetable.gwt.shared.SuggestionsInterface.RoomInfo;
import org.unitime.timetable.gwt.shared.SuggestionsInterface.SuggestionProperties;
import org.unitime.timetable.gwt.shared.SuggestionsInterface.TimeInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Tomas Muller
 */
public class AssignmentTable extends UniTimeTable<ClassAssignmentDetails>{
	protected static final GwtMessages MESSAGES = GWT.create(GwtMessages.class);
	protected static final GwtConstants CONSTANTS = GWT.create(GwtConstants.class);
	protected static final GwtResources RESOURCES =  GWT.create(GwtResources.class);
	public static NumberFormat sDF = NumberFormat.getFormat("0.###");
	
	private boolean iConflicts, iCanRemove;
	private SuggestionProperties iProperties;

	private AssignmentColumn iSortBy = null;
	private boolean iAsc = true;
	
	public AssignmentTable(SuggestionProperties properties, boolean conflicts, boolean canRemove) {
		addStyleName("unitime-ClassAssignmentTable");
		iProperties = properties;
		iConflicts = conflicts;
		iCanRemove = canRemove;
		List<UniTimeTableHeader> header = new ArrayList<UniTimeTableHeader>();

		for (AssignmentColumn column: AssignmentColumn.values()) {
			int nrCells = getNbrCells(column);
			for (int idx = 0; idx < nrCells; idx++) {
				UniTimeTableHeader h = new UniTimeTableHeader(getColumnName(column, idx), getColumnAlignment(column, idx));
				header.add(h);
			}
		}
		
		for (final AssignmentColumn column: AssignmentColumn.values()) {
			if (AssignmentComparator.isApplicable(column) && getNbrCells(column) > 0) {
				final UniTimeTableHeader h = header.get(getCellIndex(column));
				Operation op = new SortOperation() {
					@Override
					public void execute() {
						doSort(column);
					}
					@Override
					public boolean isApplicable() { return getRowCount() > 1 && h.isVisible(); }
					@Override
					public boolean hasSeparator() { return false; }
					@Override
					public String getName() { return MESSAGES.opSortBy(getColumnName()); }
					@Override
					public String getColumnName() { return h.getHTML().replace("<br>", " "); }
				};
				h.addOperation(op);
			}
		}
		
		addRow(null, header);
		
		for (int i = 0; i < getCellCount(0); i++)
			getCellFormatter().setStyleName(0, i, "unitime-ClickableTableHeader");
		
		setSortBy(iConflicts ? SolverCookie.getInstance().getConflictingAssignmentsSort() : SolverCookie.getInstance().getSelectedAssignmentsSort());
	}
	
	protected void doSort(AssignmentColumn column) {
		if (column == iSortBy) {
			iAsc = !iAsc;
		} else {
			iSortBy = column;
			iAsc = true;
		}
		if (iConflicts)
			SolverCookie.getInstance().setConflictingAssignmentsSort(getSortBy());
		else
			SolverCookie.getInstance().setSelectedAssignmentsSort(getSortBy());
		sort();
	}
	
	protected void onRemove(ClassAssignmentDetails details) {}
	
	public boolean hasSortBy() { return iSortBy != null; }
	public int getSortBy() { return iSortBy == null ? 0 : iAsc ? 1 + iSortBy.ordinal() : -1 - iSortBy.ordinal(); }
	public void setSortBy(int sortBy) {
		if (sortBy == 0) {
			iSortBy = null;
			iAsc = true;
		} else if (sortBy > 0) {
			iSortBy = AssignmentColumn.values()[sortBy - 1];
			iAsc = true;
		} else {
			iSortBy = AssignmentColumn.values()[-1 - sortBy];
			iAsc = false;
		}
		sort();
	}
	
	public void sort() {
		if (iSortBy == null) return;
		if (getNbrCells(iSortBy) == 0) iSortBy = AssignmentColumn.CLASS;
		UniTimeTableHeader header = getHeader(getCellIndex(iSortBy));
		sort(header, new AssignmentComparator(iSortBy, true), iAsc);
	}
	
	public static enum AssignmentColumn {
		CLASS,
		DATE,
		TIME,
		ROOM,
		// STD,TM,RM,DIST,INS,USL,BIG,DEPT,SUBP,PERT,
		CONSTRAINT,
		OBJECTIVES,
		;
	}
	
	protected int getNbrCells(AssignmentColumn column) {
		switch (column) {
		case CONSTRAINT:
			return (iConflicts ? 1 : 0);
		case OBJECTIVES:
			return (iConflicts ? 0 : 1);
		default:
			return 1;
		}
	}
	
	public String getColumnName(AssignmentColumn column, int idx) {
		switch (column) {
		case CLASS: return MESSAGES.colClass();
		case DATE: return MESSAGES.colDate();
		case TIME: return MESSAGES.colTime();
		case ROOM: return MESSAGES.colRoom();
		/*
		case STD: return MESSAGES.colShortStudentConflicts();
		case TM: return MESSAGES.colShortTimePref();
		case RM: return MESSAGES.colShortRoomPref();
		case DIST: return MESSAGES.colShortDistPref();
		case INS: return MESSAGES.colShortInstructorBtbPref();
		case USL: return MESSAGES.colShortUselessHalfHours();
		case BIG: return MESSAGES.colShortTooBigRooms();
		case DEPT: return MESSAGES.colShortDepartmentBalance();
		case SUBP: return MESSAGES.colShortSameSubpartBalance();
		case PERT: return MESSAGES.colShortPerturbations();
		*/
		case CONSTRAINT: return MESSAGES.colConflictingConstraint();
		case OBJECTIVES: return MESSAGES.colObjectives();
		default: return column.name();
		}
	}
	
	protected HorizontalAlignmentConstant getColumnAlignment(AssignmentColumn column, int idx) {
		switch (column) {
		default:
			return HasHorizontalAlignment.ALIGN_LEFT;
		}
	}
	
	protected int getCellIndex(AssignmentColumn column) {
		int ret = 0;
		for (AssignmentColumn c: AssignmentColumn.values())
			if (c.ordinal() < column.ordinal()) ret += getNbrCells(c);
		return ret;
	}
	
	protected Widget getCell(final ClassAssignmentDetails details, final AssignmentColumn column, final int idx) {
		switch (column) {
		case CLASS:
			if (iCanRemove) {
				P clazz = new P("class");
				Image image = new Image(RESOURCES.delete());
				image.addStyleName("delete");
				image.setTitle(MESSAGES.titleRemoveSelectedClassAssignment(details.getClazz().getName()));
				image.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						AssignmentTable.this.onRemove(details);
						event.preventDefault(); event.stopPropagation();
					}
				});
				clazz.add(image);
				P classLabel = new P("label"); 
				classLabel.setText(details.getClazz().getName());
				if (details.getClazz().getPref() != null)
					classLabel.getElement().getStyle().setColor(iProperties.getPreference(details.getClazz().getPref()).getColor());
				clazz.add(classLabel);
				return clazz;
			} else {
				P classLabel = new P("class"); 
				classLabel.setText(details.getClazz().getName());
				if (details.getClazz().getPref() != null)
					classLabel.getElement().getStyle().setColor(iProperties.getPreference(details.getClazz().getPref()).getColor());
				return classLabel;
			}
		case DATE:
			P date = new P("date");
			if (details.getTime() != null) {
				P current = new P("old");
				current.setText(details.getTime().getDatePatternName());
				if (details.getTime().getDatePatternPreference() != 0)
					current.getElement().getStyle().setColor(iProperties.getPreference(details.getTime().getDatePatternPreference()).getColor());
				if (details.getTime().isStriked())
					current.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
				date.add(current);
				if (details.getAssignedTime() == null) {
					// P arrow = new P("arrow"); arrow.setHTML(MESSAGES.assignmentArrow()); date.add(arrow);
					// P notAssigned = new P("not-assigned"); notAssigned.setText(MESSAGES.unassignment()); date.add(notAssigned);
				} else if (!details.getAssignedTime().getDatePatternName().equals(details.getTime().getDatePatternName())) {
					P arrow = new P("arrow"); arrow.setHTML(MESSAGES.assignmentArrow()); date.add(arrow);
					P other = new P("new"); other.setText(details.getAssignedTime().getDatePatternName());
					if (details.getAssignedTime().getDatePatternPreference() != 0)
						other.getElement().getStyle().setColor(iProperties.getPreference(details.getAssignedTime().getDatePatternPreference()).getColor());
					if (details.getAssignedTime().isStriked())
						other.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
					date.add(other);
				}
			} else if (details.getAssignedTime() != null) {
				P other = new P("new"); other.setText(details.getAssignedTime().getDatePatternName());
				if (details.getAssignedTime().getDatePatternPreference() != 0)
					other.getElement().getStyle().setColor(iProperties.getPreference(details.getAssignedTime().getDatePatternPreference()).getColor());
				if (details.getAssignedTime().isStriked())
					other.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
				date.add(other);
			}
			return date;
		case TIME:
			P time = new P("time");
			if (details.getTime() != null) {
				final P current = new P("old");
				current.setText(details.getTime().getName(false, CONSTANTS));
				if (details.getTime().getPref() != 0)
					current.getElement().getStyle().setColor(iProperties.getPreference(details.getTime().getPref()).getColor());
				if (details.getTime().isStriked())
					current.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
				final String timeHint = details.getClazz().getClassId() + "," + details.getTime().getDays() + "," + details.getTime().getStartSlot();
				current.addMouseOverHandler(new MouseOverHandler() {
					@Override
					public void onMouseOver(MouseOverEvent e) {
						TimeHint.showHint(current.getElement(), timeHint);
					}
				});
				current.addMouseOutHandler(new MouseOutHandler() {
					@Override
					public void onMouseOut(MouseOutEvent e) {
						TimeHint.hideHint();
					}
				});
				time.add(current);
				if (details.getAssignedTime() == null) {
					P arrow = new P("arrow"); arrow.setHTML(MESSAGES.assignmentArrow()); time.add(arrow);
					P notAssigned = new P("not-assigned"); notAssigned.setText(MESSAGES.unassignment()); time.add(notAssigned);
				} else if (details.getAssignedTime().getStartSlot() != details.getTime().getStartSlot() || details.getAssignedTime().getDays() != details.getTime().getDays() || !details.getAssignedTime().getPatternId().equals(details.getTime().getPatternId())) {
					P arrow = new P("arrow"); arrow.setHTML(MESSAGES.assignmentArrow()); time.add(arrow);
					final P other = new P("new"); other.setText(details.getAssignedTime().getName(false, CONSTANTS));
					if (details.getAssignedTime().getPref() != 0)
						other.getElement().getStyle().setColor(iProperties.getPreference(details.getAssignedTime().getPref()).getColor());
					if (details.getAssignedTime().isStriked())
						other.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
					final String otherTimeHint = details.getClazz().getClassId() + "," + details.getAssignedTime().getDays() + "," + details.getAssignedTime().getStartSlot();
					other.addMouseOverHandler(new MouseOverHandler() {
						@Override
						public void onMouseOver(MouseOverEvent e) {
							TimeHint.showHint(other.getElement(), otherTimeHint);
						}
					});
					other.addMouseOutHandler(new MouseOutHandler() {
						@Override
						public void onMouseOut(MouseOutEvent e) {
							TimeHint.hideHint();
						}
					});
					time.add(other);
				}
			} else if (details.getAssignedTime() != null) {
				final P other = new P("new"); other.setText(details.getAssignedTime().getName(false, CONSTANTS));
				if (details.getAssignedTime().getPref() != 0)
					other.getElement().getStyle().setColor(iProperties.getPreference(details.getAssignedTime().getPref()).getColor());
				if (details.getAssignedTime().isStriked())
					other.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
				final String otherTimeHint = details.getClazz().getClassId() + "," + details.getAssignedTime().getDays() + "," + details.getAssignedTime().getStartSlot();
				other.addMouseOverHandler(new MouseOverHandler() {
					@Override
					public void onMouseOver(MouseOverEvent e) {
						TimeHint.showHint(other.getElement(), otherTimeHint);
					}
				});
				other.addMouseOutHandler(new MouseOutHandler() {
					@Override
					public void onMouseOut(MouseOutEvent e) {
						TimeHint.hideHint();
					}
				});
				time.add(other);
			}
			return time;
		case ROOM:
			P rooms = new P("rooms");
			if (details.getRoom() != null) {
				for (int i = 0; i < details.getRoom().size(); i++) {
					final RoomInfo r = details.getRoom().get(i);
					P room = new P("room");
					final P current = new P("old");
					current.setText(r.getName());
					if (r.getPref() != 0)
						current.getElement().getStyle().setColor(iProperties.getPreference(r.getPref()).getColor());
					if (r.isStriked())
						current.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
					if (r.getId() != null) {
						current.addMouseOverHandler(new MouseOverHandler() {
							@Override
							public void onMouseOver(MouseOverEvent e) {
								RoomHint.showHint(current.getElement(), r.getId(), null, null, true);
							}
						});
						current.addMouseOutHandler(new MouseOutHandler() {
							@Override
							public void onMouseOut(MouseOutEvent e) {
								RoomHint.hideHint();
							}
						});
					}
					room.add(current);
					if (details.getAssignedRoom() != null && i < details.getAssignedRoom().size()) {
						final RoomInfo q = details.getAssignedRoom().get(i);
						if (!q.getId().equals(r.getId())) {
							P arrow = new P("arrow"); arrow.setHTML(MESSAGES.assignmentArrow()); room.add(arrow);
							final P other = new P("new"); other.setText(q.getName());
							if (q.getPref() != 0)
								other.getElement().getStyle().setColor(iProperties.getPreference(q.getPref()).getColor());
							if (q.isStriked())
								other.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
							if (q.getId() != null) {
								other.addMouseOverHandler(new MouseOverHandler() {
									@Override
									public void onMouseOver(MouseOverEvent e) {
										RoomHint.showHint(other.getElement(), q.getId(), null, null, true);
									}
								});
								other.addMouseOutHandler(new MouseOutHandler() {
									@Override
									public void onMouseOut(MouseOutEvent e) {
										RoomHint.hideHint();
									}
								});
							}
							room.add(other);
						}
					} else {
						P arrow = new P("arrow"); arrow.setHTML(MESSAGES.assignmentArrow()); room.add(arrow);
						P notAssigned = new P("not-assigned"); notAssigned.setText(MESSAGES.unassignment()); room.add(notAssigned);
					}
					if (i + 1 < details.getRoom().size()) {
						P separator = new P("separator"); separator.setText(CONSTANTS.itemSeparator()); rooms.add(separator);
						room.add(separator);
					}
					rooms.add(room);
				}
			} else if (details.getAssignedRoom() != null) {
				for (int i = 0; i < details.getAssignedRoom().size(); i++) {
					final RoomInfo q = details.getAssignedRoom().get(i);
					P room = new P("room");
					final P other = new P("new"); other.setText(q.getName());
					if (q.getPref() != 0)
						other.getElement().getStyle().setColor(iProperties.getPreference(q.getPref()).getColor());
					if (q.isStriked())
						other.getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
					if (q.getId() != null) {
						other.addMouseOverHandler(new MouseOverHandler() {
							@Override
							public void onMouseOver(MouseOverEvent e) {
								RoomHint.showHint(other.getElement(), q.getId(), null, null, true);
							}
						});
						other.addMouseOutHandler(new MouseOutHandler() {
							@Override
							public void onMouseOut(MouseOutEvent e) {
								RoomHint.hideHint();
							}
						});
					}
					room.add(other);
					if (i + 1 < details.getAssignedRoom().size()) {
						P separator = new P("separator"); separator.setText(CONSTANTS.itemSeparator()); rooms.add(separator);
						room.add(separator);
					}
					rooms.add(room);
				}
			}
			return rooms;
			/*
		case STD:
			String s = dispNumberShort(details.getAssignedPreferences()==null,(details.getPreferences()==null?0:details.getPreferences().getNrStudentConflicts()),(details.getAssignedPreferences()==null?0:details.getAssignedPreferences().getNrStudentConflicts()));
			String c = dispNumberShort(details.getAssignedPreferences()==null,"c",(details.getPreferences()==null?0:details.getPreferences().getNrCommitedStudentConflicts()),(details.getAssignedPreferences()==null?0:details.getAssignedPreferences().getNrCommitedStudentConflicts()));
			String d = dispNumberShort(details.getAssignedPreferences()==null,"d",(details.getPreferences()==null?0:details.getPreferences().getNrDistanceStudentConflicts()),(details.getAssignedPreferences()==null?0:details.getAssignedPreferences().getNrDistanceStudentConflicts()));
			String h = dispNumberShort(details.getAssignedPreferences()==null,"h",(details.getPreferences()==null?0:details.getPreferences().getNrHardStudentConflicts()),(details.getAssignedPreferences()==null?0:details.getAssignedPreferences().getNrHardStudentConflicts()));
			if (c.isEmpty() && d.isEmpty() && h.isEmpty()) return new HTML(s, false);
			StringBuffer sb = new StringBuffer();
			if (c.length()>0) {
				sb.append(c);
			}
			if (d.length()>0) {
				if (sb.length()>0) sb.append(",");
				sb.append(d);
			}
			if (h.length()>0) {
				if (sb.length()>0) sb.append(",");
				sb.append(h);
			}
			return new HTML(s+" ("+sb+")", false);
		case TM:
			return new HTML(dispNumberShort(details.getAssignedPreferences()==null,(details.getPreferences()==null?0.0:details.getPreferences().getNormalizedTimePreference()),(details.getAssignedPreferences()==null?0.0:details.getAssignedPreferences().getNormalizedTimePreference())), false);
		case RM:
			return new HTML(dispNumberShort(details.getAssignedPreferences()==null,(details.getPreferences()==null?0.0:details.getPreferences().sumRoomPreference()),(details.getAssignedPreferences()==null?0.0:details.getAssignedPreferences().sumRoomPreference())), false);
		case DIST:
			return new HTML(dispNumberShort(details.getAssignedPreferences()==null,(details.getPreferences()==null?0.0:details.getPreferences().getGroupConstraintPref()),(details.getAssignedPreferences()==null?0.0:details.getAssignedPreferences().getGroupConstraintPref())), false);
		case INS:
			return new HTML(dispNumberShort(details.getAssignedPreferences()==null,(details.getPreferences()==null?0.0:details.getPreferences().getBtbInstructorPreference()),(details.getAssignedPreferences()==null?0.0:details.getAssignedPreferences().getBtbInstructorPreference())), false);
		case USL:
			return new HTML(dispNumberShort(details.getAssignedPreferences()==null,(details.getPreferences()==null?0.0:details.getPreferences().getUselessHalfHours()),(details.getAssignedPreferences()==null?0.0:details.getAssignedPreferences().getUselessHalfHours())), false);
		case BIG:
			return new HTML(dispNumberShort(details.getAssignedPreferences()==null,(details.getPreferences()==null?0.0:details.getPreferences().getTooBigRoomPreference()),(details.getAssignedPreferences()==null?0.0:details.getAssignedPreferences().getTooBigRoomPreference())), false);
		case DEPT:
			return new HTML(dispNumberShort(details.getAssignedPreferences()==null,(details.getPreferences()==null?0.0:details.getPreferences().getDeptBalancPenalty()),(details.getAssignedPreferences()==null?0.0:details.getAssignedPreferences().getDeptBalancPenalty())), false);
		case SUBP:
			return new HTML(dispNumberShort(details.getAssignedPreferences()==null,(details.getPreferences()==null?0.0:details.getPreferences().getSpreadPenalty()),(details.getAssignedPreferences()==null?0.0:details.getAssignedPreferences().getSpreadPenalty())), false);
		case PERT:
			return new HTML(dispNumberShort(details.getAssignedPreferences()==null,(details.getPreferences()==null?0.0:details.getPreferences().getPerturbationPenalty()),(details.getAssignedPreferences()==null?0.0:details.getAssignedPreferences().getPerturbationPenalty())), false);
			*/
		case CONSTRAINT:
			if (details.hasConflict()) return new Label(details.getConflict());
			return null;
		case OBJECTIVES:
			P objectives = new P("objectives");
			if (details.getPreferences() != null) {
				if (details.getAssignedPreferences() != null) {
					for (String criterion: new TreeSet<String>(details.getAssignedPreferences().getCriteria().keySet())) {
						double value = details.getAssignedPreferences().getCriterion(criterion);
						double base = details.getPreferences().getCriterion(criterion);
						if (value != base) {
							P obj = new P("objective");
							obj.setHTML(criterion + ": " + dispNumber(value, base));
							objectives.add(obj);
						}
					}
				} else {
					for (String criterion: new TreeSet<String>(details.getPreferences().getCriteria().keySet())) {
						double value = 0.0;
						double base = details.getPreferences().getCriterion(criterion);
						if (value != base) {
							P obj = new P("objective");
							obj.setHTML(criterion + ": " + dispNumber(value, base));
							objectives.add(obj);
						}
					}
				}
			} else if (details.getAssignedPreferences() != null) {
				for (String criterion: new TreeSet<String>(details.getAssignedPreferences().getCriteria().keySet())) {
					double value = details.getAssignedPreferences().getCriterion(criterion);
					double base = 0.0;
					if (value != base) {
						P obj = new P("objective");
						obj.setHTML(criterion + ": " + dispNumber(value, base));
						objectives.add(obj);
					}
				}
			}
			return objectives;
		default:
			return null;
		}
	}
	
	public int addRow(final ClassAssignmentDetails details) {
		List<Widget> widgets = new ArrayList<Widget>();
		
		for (AssignmentColumn column: AssignmentColumn.values()) {
			int nbrCells = getNbrCells(column);
			for (int idx = 0; idx < nbrCells; idx ++) {
				Widget cell = getCell(details, column, idx);
				if (cell == null)
					cell = new P();
				widgets.add(cell);
			}
		}
		
		int row = addRow(details, widgets);
		getRowFormatter().setStyleName(row, "row");
		for (int col = 0; col < getCellCount(row); col++)
			getCellFormatter().setStyleName(row, col, "cell");
		
		return row;
	}
	
	public static class AssignmentComparator implements Comparator<ClassAssignmentDetails> {
		private AssignmentColumn iColumn;
		private boolean iAsc;
		
		public AssignmentComparator(AssignmentColumn column, boolean asc) {
			iColumn = column;
			iAsc = asc;
		}

		public int compareById(ClassAssignmentDetails c1, ClassAssignmentDetails c2) {
			return compare(c1.getClazz().getClassId(), c2.getClazz().getClassId());
		}
		
		public int compareByName(ClassAssignmentDetails c1, ClassAssignmentDetails c2) {
			return c1.getClazz().compareTo(c2.getClazz());
		}
		
		public int compareByDate(TimeInfo t1, TimeInfo t2) {
			return compare(t1 == null ? null : t1.getDatePatternName(), t2 == null ? null : t2.getDatePatternName());
		}
		
		public int compareByTime(TimeInfo t1, TimeInfo t2) {
			int cmp = compare(t1 == null ? null : t1.getDays(), t2 == null ? null : t2.getDays());
			if (cmp != 0) return cmp;
			cmp = compare(t1 == null ? null : t1.getStartSlot(), t2 == null ? null : t2.getStartSlot());
			if (cmp != 0) return cmp;
			return compare(t1 == null ? null : t1.getName(false, CONSTANTS), t2 == null ? null : t2.getName(false, CONSTANTS));
		}

		public int compareByRoom(List<RoomInfo> r1, List<RoomInfo> r2) {
			int c1 = (r1 == null ? 0 : r1.size());
			int c2 = (r2 == null ? 0 : r2.size());
			if (c1 != c2) return c1 < c2 ? -1 : 1;
			for (int i = 0; i < c1; i++) {
				int cmp = compare(r1.get(i).getName(), r2.get(i).getName());
				if (cmp != 0) return cmp;
			}
			return 0;
		}
		
		protected int compareByColumn(ClassAssignmentDetails c1, ClassAssignmentDetails c2) {
			switch (iColumn) {
			case CLASS: return compareByName(c1, c2);
			case DATE: return compareByDate(c1.getAssignedTime() == null ? c1.getTime() : c1.getAssignedTime(), c2.getAssignedTime() == null ? c2.getTime() : c2.getAssignedTime());
			case TIME: return compareByTime(c1.getAssignedTime() == null ? c1.getTime() : c1.getAssignedTime(), c2.getAssignedTime() == null ? c2.getTime() : c2.getAssignedTime());
			case ROOM: return compareByRoom(c1.getAssignedRoom() == null ? c1.getRoom() : c1.getAssignedRoom(), c2.getAssignedRoom() == null ? c2.getRoom() : c2.getAssignedRoom());
			/*
			case STD: return compare(
					(c1.getPreferences() == null ? 0 : c1.getPreferences().getNrStudentConflicts()) - (c1.getAssignedPreferences() == null ? 0 : c1.getAssignedPreferences().getNrStudentConflicts()),
					(c2.getPreferences() == null ? 0 : c2.getPreferences().getNrStudentConflicts()) - (c2.getAssignedPreferences() == null ? 0 : c2.getAssignedPreferences().getNrStudentConflicts()));
			case TM: return compare(
					(c1.getPreferences() == null ? 0 : c1.getPreferences().getTimePreference()) - (c1.getAssignedPreferences() == null ? 0 : c1.getAssignedPreferences().getTimePreference()),
					(c2.getPreferences() == null ? 0 : c2.getPreferences().getTimePreference()) - (c2.getAssignedPreferences() == null ? 0 : c2.getAssignedPreferences().getTimePreference()));
			case RM: return compare(
					(c1.getPreferences() == null ? 0 : c1.getPreferences().sumRoomPreference()) - (c1.getAssignedPreferences() == null ? 0 : c1.getAssignedPreferences().sumRoomPreference()),
					(c2.getPreferences() == null ? 0 : c2.getPreferences().sumRoomPreference()) - (c2.getAssignedPreferences() == null ? 0 : c2.getAssignedPreferences().sumRoomPreference()));
			case DIST: return compare(
					(c1.getPreferences() == null ? 0 : c1.getPreferences().getGroupConstraintPref()) - (c1.getAssignedPreferences() == null ? 0 : c1.getAssignedPreferences().getGroupConstraintPref()),
					(c2.getPreferences() == null ? 0 : c2.getPreferences().getGroupConstraintPref()) - (c2.getAssignedPreferences() == null ? 0 : c2.getAssignedPreferences().getGroupConstraintPref()));
			case BIG: return compare(
					(c1.getPreferences() == null ? 0 : c1.getPreferences().getTooBigRoomPreference()) - (c1.getAssignedPreferences() == null ? 0 : c1.getAssignedPreferences().getTooBigRoomPreference()),
					(c2.getPreferences() == null ? 0 : c2.getPreferences().getTooBigRoomPreference()) - (c2.getAssignedPreferences() == null ? 0 : c2.getAssignedPreferences().getTooBigRoomPreference()));
			case DEPT: return compare(
					(c1.getPreferences() == null ? 0 : c1.getPreferences().getDeptBalancPenalty()) - (c1.getAssignedPreferences() == null ? 0 : c1.getAssignedPreferences().getDeptBalancPenalty()),
					(c2.getPreferences() == null ? 0 : c2.getPreferences().getDeptBalancPenalty()) - (c2.getAssignedPreferences() == null ? 0 : c2.getAssignedPreferences().getDeptBalancPenalty()));
			case INS: return compare(
					(c1.getPreferences() == null ? 0 : c1.getPreferences().getBtbInstructorPreference()) - (c1.getAssignedPreferences() == null ? 0 : c1.getAssignedPreferences().getBtbInstructorPreference()),
					(c2.getPreferences() == null ? 0 : c2.getPreferences().getBtbInstructorPreference()) - (c2.getAssignedPreferences() == null ? 0 : c2.getAssignedPreferences().getBtbInstructorPreference()));
			case PERT: return compare(
					(c1.getPreferences() == null ? 0 : c1.getPreferences().getPerturbationPenalty()) - (c1.getAssignedPreferences() == null ? 0 : c1.getAssignedPreferences().getPerturbationPenalty()),
					(c2.getPreferences() == null ? 0 : c2.getPreferences().getPerturbationPenalty()) - (c2.getAssignedPreferences() == null ? 0 : c2.getAssignedPreferences().getPerturbationPenalty()));
			case SUBP: return compare(
					(c1.getPreferences() == null ? 0 : c1.getPreferences().getSpreadPenalty()) - (c1.getAssignedPreferences() == null ? 0 : c1.getAssignedPreferences().getSpreadPenalty()),
					(c2.getPreferences() == null ? 0 : c2.getPreferences().getSpreadPenalty()) - (c2.getAssignedPreferences() == null ? 0 : c2.getAssignedPreferences().getSpreadPenalty()));
			case USL: return compare(
					(c1.getPreferences() == null ? 0 : c1.getPreferences().getUselessHalfHours()) - (c1.getAssignedPreferences() == null ? 0 : c1.getAssignedPreferences().getUselessHalfHours()),
					(c2.getPreferences() == null ? 0 : c2.getPreferences().getUselessHalfHours()) - (c2.getAssignedPreferences() == null ? 0 : c2.getAssignedPreferences().getUselessHalfHours()));
					*/
			case CONSTRAINT: return compare(c1.getConflict(), c2.getConflict());
			default: return compareByName(c1, c2);
			}
		}
		
		public static boolean isApplicable(AssignmentColumn column) {
			switch (column) {
			case CLASS:
			case DATE:
			case TIME:
			case ROOM:
				/*
			case STD:
			case TM:
			case RM:
			case DIST:
			case BIG:
			case DEPT:
			case INS:
			case PERT:
			case SUBP:
			case USL:
			*/
			case CONSTRAINT:
				return true;
			default:
				return false;
			}
		}
		
		@Override
		public int compare(ClassAssignmentDetails c1, ClassAssignmentDetails c2) {
			int cmp = compareByColumn(c1, c2);
			if (cmp == 0) cmp = compareByName(c1, c2);
			if (cmp == 0) cmp = compareById(c1, c2);
			return (iAsc ? cmp : -cmp);
		}
		
		protected int compare(String s1, String s2) {
			if (s1 == null || s1.isEmpty()) {
				return (s2 == null || s2.isEmpty() ? 0 : 1);
			} else {
				return (s2 == null || s2.isEmpty() ? -1 : s1.compareToIgnoreCase(s2));
			}
		}
		
		protected int compare(Number n1, Number n2) {
			return (n1 == null ? n2 == null ? 0 : -1 : n2 == null ? 1 : Double.compare(n1.doubleValue(), n2.doubleValue())); 
		}
		
		protected int compare(Boolean b1, Boolean b2) {
			return (b1 == null ? b2 == null ? 0 : -1 : b2 == null ? 1 : (b1.booleanValue() == b2.booleanValue()) ? 0 : (b1.booleanValue() ? 1 : -1));
		}
	}
	
	public static interface SortOperation extends Operation, HasColumnName {}
	
	public static String dispNumber(int number) {
		return dispNumber("",number);
	}
	
	public static String dispNumber(String prefix, int number) {
		if (number>0) return "<font color='red'>"+prefix+"+"+number+"</font>";
	    if (number<0) return "<font color='green'>"+prefix+number+"</font>";
	    return prefix+"0";
	}
	
	public static String dispNumberShort(boolean rem, int n1, int n2) {
		if (n1==0 && n2==0) return "";
		if (rem) return dispNumber(-n1);
		int dif = n2-n1;
		if (dif==0)
			return n1+"&rarr;"+n2;
		else if (dif<0)
			return "<font color='green'>"+n1+"&rarr;"+n2+"</font>";
		else
			return "<font color='red'>"+n1+"&rarr;"+n2+"</font>";
	}
	
	public static String dispNumber(String prefix, double number) {
		if (number>0) return "<font color='red'>"+prefix+"+"+sDF.format(number)+"</font>";
	    if (number<0) return "<font color='green'>"+prefix+sDF.format(number)+"</font>";
	    return prefix+"0";
	}
	
	public static String dispNumberShort(boolean rem, double n1, double n2) {
		return dispNumberShort(rem,"",n1,n2);
	}
	
	public static String dispNumberShort(boolean rem, String prefix, double n1, double n2) {
		if (n1==0 && n2==0) return "";
		if (rem) return dispNumber(prefix,-n1);
		double dif = n2-n1;
		if (dif==0)
			return prefix+sDF.format(n1)+"&rarr;"+sDF.format(n2);
		else if (dif<0)
			return "<font color='green'>"+prefix+sDF.format(n1)+"&rarr;"+sDF.format(n2)+"</font>";
		else
			return "<font color='red'>"+prefix+sDF.format(n1)+"&rarr;"+sDF.format(n2)+"</font>";
	}
	
	public static String dispNumber(int n1, int n2) {
		return dispNumber(n1-n2)+" ("+n2+(n1==n2?"":" &rarr; "+n1)+")";
	}
	
	public static String dispNumber(double n1, double n2) {
		return dispNumber(n1-n2)+" ("+sDF.format(n2)+(n1==n2?"":" &rarr; "+sDF.format(n1))+")";
	}
	
	public static String dispNumber(double number) {
		return dispNumber("",number);
	}
}
