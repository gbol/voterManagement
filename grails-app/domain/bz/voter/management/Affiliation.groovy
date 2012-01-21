package bz.voter.management

class Affiliation implements Serializable{
	String name

	String toString(){
		name
	}

    static constraints = {
	 	name(unique:true,blank:false)
    }


    public boolean equals(other){
        if(!(other instanceof Affiliation)){
            return false
        }


        other.id == this.id
    }

	 def beforeValidate(){
	 	name = name?.trim()?.toUpperCase()
	}

}
