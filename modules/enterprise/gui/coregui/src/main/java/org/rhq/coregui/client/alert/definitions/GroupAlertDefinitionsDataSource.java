/*
 * RHQ Management Platform
 * Copyright (C) 2005-2010 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation, and/or the GNU Lesser
 * General Public License, version 2.1, also as published by the Free
 * Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License and the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package org.rhq.coregui.client.alert.definitions;

import java.util.Map;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;

import org.rhq.core.domain.criteria.AlertDefinitionCriteria;
import org.rhq.core.domain.resource.group.ResourceGroup;
import org.rhq.core.domain.util.PageControl;

/**
 * @author John Mazzitelli
 */
public class GroupAlertDefinitionsDataSource extends AbstractAlertDefinitionsDataSource {

    private ResourceGroup resourceGroup;

    public GroupAlertDefinitionsDataSource(ResourceGroup group) {
        super();
        this.resourceGroup = group;
    }

    @Override
    protected AlertDefinitionCriteria getFetchCriteria(DSRequest request) {
        AlertDefinitionCriteria criteria = new AlertDefinitionCriteria();

        Criteria requestCriteria = request.getCriteria();
        if (requestCriteria != null) {
            Map values = requestCriteria.getValues();
            for (Object key : values.keySet()) {
                String fieldName = (String) key;
                if (fieldName.equals(GroupAlertDefinitionsView.CRITERIA_GROUP_ID)) {
                    Integer resourceId = (Integer) values.get(fieldName);
                    criteria.addFilterResourceGroupIds(resourceId);
                }
            }
        }

        return criteria;
    }

    @Override
    protected AlertDefinitionCriteria getSimpleCriteriaForAll() {
        AlertDefinitionCriteria criteria = new AlertDefinitionCriteria();
        criteria.addFilterResourceGroupIds(Integer.valueOf(this.resourceGroup.getId()));
        criteria.setPageControl(PageControl.getUnlimitedInstance());
        return criteria;
    }
}
