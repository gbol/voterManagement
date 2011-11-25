package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils


class ElectionOfficeNavigationComposer extends GrailsComposer {

	def votersButton
	def dashboardButton

	def electionOfficeCenter

    def afterCompose = { window ->
    }

}
