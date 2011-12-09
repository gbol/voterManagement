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

	def fileUpload

	def division

	ListModelList divisionModel

    def afterCompose = { window ->
	 	divisionModel = new ListModelList(Division.list([sort:'name']))
		divisionListbox.setModel(divisionModel)

		fileUpload.setVisible(false)
    }

	 def onSelect_divisionListbox(){
	 	fileUpload.setVisible(true)
	 }


	 def onUpload_fileUpload(UploadEvent event){
	 	def spreadSheetContentType = [
				'application/vnd.ms-excel',
				'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
				]
		try{
			Media media = event.getMedia()

			if(media==null){
				Messagebox.show("No document specified!", "Document Exception!", Messagebox.OK,
					Messagebox.ERROR)
			}else{
				if(spreadSheetContentType.contains(media.getContentType())){
					InputStream srcFileInputStream =  (InputStream)new ByteArrayInputStream(media.getByteData())
					FileOutputStream file = new FileOutputStream(ConfigurationHolder.config.files.dir + media.getName())
					try{
						file.write(media.getByteData())
					}catch(java.io.IOException e){
						println e.stackTrace()
						Messagebox.show("An exception occurred while uploading file!", "Error!",
							Messagebox.OK, Messagebox.EXCLAMATION)
						
					}finally{
						file.close()
					}
				}else{
					Messagebox.show("Import file must be an excel document", "Wrong File Format", Messagebox.OK,
						Messagebox.EXCLAMATION)
				}
			}
		}catch(InterruptedException e){
			e.printStackTrace()
		}
	 }
}
