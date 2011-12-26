// Place your Spring DSL code here
beans = {
	addressService(bz.voter.management.AddressService)
	voterService(bz.voter.management.VoterService)
	divisionVotersPagingListModel(org.zkoss.zklargelivelist.model.DivisionVotersPagingListModel){
		voterService = ref("voterService")
	}
}
