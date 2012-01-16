package bz.voter.management

class Relation implements Serializable{

    String name

    static constraints = {
        name(unique: true, blank:false)
    }

    def beforeValidate(){
        this.name = name?.capitalize()?.trim()
    }
}
