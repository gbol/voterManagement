package bz.voter.management

import org.zkoss.zkgrails.*


class ImportVotersModalComposer extends GrailsComposer {

	def importLabel

    def afterCompose = { window ->
	 	importLabel.setValue("Import voters!")
    }
}
