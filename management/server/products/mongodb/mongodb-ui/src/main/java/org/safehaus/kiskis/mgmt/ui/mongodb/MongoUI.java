/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.kiskis.mgmt.ui.mongodb;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.safehaus.kiskis.mgmt.api.dbmanager.DbManager;
import org.safehaus.kiskis.mgmt.api.mongodb.Mongo;
import org.safehaus.kiskis.mgmt.server.ui.MgmtApplication;
import org.safehaus.kiskis.mgmt.server.ui.services.MainUISelectedTabChangeListener;
import org.safehaus.kiskis.mgmt.server.ui.services.Module;
import org.safehaus.kiskis.mgmt.shared.protocol.Disposable;
import org.safehaus.kiskis.mgmt.ui.mongodb.manager.Manager;
import org.safehaus.kiskis.mgmt.ui.mongodb.tracker.Tracker;
import org.safehaus.kiskis.mgmt.ui.mongodb.window.ProgressWindow;
import org.safehaus.kiskis.mgmt.ui.mongodb.wizard.Wizard;

/**
 *
 * @author dilshat
 */
public class MongoUI implements Module {

    public static final String MODULE_NAME = "Mongo";
    private static Mongo mongoManager;
    private static DbManager dbManager;
    private static ExecutorService executor;

    public static Mongo getMongoManager() {
        return mongoManager;
    }

    public static DbManager getDbManager() {
        return dbManager;
    }

    public static ExecutorService getExecutor() {
        return executor;
    }

    public void setMongoManager(Mongo mongoManager) {
        MongoUI.mongoManager = mongoManager;
    }

    public void setDbManager(DbManager dbManager) {
        MongoUI.dbManager = dbManager;
    }

    public void init() {
        executor = Executors.newCachedThreadPool();
    }

    public void destroy() {
        dbManager = null;
        mongoManager = null;
        executor.shutdown();
    }

    public static class ModuleComponent extends CustomComponent implements Disposable, MainUISelectedTabChangeListener {

        private final Wizard wizard;
        private final Tracker tracker;
        private final Manager manager;
        private final String managerTabName = "Manage";
        private final String trackerTabName = "Track";
        private String mongoSelectedTabCaption;

        public ModuleComponent() {
            setSizeFull();
            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.setSpacing(true);
            verticalLayout.setSizeFull();

            TabSheet mongoSheet = new TabSheet();
            mongoSheet.setStyleName(Runo.TABSHEET_SMALL);
            mongoSheet.setSizeFull();
            tracker = new Tracker();
            manager = new Manager();
            wizard = new Wizard(manager);
            mongoSheet.addTab(wizard.getContent(), "Install");
            mongoSheet.addTab(manager.getContent(), managerTabName);
            mongoSheet.addTab(tracker.getContent(), trackerTabName);

            mongoSheet.addListener(new TabSheet.SelectedTabChangeListener() {

                public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                    TabSheet tabsheet = event.getTabSheet();
                    mongoSelectedTabCaption = tabsheet.getTab(event.getTabSheet().getSelectedTab()).getCaption();
                    if (trackerTabName.equals(mongoSelectedTabCaption)) {
                        tracker.startTracking();
                    } else {
                        tracker.stopTracking();
                    }
//                    if (managerTabName.equals(mongoSelectedTabCaption) && tracker.isRefreshClusters()) {
//                        tracker.setRefreshClusters(false);
//                        manager.refreshClustersInfo();
//                    }
                }
            });

            verticalLayout.addComponent(mongoSheet);

            setCompositionRoot(verticalLayout);

            manager.refreshClustersInfo();

        }

        public void dispose() {
            tracker.stopTracking();
        }

        public void selectedTabChanged(TabSheet.Tab selectedTab) {
            if (MODULE_NAME.equals(selectedTab.getCaption()) && trackerTabName.equals(mongoSelectedTabCaption)) {
                tracker.startTracking();
            } else {
                tracker.stopTracking();
            }
        }

    }

    public static void showProgressWindow(final Manager manager, UUID trackID) {
        ProgressWindow progressWindow = new ProgressWindow(trackID);
        MgmtApplication.addCustomWindow(progressWindow);
        progressWindow.addListener(new Window.CloseListener() {

            @Override
            public void windowClose(Window.CloseEvent e) {
                manager.refreshClustersInfo();

            }
        });
    }

    public String getName() {
        return MODULE_NAME;
    }

    public Component createComponent() {
        return new ModuleComponent();
    }

}
