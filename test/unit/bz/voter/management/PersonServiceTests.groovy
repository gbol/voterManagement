package bz.voter.management

import grails.test.*

class PersonServiceTests extends BaseUnitTestCase {

	def personService
	def municipality
    def addressType

    protected void setUp() {
        super.setUp()
		  mockDomain(Sex)
		  mockDomain(Person)
		  mockForConstraintsTests(Person)
		  mockLogging(PersonService.class,true)

		  new Sex(name:'Male',code:'M').save()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void test_Add_New_Person() {

		personService = new PersonService()

	 	def params = [:]
		params.firstName = 'John'
		params.lastName = 'Doe'
		params.birthDate = new Date().parse('dd-MM-yyyy','12-12-1984')
		params.sex = Sex.findByCode('M')


		personService.messageSource = [getMessage: {errors,locale-> return "message"}]

		def person  = personService.add(params)

		assertNotNull person.id

    }

	 
	 void test_Save_New_Person(){
		personService = new PersonService()

	 	def params = [:]
		params.firstName = 'John'
		params.lastName = 'Doe'
		params.birthDate = new Date().parse('dd-MM-yyyy','12-12-1984')
		params.sex = Sex.findByCode('M')

		personService.messageSource = [getMessage: {errors,locale-> return "message"}]

		def person  = personService.save(params)

		assertNotNull person.id
	 }


	 void test_Update_Existing_Person(){
		personService = new PersonService()

	 	def params = [:]
		params.firstName = 'John'
		params.lastName = 'Doe'
		params.birthDate = new Date().parse('dd-MM-yyyy','12-12-1984')
		params.sex = Sex.findByCode('M')

		personService.messageSource = [getMessage: {errors,locale-> return "message"}]

		def person  = personService.add(params)


		assertEquals 'John', person.firstName

		def params2 = [
			person:     person,
			firstName:  'Joe',
		]

		def person2 = personService.save(params2)

		assertEquals 'Joe', person2.firstName
	 }

	 void test_Update_Existing_Person_With_Empty_First_Name_Should_Fail(){
		personService = new PersonService()


	 	def params = [:]
		params.firstName = 'John'
		params.lastName = 'Doe'
		params.birthDate = new Date().parse('dd-MM-yyyy','12-12-1984')
		params.sex = Sex.findByCode('M')

		personService.messageSource = [getMessage: {errors,locale-> return "message"}]

		def person  = personService.add(params)

		assertEquals 'John', person.firstName

		def params2 = [
			person: person,
			firstName: ' '
		]

		def person2 = personService.save(params2)

		assertEquals '1 errors', person2.errors.toString().substring(61,69) 

	 }

}
