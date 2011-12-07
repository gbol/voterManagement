package bz.voter.management

class SecRole implements Serializable{

	String authority

	static mapping = {
		cache true
	}

	static constraints = {
		authority blank: false, unique: true
	}
}
