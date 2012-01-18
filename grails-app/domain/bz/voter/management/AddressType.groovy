package bz.voter.management

class AddressType implements Serializable {

    String name

    static constraints = {
        name(unique:  true, blank: false)
    }

    def beforeValidate(){
        this.name = name?.capitalize()?.trim()
    }
}
