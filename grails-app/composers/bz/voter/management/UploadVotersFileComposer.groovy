package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*
import org.zkoss.zul.*
import org.zkoss.zk.ui.event.*
import org.zkoss.util.media.*

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder

import org.apache.commons.io.FileUtils

import bz.voter.management.zk.ComposerHelper

class UploadVotersFileComposer extends GrailsComposer {

	def center

	def divisionListbox
	def electionListbox

	def electionListLabel
	def uploadLabel

	def fileUpload

	def division
	def election

	ListModelList divisionModel

	def importFileService

    def afterCompose = { window ->
	 	divisionModel = new ListModelList(Division.list([sort:'name']))
		divisionListbox.setModel(divisionModel)

		fileUpload.setVisible(false)
		electionListbox.setVisible(false)
		electionListLabel.setVisible(false)
		uploadLabel.setVisible(false)
    }

	 def onSelect_divisionListbox(){
		def electionList = Election.list([sort:'year'])
		for(election in electionList){
			electionListbox.append{
				listitem(value: election){
					listcell(label: "${election.year}")
					listcell(label: "${election.electionType}")
					listcell(label: "${election.electionDate?.format('dd-MMM-yyyy')}")
				}
			}
		}
		
		division = divisionListbox.getSelectedItem().getValue()
		electionListLabel.setVisible(true)
		electionListbox.setVisible(true)
	 }


	 def onSelect_electionListbox(){
	 	election = electionListbox.getSelectedItem().getValue()
	 	uploadLabel.setVisible(true)
	 	fileUpload.setVisible(true)
	 }


	 def onUpload_fileUpload(UploadEvent event){
	 	def spreadSheetContentType = [
				'application/vnd.ms-excel',
				'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
				]

		def importFile
		Media media
		FileOutputStream file
		try{
			media = event.getMedia()

			if(media==null){
				Messagebox.show("No document specified!", "Document Exception!", Messagebox.OK,
					Messagebox.ERROR)
			}else{
				if(spreadSheetContentType.contains(media.getContentType())){
					//println "\nStream Data: ${media.getStreamData()}\n"
					InputStream srcFileInputStream =  media.getStreamData()
					file = new FileOutputStream(new File(ConfigurationHolder.config.files.dir + media.getName()))
					
					try{
						byte[] buffer = new byte[1024]
						int bytesRead
						while((bytesRead = srcFileInputStream.read(buffer)) != -1){
							file.write(buffer,0,bytesRead)
						}
					}catch(java.io.IOException e){
						log.error e.stackTrace()
						Messagebox.show("An exception occurred while uploading file!", "Error!",
							Messagebox.OK, Messagebox.EXCLAMATION)
						
					}finally{
						srcFileInputStream.close()
						file.flush()
						file.close()
						if((new File(ConfigurationHolder.config.files.dir + media.getName()))?.exists()){
							println "\nImported File ${media.getName()}"
							println "Importing data\n"
							importFileService.importVoters(division,election,media.getName())
						}

					}
				}else{
					Messagebox.show("Import file must be an excel document", "Wrong File Format", Messagebox.OK,
						Messagebox.EXCLAMATION)
				}
			}
		}catch(InterruptedException e){
			log.error e.stackTrace()
		}

	 }

}
