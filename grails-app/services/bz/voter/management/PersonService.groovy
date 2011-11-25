package bz.voter.management

class PersonService {

    static transactional = true


	 def messageSource
	 def addressService


	/**
	This adds a new instance of person. It forwards the request to save(params).
	**/
    def add(def params) {
	 	save(params)
    }


	/**
	Saves an instance of person
	@param A map of values that match the fields in Person and Address:
	1. firstName
	2. middleName
	3. lastName
	4. dateOfBirth
	5. sex
	6. ethnicity
	7. homePhone
	8. workPhone
	9. cellPhone
	10. address : this is a map with the following keys: houseNumber, street, municipality
	**/
	 def save(def params) {
	 	def personInstance 
		def addressInstance

		if(params.person?.id){
			personInstance = params.person
		}else{
			personInstance = new Person()
		}

		if(params.address && params.address.id){
			addressInstance = addressService.save(params.address)
		}else if(params.address){
			addressInstance = addressService.add(params.address)
		}else{
			addressInstance = personInstance.address ?: null
		}

		if(addressInstance?.hasErrors()){
			for(error in addressInstance.errors.allErrors()){
				log.error error
				errorMessages = errorMessages + "\n" + (messageSource.getMessage(error,null))
			}

			personInstance.errors.rejectValue("address","address.error", errorMessages)
		}else{

			personInstance.firstName = params.firstName ?: personInstance.firstName
			personInstance.lastName = params.lastName ?: personInstance.lastName
			personInstance.middleName = params.middleName ?: personInstance.middleName
			personInstance.birthDate = params.birthDate ?: personInstance.birthDate
			personInstance.sex = params.sex ?: personInstance.sex
			personInstance.address = addressInstance?.errors ? addressInstance : personInstance.address
			personInstance.comments = params.comments ?: personInstance.comments
			personInstance.workPhone = params.workPhone ?: personInstance.workPhone
			personInstance.cellPhone = params.cellPhone ?: personInstance.cellPhone
			personInstance.homePhone = params.homePhone ?: personInstance.homePhone

			personInstance.validate()

			if(personInstance.hasErrors()){
				for(error in personInstance.errors.allErrors){
					log.error error
				}

			}else{
				personInstance.save()
			}
		}

		return personInstance
	 }
}
