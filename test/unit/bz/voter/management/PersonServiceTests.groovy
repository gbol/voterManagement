package bz.voter.management

import grails.test.*

class PersonServiceTests extends BaseUnitTestCase {

	def personService
	def municipality

    protected void setUp() {
        super.setUp()
		  mockDomain(Municipality, [municipality])
		  mockDomain(District)
		  mockDomain(Address)
		  mockDomain(Sex)
		  mockDomain(Person)
		  mockForConstraintsTests(Person)
		  mockForConstraintsTests(Address)
		  mockLogging(PersonService.class,true)

		  new District(name:'Cayo',code:'CY').save()
		  new Sex(name:'Male',code:'M').save()
		  municipality = new Municipality(name: 'Belmopan', district: District.findByCode('CY')).save()
		  //municipality = Municipality.findByName('Belmopan')
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

		def addressParams = [
			houseNumber: '0',
			street:  'N/A',
			municipality: municipality ]

		params.address = addressParams

		personService.messageSource = [getMessage: {errors,locale-> return "message"}]
		personService.addressService = [add : { new Address(houseNumber: '0', street: 'N/A', municipality: municipality).save() } ]

		def person  = personService.add(params)

		assertNotNull person.id

    }

	 
	 void test_Save_New_Person(){
		personService = new PersonService()


		def addressParams = [
			houseNumber: '0',
			street: 'N/A',
			municipality: municipality
		]

	 	def params = [:]
		params.firstName = 'John'
		params.lastName = 'Doe'
		params.birthDate = new Date().parse('dd-MM-yyyy','12-12-1984')
		params.sex = Sex.findByCode('M')
		params.address = addressParams

		personService.messageSource = [getMessage: {errors,locale-> return "message"}]
		personService.addressService = [add : { new Address(houseNumber: '0', street: 'N/A', municipality: municipality).save() } ]

		def person  = personService.save(params)

		assertNotNull person.id
	 }


	 void test_Update_Existing_Person(){
		personService = new PersonService()

		def addressParams = [
			houseNumber: '0',
			street: 'N/A',
			municipality: municipality
		]

	 	def params = [:]
		params.firstName = 'John'
		params.lastName = 'Doe'
		params.birthDate = new Date().parse('dd-MM-yyyy','12-12-1984')
		params.sex = Sex.findByCode('M')
		params.address = addressParams

		personService.messageSource = [getMessage: {errors,locale-> return "message"}]
		personService.addressService = [add : { new Address(houseNumber: '0', street: 'N/A', municipality: municipality).save() } ]

		def person  = personService.add(params)

		assertEquals 'John', person.firstName

		def params2 = [
			person: person,
			firstName: 'Joe'
		]

		def person2 = personService.save(params2)

		assertEquals 'Joe', person2.firstName
	 }

	 void test_Update_Existing_Person_With_Empty_First_Name_Should_Fail(){
		personService = new PersonService()

		def addressParams = [
			houseNumber: '0',
			street: 'N/A',
			municipality: municipality
		]

	 	def params = [:]
		params.firstName = 'John'
		params.lastName = 'Doe'
		params.birthDate = new Date().parse('dd-MM-yyyy','12-12-1984')
		params.sex = Sex.findByCode('M')
		params.address = addressParams

		personService.messageSource = [getMessage: {errors,locale-> return "message"}]
		personService.addressService = [add : { new Address(houseNumber: '0', street: 'N/A', municipality: municipality).save() } ]

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
