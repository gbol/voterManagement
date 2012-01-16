package bz.voter.management.display.panel

import org.zkoss.zkgrails.*
import org.zkoss.zul.Tab
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.ForwardEvent
import org.zkoss.zk.ui.Executions


import bz.voter.management.Voter
import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class VoterMainPanelComposer extends GrailsComposer {

    def voterMainWindow

    def generalInformationTab
    def registrationInformationTab
    def addressTab
    def contactInformationTab
    def dependentsTab
    def activitiesTab
    def pledgesTab

    def voter
    def voterFacade

    def afterCompose = { window ->
        if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_OFFICE_STATION')){
            voter = Executions.getCurrent().getArg().voter
            Executions.createComponents("/bz/voter/management/display/panel/generalInformationTab.zul",
                generalInformationTab, [voter: voter])
            Executions.createComponents("/bz/voter/management/display/panel/addressPanel.zul",
                addressTab, [voter: voter])
            Executions.createComponents("/bz/voter/management/display/panel/contactInformationTab.zul",
                contactInformationTab, [voter: voter])
            Executions.createComponents("/bz/voter/management/display/panel/registrationInformation.zul",
                registrationInformationTab, [voter: voter])

        }else{
            ComposerHelper.permissionDeniedBox()
        }
    }


    public void onSwitchTab(ForwardEvent forwardEvent) throws InterruptedException{
        final Tab tab = (Tab) forwardEvent.getOrigin().getTarget()

        switch(tab.getLabel()){
            
            case "Dependents":
                dependentsTab.getChildren().clear()

            case "Activities":
                actitivitesTab.getChildren().clear()

            case "Pledges":
                pledgesTab.getChildren().clear()
        }
    }
}
