package bz.voter.management

class Ethnicity implements Serializable{

	String name

    static constraints = {
	 	name(unique:true, blank:false)
    }

	 def beforeValidate(){
	 	name = name?.trim()?.capitalize()
	 }

	 String toString(){
	 	name
	 }

}
