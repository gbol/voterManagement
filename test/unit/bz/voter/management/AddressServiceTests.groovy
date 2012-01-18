package bz.voter.management

import grails.test.*

class AddressServiceTests extends GrailsUnitTestCase {

	def addressService
	def municipality
    def addressType
    def person
    def sex

    protected void setUp() {
        super.setUp()

		  mockDomain(District)
		  mockDomain(Municipality, [municipality])
		  mockDomain(Address)
		  mockDomain(AddressType)
		  mockForConstraintsTests(Address)
          mockDomain(Person)
          mockDomain(Sex,[sex])
          addressType = new AddressType(name:'Registration').save()
          sex = new Sex(name: 'Male', code:'M').save()
		  mockLogging(AddressService.class, true)

		  new District(name: 'Cayo', code: 'CY').save()
		  municipality = new Municipality(name: 'Belmopan', district: District.findByCode('CY')).save()
          person =  new Person(
                                firstName:      'FirstName',
                                lastName:       'LastName',
                                birthDate:      new Date().parse('dd-MM-yyyy','01-01-1970'),
                                sex:            sex,
                                alive:          true
                            ).save()

    }

    protected void tearDown() {
        super.tearDown()
    }

    void test_Add_New_Address() {
	 	addressService = new AddressService()

		def params = [:]
		params.houseNumber = '0'
		params.street = 'Stret'
		params.municipality = municipality
        params.addressType = addressType
        params.person = person

		def address = addressService.add(params)

		assertNotNull address.id

    }


	 void test_Add_New_Address_Without_Municipality_Fails(){
	 	def addressService = new AddressService()

		def params  = [
			houseNumber: '0',
			street: 'Street']


		def address = addressService.add(params)

		assertNotNull address.errors
	 }


	 void test_Save_A_New_Address(){
	 	def addressService = new AddressService()

		def params = [
			houseNumber:    '0',
			street:         'Street',
            addressType:    addressType,
            person:         person,
			municipality:   municipality]
		
		def address = addressService.save(params)

		assertNotNull address.id
	 }

	void test_Update_Existing_Address(){
		def addressInstance = new Address(
			houseNumber:    '0', 
            street:         'N/A',
            addressType:    addressType,
            person:         person,
			municipality:   municipality).save()
		
		def addressService = new AddressService()

		def params = [
			address: addressInstance,
            addressType: addressType,
			houseNumber: '36',
			street: 'Orchid Garden Street',
			municipality: municipality
		]

		def address = addressService.save(params)

		assertEquals '36', address.houseNumber
		assertEquals 'Orchid Garden Street', address.street
		assertEquals addressInstance.id, address.id

	}


	void test_Update_Existing_Address_With_Empty_String_Should_Fail(){
		def addressInstance = new Address(
            addressType: addressType,
			houseNumber: '0', 
            street: 'N/A/',
            person:     person,
			municipality: municipality).save()
		
		def addressService = new AddressService()

		def params = [
			address:    addressInstance,
            addressType:    addressType,
			houseNumber:    '36',
			street: ' '
		]

		def address = addressService.save(params)

		assertEquals '1 errors', address.errors.toString().substring(61,69) 
	}

}
