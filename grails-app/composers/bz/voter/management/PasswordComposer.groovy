package bz.voter.management

import org.zkoss.zul.Messagebox
import org.zkoss.zkgrails.*
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class PasswordComposer extends GrailsComposer {

	def springSecurityService

    def user

    def currentPasswordTextbox
    def newPasswordTextbox
    def verifyPasswordTextbox

    def saveBtn

    def afterCompose = { window ->
	 	if(!springSecurityService.isLoggedIn()){
			execution.sendRedirect('/login')

		}else{
            println "\nuser: ${user}"
        }
    }

    
    def onClick_saveBtn(){
        def currentPassword = currentPasswordTextbox.getValue()
        def newPassword = newPasswordTextbox.getValue()
        def verifyPassword = verifyPasswordTextbox.getValue()

        def errors = validatePasswords()

        if(errors.isAllWhitespace()){
            user = SecUser.load(springSecurityService.getCurrentUser().id) //SecUser.get(SpringSecurityUtils.getSubject().getPrinicpal())
            user.password = verifyPassword
            user.save(flush:true)
            Messagebox.show("Password changed!", "Password", Messagebox.OK,
                Messagebox.ERROR)
        }else{
            Messagebox.show(errors, "Error changing password!", Messagebox.OK,
                Messagebox.ERROR)
        }

    }


    def validatePasswords(){
        user = SecUser.load(springSecurityService.getCurrentUser().id) //SecUser.get(SpringSecurityUtils.getSubject().getPrinicpal())
        def errorStr = ""
        if(!springSecurityService.encodePassword(currentPasswordTextbox.getValue()).equals(user.password)){
            errorStr += "Current password is incorrect!"
        }

        if(!newPasswordTextbox.getValue().equals(verifyPasswordTextbox.getValue())){
            errorStr += "The new passwords do not match!" 
        }

        return errorStr
    }


}
