package bz.voter.management.zk

import org.zkoss.zul.Label
import org.zkoss.zul.Row
import org.zkoss.zul.RowRenderer
import org.zkoss.zul.Window
import org.zkoss.zul.Button
import org.zkoss.zul.Messagebox
import org.zkoss.zul.Checkbox
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.Executions

import org.zkoss.zkgrails.*

import bz.voter.management.VoterElection

public class PollStationVoterRenderer implements RowRenderer{

    public void render(Row row, java.lang.Object data){
        VoterElection _voterElection = (VoterElection) data
		def voted = _voterElection.voted ? true : false
		def backgroundColor = voted ? "red" : "white"

        Button saveButton = new Button('Save')
        saveButton.addEventListener("onClick", new EventListener(){
            public void onEvent(Event evt) throws Exception{
			    _voterElection.save(flush:true)
			    Messagebox.show("Saved Successfuly!", "Voter", 
			        Messagebox.OK, Messagebox.INFORMATION)
			    if(_voterElection.voted){
			        evt.getTarget().getParent().setStyle("background-color:red")
			    }else{
			        evt.getTarget().getParent().setStyle("background-color: white")
		        }
            }
        })

        Button detailsButton = new Button('Details')
        detailsButton.addEventListener("onClick", new EventListener(){
            public void onEvent(Event event) throws Exception{
				final Window win = (Window) Executions.createComponents("/bz/voter/management/voterGeneralInformation.zul", 
					null, [voterElection: _voterElection])
				win.doModal()
				win.setPosition("top,center")
            }
        })

        Checkbox votedCheckbox = new Checkbox()
        votedCheckbox.addEventListener("onCheck", new EventListener(){
            public void onEvent(Event event) throws Exception{
			    if(_voterElection.voted){
		            _voterElection.voted = false
			        _voterElection.voteTime = null
			    }else{
			        _voterElection.voted = true
		   	        _voterElection.voteTime = new Date()
			    }
            }
        })

        row.setStyle("background-color: ${backgroundColor}")
        row.getChildren().add(new Label("${_voterElection.voter.registrationNumber}"))
        row.getChildren().add(new Label("${_voterElection.voter.registrationDate.format('dd-MMM-yyyy')}"))
        row.getChildren().add(new Label("${_voterElection.voter.person.lastName}"))
        row.getChildren().add(new Label("${_voterElection.voter.person.firstName}"))
        row.getChildren().add(new Label("${_voterElection.voter.person.address.houseNumber}"))
        row.getChildren().add(new Label("${_voterElection.voter.person.address.street}"))
        row.getChildren().add(new Label("${_voterElection.voter.person.sex.code}"))
        row.getChildren().add(new Label("${_voterElection.voter.person.age}"))
        row.getChildren().add(new Label("${_voterElection.voter.person.birthDate.format('dd-MMM-yyyy')}"))
        row.getChildren().add(new Label("${_voterElection.voter.pollStation.pollNumber}"))
        row.getChildren().add(votedCheckbox)
        row.getChildren().add(saveButton)
        row.getChildren().add(detailsButton)

    }

}
