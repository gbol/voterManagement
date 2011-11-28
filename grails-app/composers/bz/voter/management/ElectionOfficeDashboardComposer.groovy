package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

class ElectionOfficeDashboardComposer extends GrailsComposer {
	
	def votesCountBox
	def election

    def afterCompose = { window ->
	 	votesCountBox.getChildren().clear()
		election = Election.get(Executions.getCurrent().getArg().electionId)
	 	Executions.createComponents("pollStationVotesCount.zul", votesCountBox, 
		[id: election.id])
    }
}
