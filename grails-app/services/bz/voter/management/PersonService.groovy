package bz.voter.management

class PersonService {

    static transactional = true

	 def messageSource

	/**
	This adds a new instance of person. It forwards the request to save(params).
	**/
    def add(def params) {
	 	save(params)
    }


	/**
	Saves an instance of person
	@param A map of values that match the fields in Person and Address:
    <ul>
	    <li>firstName</li>
	    <li>middleName</li>
	    <li>lastName</li>
	    <li>dateOfBirth</li>
	    <li>sex</li>
	    <li>ethnicity</li>
    </ul>
	**/
	 def save(def params) {
	 	def personInstance 
		def errorMessages


		if(params.person?.id){
			personInstance = params.person
		}else{
			personInstance = new Person()
		}

	    personInstance.firstName = params.firstName ?: personInstance.firstName
		personInstance.lastName = params.lastName ?: personInstance.lastName
		personInstance.middleName = params.middleName ?: personInstance.middleName
		personInstance.birthDate = params.birthDate ?: personInstance.birthDate
		personInstance.sex = params.sex ?: personInstance.sex
        personInstance.emailAddress = params.emailAddress ?: personInstance.emailAddress

		personInstance.validate()

		if(personInstance.hasErrors()){
			for(error in personInstance.errors.allErrors){
				log.error error
			}

		}else{
			personInstance.save()
		}

		return personInstance
	 }


}
