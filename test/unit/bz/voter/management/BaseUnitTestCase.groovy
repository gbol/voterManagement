package bz.voter.management

import grails.test.GrailsUnitTestCase
import org.springframework.transaction.TransactionStatus

class BaseUnitTestCase extends GrailsUnitTestCase {

	def statusControls

	protected void setUp(){
		super.setUp()
		statusControls = []
	}

	protected void tearDown(){
		statusControls.each{
			it.verify()
		}

		statusControls.clear()
		super.tearDown()
	}

	def mockForTransaction(Class clazz, boolean expectedRollback = false){
		
		registerMetaClass(clazz)
		def statusControl = mockFor(TransactionStatus)
		statusControls << statusControl
		if(expectedRollback){
			statusControl.demand.setRollbackOnly(1..1){
				println 'set RollbackOnly called'
			}
			def status = statusControl.createMock()
			clazz.metaClass.'static'.withTransaction = {
				Closure callable -> callable.call(status)
			}
		}

		return statusControl
	}
	
}
