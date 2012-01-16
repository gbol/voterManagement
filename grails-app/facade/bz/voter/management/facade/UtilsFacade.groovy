package bz.voter.management.facade

import org.zkoss.zkgrails.*

import bz.voter.management.Sex
import bz.voter.management.District
import bz.voter.management.Municipality

class UtilsFacade {

    def sessionFactory


    /**
    Lists sex values.
    @return List of sex values.
    **/
    List<Sex> listSex() {
        Sex.list()
    }


    /**
    Lists all districts
    @return List of districts
    **/
    List<District> listDistricts(){
        District.list([sort:'name'])
    }


    /**
    List all municipalities
    @return List of municipalities
    **/
    List<Municipality> listMunicipalities(){
        Municipality.list([sort:'name'])
    }


    /**
    List all municipalities in a specific district.
    @param district is the district whose municipalities we want to list.
    @return List of municipalities
    **/
    List<Municipality> listMunicipalitiesByDistrict(district){
        Municipality.findAllByDistrict(district,[sort:'name'])
    }

}
