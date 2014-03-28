/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.kiskis.mgmt.ui.mongodb;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;
import org.safehaus.kiskis.mgmt.shared.protocol.Disposable;
import org.safehaus.kiskis.mgmt.ui.mongodb.manager.Manager;
import org.safehaus.kiskis.mgmt.ui.mongodb.wizard.Wizard;

/**
 *
 * @author dilshat
 */
public class MongoForm extends CustomComponent implements Disposable {

    private final Wizard wizard;
    private final Manager manager;

    public MongoForm() {
        setSizeFull();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setSizeFull();
        TabSheet mongoSheet = new TabSheet();
        mongoSheet.setStyleName(Runo.TABSHEET_SMALL);
        mongoSheet.setSizeFull();
        manager = new Manager();
        wizard = new Wizard(manager);
        mongoSheet.addTab(wizard.getContent(), "Install");
        mongoSheet.addTab(manager.getContent(), "Manage");
        verticalLayout.addComponent(mongoSheet);
        setCompositionRoot(verticalLayout);
        manager.refreshClustersInfo();
    }

    public void dispose() {
    }

}
