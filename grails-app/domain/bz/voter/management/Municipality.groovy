package bz.voter.management

class Municipality implements Serializable{

	String name
	District district

	String toString(){
		name
	}

    static constraints = {
    }

	 def beforeValidate(){
	 	name = name?.trim()?.capitalize()
	 }


     public boolean equalsTo(def municipalityInstance){
        this.id == municipalityInstance.id
     }
}
