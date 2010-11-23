/*
 * RHQ Management Platform
 * Copyright (C) 2005-2010 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.rhq.enterprise.gui.coregui.client.inventory.groups;

import static org.rhq.enterprise.gui.coregui.client.inventory.groups.ResourceGroupDataSourceField.CATEGORY;
import static org.rhq.enterprise.gui.coregui.client.inventory.groups.ResourceGroupDataSourceField.DESCRIPTION;
import static org.rhq.enterprise.gui.coregui.client.inventory.groups.ResourceGroupDataSourceField.NAME;
import static org.rhq.enterprise.gui.coregui.client.inventory.groups.ResourceGroupDataSourceField.PLUGIN;
import static org.rhq.enterprise.gui.coregui.client.inventory.groups.ResourceGroupDataSourceField.TYPE;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AutoFitWidthApproach;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;

import org.rhq.enterprise.gui.coregui.client.CoreGUI;
import org.rhq.enterprise.gui.coregui.client.LinkManager;
import org.rhq.enterprise.gui.coregui.client.components.table.AbstractTableAction;
import org.rhq.enterprise.gui.coregui.client.components.table.Table;
import org.rhq.enterprise.gui.coregui.client.components.table.TableActionEnablement;
import org.rhq.enterprise.gui.coregui.client.gwt.GWTServiceLookup;
import org.rhq.enterprise.gui.coregui.client.gwt.ResourceGroupGWTServiceAsync;
import org.rhq.enterprise.gui.coregui.client.inventory.groups.wizard.GroupCreateWizard;
import org.rhq.enterprise.gui.coregui.client.util.message.Message;
import org.rhq.enterprise.gui.coregui.client.util.message.Message.Severity;
import org.rhq.enterprise.gui.coregui.client.util.selenium.SeleniumUtility;

/**
 * @author Greg Hinkle
 * @author Joseph Marques
 */
public class ResourceGroupListView extends Table {

    private static final String DEFAULT_TITLE = MSG.view_inventory_groups_resourceGroups();

    public ResourceGroupListView(String locatorId) {
        this(locatorId, DEFAULT_TITLE);
    }

    public ResourceGroupListView(String locatorId, String title) {
        this(locatorId, null, title);
    }

    public ResourceGroupListView(String locatorId, Criteria criteria, String title, String... headerIcons) {
        super(locatorId, title, criteria);

        for (String headerIcon : headerIcons) {
            addHeaderIcon(headerIcon);
        }

        final ResourceGroupCompositeDataSource datasource = ResourceGroupCompositeDataSource.getInstance();
        setDataSource(datasource);
    }

    @Override
    protected void configureTable() {
        ListGridField nameField = new ListGridField(NAME.propertyName(), NAME.title());
        nameField.setCellFormatter(new CellFormatter() {
            public String format(Object value, ListGridRecord record, int i, int i1) {
                String groupId = record.getAttribute("id");
                String groupUrl = LinkManager.getResourceGroupLink(Integer.valueOf(groupId));
                return SeleniumUtility.getLocatableHref(groupUrl, value.toString(), null);
            }
        });

        ListGridField idField = new ListGridField("id", MSG.common_title_id());
        ListGridField descriptionField = new ListGridField(DESCRIPTION.propertyName(), DESCRIPTION.title());
        ListGridField typeNameField = new ListGridField(TYPE.propertyName(), TYPE.title());
        ListGridField pluginNameField = new ListGridField(PLUGIN.propertyName(), PLUGIN.title());
        ListGridField categoryField = new ListGridField(CATEGORY.propertyName(), CATEGORY.title());

        ListGridField availabilityChildrenField = new ListGridField("availabilityChildren", MSG
            .view_inventory_groups_children());
        availabilityChildrenField.setWrap(false);
        availabilityChildrenField.setAlign(Alignment.CENTER);

        ListGridField availabilityDescendantsField = new ListGridField("availabilityDescendents", MSG
            .view_inventory_groups_descendants());
        availabilityDescendantsField.setWrap(false);
        availabilityDescendantsField.setAlign(Alignment.CENTER);

        nameField.setWidth("50%");
        descriptionField.setWidth("50%");
        typeNameField.setWidth(75);
        pluginNameField.setAutoFitWidth(true);
        pluginNameField.setAutoFitWidthApproach(AutoFitWidthApproach.BOTH);
        categoryField.setAutoFitWidth(true);
        categoryField.setAutoFitWidthApproach(AutoFitWidthApproach.BOTH);
        availabilityChildrenField.setAutoFitWidth(true);
        availabilityChildrenField.setAutoFitWidthApproach(AutoFitWidthApproach.BOTH);
        availabilityDescendantsField.setAutoFitWidth(true);
        availabilityDescendantsField.setAutoFitWidthApproach(AutoFitWidthApproach.BOTH);

        setListGridFields(false, idField, nameField, descriptionField, typeNameField, pluginNameField, categoryField,
            availabilityChildrenField, availabilityDescendantsField);

        addTableAction(extendLocatorId("Delete"), MSG.common_button_delete(), MSG.common_msg_areYouSure(),
            new AbstractTableAction(TableActionEnablement.ANY) {
                public void executeAction(ListGridRecord[] selections, Object actionValue) {
                    int[] groupIds = new int[selections.length];
                    int index = 0;
                    for (ListGridRecord selection : selections) {
                        groupIds[index++] = selection.getAttributeAsInt("id");
                    }
                    ResourceGroupGWTServiceAsync resourceGroupManager = GWTServiceLookup.getResourceGroupService();

                    resourceGroupManager.deleteResourceGroups(groupIds, new AsyncCallback<Void>() {
                        public void onFailure(Throwable caught) {
                            CoreGUI.getErrorHandler().handleError(MSG.view_inventory_groups_deleteFailed(), caught);
                        }

                        public void onSuccess(Void result) {
                            CoreGUI.getMessageCenter().notify(
                                new Message(MSG.view_inventory_groups_deleteSuccessful(), Severity.Info));

                            ResourceGroupListView.this.refresh();
                        }
                    });
                }
            });

        addTableAction(extendLocatorId("New"), MSG.common_button_new(), new AbstractTableAction() {
            public void executeAction(ListGridRecord[] selection, Object actionValue) {
                new GroupCreateWizard(ResourceGroupListView.this).startWizard();
            }
        });
        //adding cell double click handler
        getListGrid().addCellDoubleClickHandler(new CellDoubleClickHandler() {
            @Override
            public void onCellDoubleClick(CellDoubleClickEvent event) {
                CoreGUI.goToView("ResourceGroup/" + event.getRecord().getAttribute("id"));
            }
        });

    }

    // -------- Static Utility loaders ------------

    public static ResourceGroupListView getGroupsOf(String locatorId, int explicitResourceId) {
        return new ResourceGroupListView(locatorId, new Criteria("explicitResourceId", String
            .valueOf(explicitResourceId)), MSG.view_inventory_groups_resourceGroups());
    }

}