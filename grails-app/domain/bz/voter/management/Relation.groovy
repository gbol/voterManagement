package bz.voter.management

class Relation implements Serializable{

    String name

    String toString() { name }

    static constraints = {
        name(unique: true, blank:false)
    }

    def beforeValidate(){
        this.name = name?.capitalize()?.trim()
    }


    public boolean equals(other){
        if(!(other instanceof Relation)){
            return false
        }

        this.id == other.id
    }
}
