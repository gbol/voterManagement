package bz.voter.management.zk

import org.zkoss.zul.Label
import org.zkoss.zul.Row
import org.zkoss.zul.RowRenderer
import org.zkoss.zul.Window
import org.zkoss.zul.Button
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.Executions

import org.zkoss.zkgrails.*

import bz.voter.management.Voter

public class VoterRenderer implements RowRenderer{


    def grid
    def panel
    def centerPanel

	public void render(Row row, java.lang.Object data){
		Voter voter = (Voter) data

        grid = row.getParent()
        panel = grid.getParent().getParent()
        centerPanel = panel.getParent().getParent()

		Button manageButton = new Button("Manage")


        manageButton.addEventListener("onClick", new EventListener(){
            public void onEvent(Event event) throws Exception{
                centerPanel.getChildren().clear()
                Executions.createComponents("/bz/voter/management/display/panel/voterMainPanel.zul",
                    centerPanel, [voter: voter])
            }
        })

		row.getChildren().add(new Label("${voter.registrationNumber}"))
		row.getChildren().add(new Label("${voter.person.lastName}"))
		row.getChildren().add(new Label("${voter.person.firstName}"))
		row.getChildren().add(new Label("${voter.person.age}"))
		row.getChildren().add(new Label("${voter.person.sex}"))
		row.getChildren().add(new Label("${voter.person.homePhone}"))
		row.getChildren().add(new Label("${voter.person.cellPhone}"))
		row.getChildren().add(new Label("${voter.affiliation}"))
		row.getChildren().add(manageButton)
	}

}
