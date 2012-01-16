package bz.voter.management.display.panel

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.Executions
import org.zkoss.zul.Messagebox

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class AddressPanelComposer extends GrailsComposer {

    def voterFacade
    def utilsFacade
    def voter

    def houseNumberTextbox
    def streetTextbox
    def districtListbox
    def municipalityListbox
    def saveAddressButton

    def address

    def addressDisplayPanel

    def afterCompose = { window ->
        voter = Executions.getCurrent().getArg().voter

        address = voterFacade.getAddress()

        for(district in utilsFacade.listDistricts()){
            districtListbox.append{
                def selected = address.district.equalsTo(district) ?: false
                listitem(value: district, selected: selected){
                    listcell(label: district.name )
                    listcell(label: district.id )
                }
            }
        }

        setupMunicipalityListbox(address.district)
    }


    def onSelect_districtListbox(){
        setupMunicipalityListbox(districtListbox.getSelectedItem().getValue())
    }


    def onClick_saveAddressButton(){
	    if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_OFFICE_STATION')){
            def params = [
                houseNumber:        houseNumberTextbox.getValue()?.trim(),
                street:             streetTextbox.getValue()?.trim(),
                municipality:       municipalityListbox.getSelectedItem()?.getValue()
            ]
            
            println "\nSaving address: ${params}"

            def addressInstance = voterFacade.saveAddress(params)

            if(addressInstance.hasErrors()){
                Messagebox.show(addressInstance.retrieveErrors(), 'Error Saving Address', 
                    Messagebox.OK, Messagebox.ERROR)
            }else{
                Messagebox.show("Address Saved!", "Save Message", Messagebox.OK,
                    Messagebox.INFORMATION)
            }
        }else{
            ComposerHelper.permissionDeniedBox()
        }
    
    }


    def setupMunicipalityListbox(district){
        municipalityListbox.getChildren().clear()
        for(municipality in utilsFacade.listMunicipalitiesByDistrict(district)){
            municipalityListbox.append{
                def selected = address.municipality.equalsTo(municipality) ?: false
                listitem(value: municipality, selected: selected){
                    listcell(label: municipality.name)
                    listcell(label: municipality.id)
                }
            }
        }
    }

}
