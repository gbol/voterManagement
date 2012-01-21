package bz.voter.management.display.panel

import org.zkoss.zkgrails.*
import org.zkoss.zul.Messagebox
import org.zkoss.zk.ui.Executions

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class BasicInformationPanelComposer extends GrailsComposer {

    def voterFacade
    def utilsFacade
    def basicInformation

    def basicInformationPanel
    def ageValueLabel

    def firstNameTextbox
    def middleNameTextbox
    def lastNameTextbox
    def birthDatebox
    def sexListbox
    def deceasedCheckbox
    def voterEmailAddressTextbox
    def affiliationListbox

    def saveBasicInformationButton

    def voter


    def afterCompose = { window ->
        voter = Executions.getCurrent().getArg().voter
        voterFacade.voter = voter

        basicInformation = voterFacade.getBasicInformation()

        if(basicInformation.alive){
            ageValueLabel.setValue("${basicInformation.age}")
        }else{
            ageValueLabel.setVisible(false)
        }

        for(sex in utilsFacade.listSex()){
            sexListbox.append{
                def selected = (sex.equals(basicInformation.sex))? true : false 
                listitem(value: sex, selected:selected){
                    listcell(label: sex.name)
                    listcell(label: sex.id)
                }
            }
        }


        for(affiliation in utilsFacade.listAffiliations()){
            affiliationListbox.append{
                def selected = affiliation.equals(basicInformation.affiliation) ? true : false
                listitem(value: affiliation, selected: selected){
                    listcell(label: affiliation.name)
                    listcell(label: affiliation.id)
                }
            }
        }

        deceasedCheckbox.checked = basicInformation.alive ? false : true

    }


    def onClick_saveBasicInformationButton(){
	    if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_OFFICE_STATION')){
            def params = [
                firstName:      firstNameTextbox.getValue()?.trim(),
                middleName:     middleNameTextbox.getValue()?.trim(),
                lastName:       lastNameTextbox.getValue()?.trim(),
                birthDate:      birthDatebox.getValue(),
                sex:            sexListbox.getSelectedItem()?.getValue(),
                emailAddress:   voterEmailAddressTextbox.getValue()?.trim(),
                affiliation:    affiliationListbox.getSelectedItem()?.getValue(),
                alive:          deceasedCheckbox.isChecked() ? false : true

            ]


            def person = voterFacade.saveBasicInformation(params)
            if(person.hasErrors()){
                Messagebox.show(person.retrieveErrors(), 'Error Saving', Messagebox.OK,
                    Messagebox.ERROR)
            }else{
                Messagebox.show('Information Saved Successfully!', 'Save Message', Messagebox.OK,
                    Messagebox.INFORMATION)
            }

            
        }else{
            ComposerHelper.permissionDeniedBox()
        }
    }
}
