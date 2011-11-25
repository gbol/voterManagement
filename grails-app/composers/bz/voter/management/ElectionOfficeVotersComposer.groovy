package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*
import org.zkoss.zul.*

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils


class ElectionOfficeVotersComposer extends GrailsComposer {

	def springSecurityService
	def voterElectionService

    def afterCompose = { window ->
	 	if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_OFFICE_STATION')){
			Election election = Election.get(Executions.getCurrent().getArg().id)
			def votersElection = VoterElection.findAllByElection(election)
			showVoters(votersElection)
		}else{
			ComposerHelper.permissionDeniedBox()
		}
    }


	 def showVoters(def votersElection){
	 	println "\n\n Voters in this elecion:\n\n"
		votersElection.each{voterElection->
			println voterElection.voter.person.firstName
		}
	 }
}
