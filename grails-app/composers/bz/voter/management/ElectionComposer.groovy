package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zul.*

class ElectionComposer extends GrailsComposer {
	
	def addElectionButton
	def cancelElectionButton
	def saveElectionButton

	def electionFormPanel

	def electionIdLabel

	def yearTextbox

	def electionTypeListbox

	def electionsListRows


	def errorMessages
	def messageSource

	def election

    def afterCompose = { window ->
    }


}
