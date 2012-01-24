package bz.voter.management.display.panel

import org.zkoss.zkgrails.*
import org.zkoss.zul.Messagebox
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventQueue
import org.zkoss.zk.ui.event.EventQueues
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.Executions

import java.text.DateFormat
import java.util.Locale

import bz.voter.management.Voter
import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class ActivitiesFormComposer extends GrailsComposer {


    def voter
    def activity

    def activityFormWindow

    def createActivityBtn
    def saveActivityBtn
    def activityListbox
    def activityDatebox
    def notesTextbox
    def activityTypeRow
    def activityListRow
    def activityTypeTextbox

    def voterFacade
    def activityFacade

    EventQueue queue

    def afterCompose = { window ->
        if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_OFFICE_STATION')){
            voter = Executions.getCurrent().getArg().voter
            voterFacade.voter = voter
            def activityId = Executions.getCurrent().getArg().activityId
            activity = activityId ? activityFacade.getActivity(activityId) : null

            activityFormWindow.title = activity ? 'Edit Activity' : 'Create New Activity'

            for(activityType in activityFacade.getActivityTypes()){
                activityListbox.append{
                    def selected = activityType.equals(activity?.activityType)  
                    listitem(value: activityType, selected: selected){
                        listcell(label: activityType.name)
                        listcell(label: activityType.id)
                    }
                }
            }
            
        queue = EventQueues.lookup('voterActivity', EventQueues.DESKTOP,true)

        }else{
            ComposerHelper.permissionDeniedBox()
        }
    }


    def onClick_createActivityBtn(){
        activityListRow.setVisible(false)
        activityTypeRow.setVisible(true)
        activityTypeTextbox.setConstraint('no empty')
    }


    def onClick_saveActivityBtn(){
        if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_OFFICE_STATION')){
            
            def activityType = activityTypeTextbox.getValue() ? activityFacade.saveActivity(activityTypeTextbox.getValue()) : activityListbox.getSelectedItem()?.getValue()
            
            def params = [
                voterId:    voter.id,
                activityId: activity?.id,
                activityType:  activityType,
                notes:      notesTextbox.getValue()?.trim(),
                activityDate:   activityDatebox.getValue()
            ]

            println "\nparams: ${params}\n"

            def activityInstance = voterFacade.saveActivity(params)

            if(activityInstance.hasErrors()){
                Messagebox.show(activityInstance.retrieveErrors(),'Activity Message', Messagebox.OK, Messagebox.ERROR)
            }else{
                queue.publish(new Event("onVoterActivity", null ,null))
                Messagebox.show("Activity Saved", "Activity Message", Messagebox.OK, Messagebox.INFORMATION)
                activityFormWindow.detach()
            }
        }else{
            ComposerHelper.permissionDeniedBox()
        }
    }


}
