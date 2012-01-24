package bz.voter.management

class ActivityType implements Serializable{

    String name

    String toString(){
        name
    }


    public boolean equals(other){
        if(!(other instanceof ActivityType)){
            return false
        }

        other.id == this.id
    }

    static constraints = {
        name(blank: false, unique: true)
    }


    def beforeValidate(){
        this.name = name?.capitalize()?.trim()
    }
}
