package bz.voter.management

class Sex implements Serializable{

	String name
	String code

	String toString(){
		name
	}

    static constraints = {
	 	name(nullable:false,unique:true)
		code(nullable:false,unique:true)
    }

	 def beforeValidate(){
	 	this.name = this.name?.trim()?.capitalize()
	 	this.code = this.code?.trim()?.toUpperCase()
	 }

     public boolean equals(other){
        if(!(other instanceof Sex)){
            return false
        }
        this.id == other.id
     }
}
