/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bz.voter.management.importer

import org.grails.plugins.excelimport.ExcelImportUtils
import org.grails.plugins.excelimport.*
import bz.voter.management.*


/**
 *
 * @author BOLGU
 */
class PersonExcelImporter extends AbstractExcelImporter {
    
    static Map CONFIG_PERSON_COLUMN_MAP = [
        sheet: 'Sheet1',
                        startRow: 1,
              columnMap: [
                'D':'lastName',
                'E':'firstName',
                'K':'birthDate',
                'R':'homePhone',
                'S':'comments',
                'F':'Sex',
                'Z':'Address.id'
              ]
                        
    ]
    
    static Map propertyConfigurationMap = [
        lastName: ([expectedType: ExpectedPropertyType.StringType, defaultValue:null] ),
        firstName: ([expectedType: ExpectedPropertyType.StringType, defaultValue:null]),
        homePhone: ([expectedType: ExpectedPropertyType.StringType, defaultValue:null]),
        comments: ([expectedType: ExpectedPropertyType.StringType, defaultValue:null]),
        sex: ([expectedType: ExpectedPropertyType.IntType, defaultValue:0]),
        birthDate: ([expectedType: ExpectedPropertyType.DateJavaType, defaultValue:null]),
        address: ([expectedType: ExpectedPropertyType.IntType, defaultValue:0])
      
    ]
    
    //List bookParamsList = ExcelImportUtils.columns(workbook, CONFIG_BOOK_COLUMN_MAP)
    
    //bookParamsList.each { Map bookParamMap ->
       // new Book (bookParamMap).save()
        
    //}
    public PersonExcelImporter(fileName2) {
        super(fileName2)
    }
    
    List<Map> getPersons() {
        List PersonList = ExcelImportUtils.convertColumnMapConfigManyRows(workbook, CONFIG_PERSON_COLUMN_MAP, null, null, propertyConfigurationMap)
   // List bookList = ExcelImportUtils.convertColumnMapConfigManyRows(workbook, CONFIG_BOOK_COLUMN_MAP)
        return PersonList
    }

	
}

