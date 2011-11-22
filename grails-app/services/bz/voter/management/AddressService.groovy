package bz.voter.management

class AddressService {

    static transactional = true

	//def messageSource

	/**
	Add a new address
	@param params is a map with the fields required to create an address:
	1. houseNumber
	2. street
	3. municipality
	**/
    def add(def params) {
		 save(params)
    }


	/**
	@param params is map with the required values to save an address:
	1. houseNumber
	2. street
	3. municipality
	4. address
	@return Instance of address.
	**/
	 def save(def params){
		def addressInstance

		if(params.address?.id){
			addressInstance = params.address
		}else{
			addressInstance = new Address()
		}

		try{
			addressInstance.houseNumber = params.houseNumber ?: addressInstance.houseNumber
			addressInstance.street = params.street ?: addressInstance.street
			addressInstance.municipality = (Municipality)params.municipality ?: addressInstance.municipality
			addressInstance.validate()
		}catch(e){
			log.error "An exception was thrown : ${e.printStackTrace()}"
			return addressInstance
		}


		if(addressInstance.hasErrors()){
			log.error "An error occurred while saving address."
			for(error in addressInstance.errors.allErrors){
				log.error error
			}
		}else{
			addressInstance.save()
			log.info 'Saved address: ' + addressInstance
		}


		return addressInstance
		
	 }

}
