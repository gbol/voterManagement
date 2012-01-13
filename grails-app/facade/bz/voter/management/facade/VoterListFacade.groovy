package bz.voter.management.facade

import org.zkoss.zkgrails.*

import org.codehaus.groovy.grails.commons.ConfigurationHolder

import bz.voter.management.Division

class VoterListFacade {

    /*VoterList selected

    List<VoterList> getVoterLists() {
        VoterList.list()
    }*/


    Division getSystemDivision(){
        Division.findByName(ConfigurationHolder.config.division)
    }
}
